package cl.duoc.pedidos_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
@Schema(description = "Pedido de un paciente por un producto")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador generado por el sistema", example = "1")
    private Long id;

    @NotNull(message = "Paciente no puede estar vacio.")
    @Column(name = "id_paciente")
    @Schema(description = "ID del paciente (de pacientes-service). No se valida contra ese servicio", example = "1")
    private Long idPaciente;

    @NotNull(message = "Producto no puede estar vacio.")
    @Column(name = "id_producto")
    @Schema(description = "ID del producto (de productos-service). No se valida contra ese servicio", example = "1")
    private Long idProducto;

    @NotNull(message = "Cantidad no puede estar vacia.")
    @Positive(message = "Cantidad debe ser mayor a 0.")
    @Schema(description = "Unidades pedidas", example = "2")
    private Integer cantidad;

    @NotNull(message = "Precio unitario no puede estar vacio.")
    @PositiveOrZero(message = "Precio unitario no puede ser negativo.")
    @Column(name = "precio_unitario")
    @Schema(description = "Precio de una unidad en pesos chilenos", example = "7990")
    private Integer precioUnitario;

    @NotNull(message = "Total no puede estar vacio.")
    @PositiveOrZero(message = "Total no puede ser negativo.")
    @Schema(description = "Debe ser igual a cantidad x precio unitario", example = "15980")
    private Integer total;

    // Opcional: si viene vacio, el service usa "pendiente"
    @Size(max = 20, message = "Estado debe tener un maximo de 20 caracteres.")
    @Schema(description = "Opcional. Solo acepta 'pendiente' o 'pagado'; si no se envia queda 'pendiente'",
            example = "pendiente", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String estado;

    // Opcional: si viene vacia, el service usa la fecha y hora actual
    @Column(name = "fecha_pedido")
    @Schema(description = "Opcional. Fecha y hora del pedido; si no se envia se usa la actual",
            example = "2026-11-10T09:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime fechaPedido;
}