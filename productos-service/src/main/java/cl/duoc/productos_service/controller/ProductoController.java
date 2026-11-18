package cl.duoc.productos_service.controller;

import cl.duoc.productos_service.exception.ErrorResponse;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.service.ProductoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Productos", description = "CRUD de productos. Los GET devuelven enlaces HATEOAS y los puede usar "
        + "cualquier usuario autenticado; POST, PUT y DELETE requieren rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
@RestController
@RequestMapping("api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    //1
    @Operation(
            summary = "Listar productos",
            description = "Lista todos los productos del sistema. Cada producto incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Productos encontrados",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Lista de productos", value = """
                            {
                              "_embedded": {
                                "productoList": [
                                  {
                                    "id": 1,
                                    "nombre": "Aceite de Oliva Extra Virgen 500ml",
                                    "descripcion": "Aceite de oliva extra virgen prensado en frio, ideal para ensaladas y cocina diaria.",
                                    "precio": 7990,
                                    "categoria": "Despensa",
                                    "destacado": false,
                                    "activo": true,
                                    "_links": {
                                      "self": { "href": "http://localhost:8082/api/v1/productos/1" },
                                      "productos": { "href": "http://localhost:8082/api/v1/productos" }
                                    }
                                  }
                                ]
                              },
                              "_links": {
                                "self": { "href": "http://localhost:8082/api/v1/productos" }
                              }
                            }
                            """)
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        List<EntityModel<Producto>> productos = productoService.findAll().stream()
                .map(this::conEnlaces)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listar()).withSelfRel()));
    }

    //2
    @Operation(
            summary = "Buscar producto por ID",
            description = "Busca un producto a traves del ID (numerico, tipo Long). Incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Producto encontrado", value = """
                            {
                              "id": 1,
                              "nombre": "Aceite de Oliva Extra Virgen 500ml",
                              "descripcion": "Aceite de oliva extra virgen prensado en frio, ideal para ensaladas y cocina diaria.",
                              "precio": 7990,
                              "categoria": "Despensa",
                              "destacado": false,
                              "activo": true,
                              "_links": {
                                "self": { "href": "http://localhost:8082/api/v1/productos/1" },
                                "productos": { "href": "http://localhost:8082/api/v1/productos" }
                              }
                            }
                            """)
            )
    )
    @ApiResponse(responseCode = "404", description = "Producto no encontrado (respuesta sin cuerpo)")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(conEnlaces(producto));
    }

    //3
    @Operation(
            summary = "Registrar un producto",
            description = "Registra un producto en el sistema. Requiere rol ADMIN. "
                    + "Si no se envia destacado queda en false y si no se envia activo queda en true",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class),
                            examples = {
                                    @ExampleObject(name = "Solo datos obligatorios (destacado y activo por defecto)", value = """
                                            {
                                              "nombre": "Barra de Cereal Integral 30g",
                                              "descripcion": "Barra de cereal con avena, miel y frutos secos.",
                                              "precio": 1290,
                                              "categoria": "Snacks"
                                            }
                                            """),
                                    @ExampleObject(name = "Producto destacado", value = """
                                            {
                                              "nombre": "Quinoa Organica 500g",
                                              "descripcion": "Quinoa organica libre de gluten, alta en proteinas y fibra.",
                                              "precio": 5990,
                                              "categoria": "Despensa",
                                              "destacado": true,
                                              "activo": true
                                            }
                                            """)
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Producto creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(name = "Producto creado", value = """
                            {
                              "id": 7,
                              "nombre": "Barra de Cereal Integral 30g",
                              "descripcion": "Barra de cereal con avena, miel y frutos secos.",
                              "precio": 1290,
                              "categoria": "Snacks",
                              "destacado": false,
                              "activo": true
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Campos obligatorios vacios", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Nombre no puede estar vacio., Categoria no puede estar vacia.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Precio negativo", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Precio no puede ser negativo.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
            )
    )
    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN (respuesta sin cuerpo definido)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Producto producto) {
        Producto productoNuevo = productoService.save(producto);
        return new ResponseEntity<>(productoNuevo, HttpStatus.CREATED);
    }

    //4
    @Operation(
            summary = "Actualizar un producto por ID",
            description = "Actualiza un producto a traves del ID. Requiere rol ADMIN",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(name = "Subir el precio del Mix de Frutos Secos", value = """
                                    {
                                      "nombre": "Mix de Frutos Secos 200g",
                                      "descripcion": "Mezcla de nueces, almendras y arandanos sin sal agregada.",
                                      "precio": 3790,
                                      "categoria": "Snacks",
                                      "destacado": false,
                                      "activo": true
                                    }
                                    """)
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(name = "Producto actualizado", value = """
                            {
                              "id": 4,
                              "nombre": "Mix de Frutos Secos 200g",
                              "descripcion": "Mezcla de nueces, almendras y arandanos sin sal agregada.",
                              "precio": 3790,
                              "categoria": "Snacks",
                              "destacado": false,
                              "activo": true
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Datos invalidos", value = """
                            {
                              "status": 400,
                              "error": "Error de validacion",
                              "mensaje": "[Precio no puede ser negativo., Descripcion no puede estar vacia.]",
                              "fechaHora": "2026-11-10T09:00:00"
                            }
                            """)
            )
    )
    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN (respuesta sin cuerpo definido)")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado (respuesta sin cuerpo)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del producto", example = "4") @PathVariable Long id,
            @Valid @RequestBody Producto producto) {
        Producto productoActualizado = productoService.update(id, producto);
        if (productoActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productoActualizado);
    }

    //5
    @Operation(
            summary = "Eliminar un producto por ID",
            description = "Elimina un producto del sistema a traves del ID. Requiere rol ADMIN"
    )
    @ApiResponse(responseCode = "204", description = "Producto eliminado (respuesta sin cuerpo)")
    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN (respuesta sin cuerpo definido)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Envuelve el producto agregando sus enlaces HATEOAS: el propio recurso y la coleccion
    private EntityModel<Producto> conEnlaces(Producto producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).buscarPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listar()).withRel("productos"));
    }
}