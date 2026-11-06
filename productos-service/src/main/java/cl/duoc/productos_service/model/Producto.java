package cl.duoc.productos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nombre no puede estar vacio.")
    @Size(max = 120, message = "Nombre debe tener un maximo de 120 caracteres.")
    private String nombre;

    // La columna es TEXT, por eso no lleva @Size
    @NotBlank(message = "Descripcion no puede estar vacia.")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "Precio no puede estar vacio.")
    @PositiveOrZero(message = "Precio no puede ser negativo.")
    private Integer precio;

    @NotBlank(message = "Categoria no puede estar vacia.")
    @Size(max = 30, message = "Categoria debe tener un maximo de 30 caracteres.")
    private String categoria;

    // Opcional: si viene vacio, el service usa false
    @Schema(hidden = true)
    private Boolean destacado;

    // Opcional: si viene vacio, el service usa true
    @Schema(hidden = true)
    private Boolean activo;
}