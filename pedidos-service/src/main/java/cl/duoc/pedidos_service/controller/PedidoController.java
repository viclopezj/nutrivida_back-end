package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.exception.ErrorResponse;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pedidos", description = "CRUD de pedidos")
@RestController
@RequestMapping("api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    //1
    @Operation(
            summary = "Listar pedidos",
            description = "Metodo que lista todos los pedidos del sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedidos encontrados",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = Pedido.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    //2
    @Operation(
            summary = "Buscar pedido por ID",
            description = "Metodo que busca un pedido a traves del ID. Este id es de tipo numerico (LONG)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pedido);
    }

    //3
    @Operation(
            summary = "Registrar un pedido",
            description = "Metodo que registra un pedido. El total debe ser cantidad x precio unitario; "
                    + "si no se envia estado queda 'pendiente' y si no se envia fecha se usa la actual"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Pedido creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos, total incorrecto o estado invalido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
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
            description = "Metodo que actualiza un pedido a traves del ID aplicando las mismas reglas del registro"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pedido actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody Pedido pedido) {
        Pedido pedidoActualizado = pedidoService.update(id, pedido);
        if (pedidoActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pedidoActualizado);
    }

    //5
    @Operation(
            summary = "Eliminar un pedido por ID",
            description = "Metodo que elimina un pedido del sistema a traves del ID"
    )
    @ApiResponse(responseCode = "204", description = "Pedido eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}