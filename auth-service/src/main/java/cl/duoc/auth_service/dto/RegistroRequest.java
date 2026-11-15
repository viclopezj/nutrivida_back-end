package cl.duoc.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistroRequest(
        @NotBlank(message = "Email no puede estar vacio.")
        @Pattern(regexp = "^[^\\s@]+@(gmail\\.com|duocuc\\.cl|outlook\\.com)$",
                message = "El correo debe pertenecer a @gmail.com, @duocuc.cl o @outlook.com.")
        String email,

        @NotBlank(message = "Password no puede estar vacio.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$",
                message = "La contraseña debe tener entre 8 y 72 caracteres, una mayuscula, una minuscula y un numero.")
        String password
) {}