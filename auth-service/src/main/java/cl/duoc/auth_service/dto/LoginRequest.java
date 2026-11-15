package cl.duoc.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email no puede estar vacio.") String email,
        @NotBlank(message = "Password no puede estar vacio.") String password
) {}