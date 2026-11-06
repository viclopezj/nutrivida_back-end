package cl.duoc.pedidos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Paciente no puede estar vacio.")
    @Column(name = "id_paciente")
    private Long idPaciente;

    @NotNull(message = "Producto no puede estar vacio.")
    @Column(name = "id_producto")
    private Long idProducto;

    @NotNull(message = "Cantidad no puede estar vacia.")
    @Positive(message = "Cantidad debe ser mayor a 0.")
    private Integer cantidad;

    @NotNull(message = "Precio unitario no puede estar vacio.")
    @PositiveOrZero(message = "Precio unitario no puede ser negativo.")
    @Column(name = "precio_unitario")
    private Integer precioUnitario;

    @NotNull(message = "Total no puede estar vacio.")
    @PositiveOrZero(message = "Total no puede ser negativo.")
    private Integer total;

    // Opcional: si viene vacio, el service usa "pendiente"
    @Schema(hidden = true)
    @Size(max = 20, message = "Estado debe tener un maximo de 20 caracteres.")
    private String estado;

    // Opcional: si viene vacia, el service usa la fecha y hora actual
    @Schema(hidden = true)
    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;
}