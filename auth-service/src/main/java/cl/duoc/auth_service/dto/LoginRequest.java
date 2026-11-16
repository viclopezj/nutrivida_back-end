package cl.duoc.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesion")
public record LoginRequest(
        @NotBlank(message = "Email no puede estar vacio.")
        @Schema(description = "Correo registrado", example = "admin@duocuc.cl")
        String email,

        @NotBlank(message = "Password no puede estar vacio.")
        @Schema(description = "Contraseña del usuario", example = "Admin1234")
        String password
) {}