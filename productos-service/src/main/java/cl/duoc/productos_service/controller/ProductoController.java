package cl.duoc.productos_service.controller;

import cl.duoc.productos_service.exception.ErrorResponse;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.service.ProductoService;
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

@Tag(name = "Productos", description = "CRUD de productos")
@RestController
@RequestMapping("api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    //1
    @Operation(
            summary = "Listar productos",
            description = "Metodo que lista todos los productos del sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Productos encontrados",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = Producto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(productoService.findAll());
    }

    //2
    @Operation(
            summary = "Buscar producto por ID",
            description = "Metodo que busca un producto a traves del ID. Este id es de tipo numerico (LONG)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(producto);
    }

    //3
    @Operation(
            summary = "Registrar un producto",
            description = "Metodo que registra un producto en el sistema. "
                    + "Si no se envia destacado queda en false y si no se envia activo queda en true"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Producto creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Producto producto) {
        Producto productoNuevo = productoService.save(producto);
        return new ResponseEntity<>(productoNuevo, HttpStatus.CREATED);
    }

    //4
    @Operation(
            summary = "Actualizar un producto por ID",
            description = "Metodo que actualiza un producto en el sistema a traves del ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id,
            @Valid @RequestBody Producto producto) {
        Producto productoActualizado = productoService.update(id, producto);
        if (productoActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productoActualizado);
    }

    //5
    @Operation(
            summary = "Eliminar un producto por ID",
            description = "Metodo que elimina un producto del sistema a traves del ID"
    )
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}