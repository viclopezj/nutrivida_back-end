package cl.duoc.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Usuario creado. Nunca incluye la contraseña")
public record UsuarioResponse(
        @Schema(description = "Identificador del usuario", example = "4")
        Long id,

        @Schema(description = "Correo del usuario", example = "nuevo@gmail.com")
        String email,

        @Schema(description = "Rol asignado", example = "PACIENTE")
        String rol
) {}