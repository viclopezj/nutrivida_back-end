package cl.duoc.auth_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Formato de error que devuelve la API")
public class ErrorResponse {
    @Schema(description = "Codigo HTTP", example = "400")
    private int status;

    @Schema(description = "Tipo de error", example = "Error de validacion")
    private String error;

    @Schema(description = "Detalle del error", example = "[Email no puede estar vacio.]")
    private String mensaje;

    @Schema(description = "Momento en que ocurrio el error", example = "2026-11-10T09:00:00")
    private LocalDateTime fechaHora;
}