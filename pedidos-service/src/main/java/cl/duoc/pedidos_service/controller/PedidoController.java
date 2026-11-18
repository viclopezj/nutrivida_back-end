package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.exception.ErrorResponse;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pedidos", description = "CRUD de pedidos. Los GET devuelven enlaces HATEOAS (incluye enlaces "
        + "al paciente y al producto del pedido); requiere token JWT de cualquier rol")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
@RestController
@RequestMapping("api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    //1
    @Operation(
            summary = "Listar pedidos",
            description = "Lista todos los pedidos del sistema. Cada pedido incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedidos encontrados",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Lista de pedidos", value = """
                            {
                              "_embedded": {
                                "pedidoList": [
                                  {
                                    "id": 1,
                                    "idPaciente": 1,
                                    "idProducto": 1,
                                    "cantidad": 2,
                                    "precioUnitario": 7990,
                                    "total": 15980,
                                    "estado": "pagado",
                                    "fechaPedido": "2026-11-02T10:15:00",
                                    "_links": {
                                      "self": { "href": "http://localhost:8083/api/v1/pedidos/1" },
                                      "pedidos": { "href": "http://localhost:8083/api/v1/pedidos" },
                                      "paciente": { "href": "http://localhost:8081/api/v1/pacientes/1" },
                                      "producto": { "href": "http://localhost:8082/api/v1/productos/1" }
                                    }
                                  }
                                ]
                              },
                              "_links": {
                                "self": { "href": "http://localhost:8083/api/v1/pedidos" }
                              }
                            }
                            """)
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        List<EntityModel<Pedido>> pedidos = pedidoService.findAll().stream()
                .map(this::conEnlaces)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).listar()).withSelfRel()));
    }

    //2
    @Operation(
            summary = "Buscar pedido por ID",
            description = "Busca un pedido a traves del ID (numerico, tipo Long). Incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Pedido encontrado", value = """
                            {
                              "id": 1,
                              "idPaciente": 1,
                              "idProducto": 1,
                              "cantidad": 2,
                              "precioUnitario": 7990,
                              "total": 15980,
                              "estado": "pagado",
                              "fechaPedido": "2026-11-02T10:15:00",
                              "_links": {
                                "self": { "href": "http://localhost:8083/api/v1/pedidos/1" },
                                "pedidos": { "href": "http://localhost:8083/api/v1/pedidos" },
                                "paciente": { "href": "http://localhost:8081/api/v1/pacientes/1" },
                                "producto": { "href": "http://localhost:8082/api/v1/productos/1" }
                              }
                            }
                            """)
            )
    )
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado (respuesta sin cuerpo)")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(conEnlaces(pedido));
    }

    //3
    @Operation(
            summary = "Registrar un pedido",
            description = "Registra un pedido. El total debe ser igual a cantidad x precio unitario. "
                    + "El estado solo acepta 'pendiente' o 'pagado': si no se envia queda 'pendiente'. "
                    + "Si no se envia fechaPedido se usa la fecha y hora actual",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Pedido.class),
                            examples = {
                                    @ExampleObject(name = "Solo datos obligatorios (estado y fecha por defecto)", value = """
                                            {
                                              "idPaciente": 1,
                                              "idProducto": 1,
                                              "cantidad": 2,
                                              "precioUnitario": 7990,
                                              "total": 15980
                                            }
                                            """),
                                    @ExampleObject(name = "Pedido pagado con fecha", value = """
                                            {
                                              "idPaciente": 2,
                                              "idProducto": 3,
                                              "cantidad": 1,
                                              "precioUnitario": 12990,
                                              "total": 12990,
                                              "estado": "pagado",
                                              "fechaPedido": "2026-11-10T09:00:00"
                                            }
                                            """)
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Pedido creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(name = "Pedido creado", value = """
                            {
                              "id": 6,
                              "idPaciente": 1,
                              "idProducto": 1,
                              "cantidad": 2,
                              "precioUnitario": 7990,
                              "total": 15980,
                              "estado": "pendiente",
                              "fechaPedido": "2026-11-10T09:00:00"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos, total distinto de cantidad x precio unitario o estado invalido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Datos invalidos", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Cantidad debe ser mayor a 0., Paciente no puede estar vacio.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Total no cuadra", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Total)",
                                      "mensaje": "El total (15981) debe ser igual a cantidad x precio unitario (15980).",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Estado invalido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Estado)",
                                      "mensaje": "El estado 'cancelado' es invalido. Los estados permitidos son: 'pendiente' o 'pagado'.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
            )
    )
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Pedido pedido) {
        Pedido pedidoNuevo = pedidoService.save(pedido);
        return new ResponseEntity<>(pedidoNuevo, HttpStatus.CREATED);
    }

    //4
    @Operation(
            summary = "Actualizar un pedido por ID",
            description = "Actualiza un pedido a traves del ID aplicando las mismas reglas del registro",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Pedido.class),
                            examples = @ExampleObject(name = "Marcar pedido como pagado", value = """
                                    {
                                      "idPaciente": 1,
                                      "idProducto": 3,
                                      "cantidad": 1,
                                      "precioUnitario": 12990,
                                      "total": 12990,
                                      "estado": "pagado",
                                      "fechaPedido": "2026-11-05T16:40:00"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedido actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(name = "Pedido actualizado", value = """
                            {
                              "id": 2,
                              "idPaciente": 1,
                              "idProducto": 3,
                              "cantidad": 1,
                              "precioUnitario": 12990,
                              "total": 12990,
                              "estado": "pagado",
                              "fechaPedido": "2026-11-05T16:40:00"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos, total distinto de cantidad x precio unitario o estado invalido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Datos invalidos", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Total no puede ser negativo.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Total no cuadra", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Total)",
                                      "mensaje": "El total (13000) debe ser igual a cantidad x precio unitario (12990).",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Estado invalido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Estado)",
                                      "mensaje": "El estado 'enviado' es invalido. Los estados permitidos son: 'pendiente' o 'pagado'.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
            )
    )
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado (respuesta sin cuerpo)")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del pedido", example = "2") @PathVariable Long id,
            @Valid @RequestBody Pedido pedido) {
        Pedido pedidoActualizado = pedidoService.update(id, pedido);
        if (pedidoActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pedidoActualizado);
    }

    //5
    @Operation(
            summary = "Eliminar un pedido por ID",
            description = "Elimina un pedido del sistema a traves del ID"
    )
    @ApiResponse(responseCode = "204", description = "Pedido eliminado (respuesta sin cuerpo)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cuando exista el API Gateway estas URLs se cambian por la del gateway (puerto 8080)
    private static final String URL_PACIENTES = "http://localhost:8081/api/v1/pacientes/";
    private static final String URL_PRODUCTOS = "http://localhost:8082/api/v1/productos/";

    private EntityModel<Pedido> conEnlaces(Pedido pedido) {
        return EntityModel.of(pedido,
                linkTo(methodOn(PedidoController.class).buscarPorId(pedido.getId())).withSelfRel(),
                linkTo(methodOn(PedidoController.class).listar()).withRel("pedidos"),
                Link.of(URL_PACIENTES + pedido.getIdPaciente()).withRel("paciente"),
                Link.of(URL_PRODUCTOS + pedido.getIdProducto()).withRel("producto"));
    }
}