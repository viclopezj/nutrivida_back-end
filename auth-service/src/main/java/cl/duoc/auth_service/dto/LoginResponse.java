package cl.duoc.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del login con el token JWT")
public record LoginResponse(
        @Schema(description = "Token JWT. Se envia en los demas servicios como Authorization: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBkdW9jdWMuY2wiLCJyb2wiOiJBRE1JTiJ9.firma")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        @Schema(description = "Rol del usuario: ADMIN, PACIENTE o SECRETARIA", example = "ADMIN")
        String rol
) {}