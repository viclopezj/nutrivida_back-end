package cl.duoc.productos_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
@Schema(description = "Producto del catalogo de NutriVida")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador generado por el sistema", example = "1")
    private Long id;

    @NotBlank(message = "Nombre no puede estar vacio.")
    @Size(max = 120, message = "Nombre debe tener un maximo de 120 caracteres.")
    @Schema(description = "Nombre del producto", example = "Aceite de Oliva Extra Virgen 500ml")
    private String nombre;

    // La columna es TEXT, por eso no lleva @Size
    @NotBlank(message = "Descripcion no puede estar vacia.")
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripcion del producto",
            example = "Aceite de oliva extra virgen prensado en frio, ideal para ensaladas y cocina diaria.")
    private String descripcion;

    @NotNull(message = "Precio no puede estar vacio.")
    @PositiveOrZero(message = "Precio no puede ser negativo.")
    @Schema(description = "Precio en pesos chilenos", example = "7990")
    private Integer precio;

    @NotBlank(message = "Categoria no puede estar vacia.")
    @Size(max = 30, message = "Categoria debe tener un maximo de 30 caracteres.")
    @Schema(description = "Categoria del producto", example = "Despensa")
    private String categoria;

    // Opcional: si viene vacio, el service usa false
    @Schema(description = "Opcional. Marca el producto como Best Seller; si no se envia queda en false",
            example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean destacado;

    // Opcional: si viene vacio, el service usa true
    @Schema(description = "Opcional. Indica si el producto esta disponible; si no se envia queda en true",
            example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean activo;
}