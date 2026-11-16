package cl.duoc.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos para registrar un usuario. El rol asignado siempre es PACIENTE")
public record RegistroRequest(
        @NotBlank(message = "Email no puede estar vacio.")
        @Pattern(regexp = "^[^\\s@]+@(gmail\\.com|duocuc\\.cl|outlook\\.com)$",
                message = "El correo debe pertenecer a @gmail.com, @duocuc.cl o @outlook.com.")
        @Schema(description = "Correo de dominio gmail.com, duocuc.cl o outlook.com. No puede repetirse",
                example = "nuevo@gmail.com")
        String email,

        @NotBlank(message = "Password no puede estar vacio.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$",
                message = "La contraseña debe tener entre 8 y 72 caracteres, una mayuscula, una minuscula y un numero.")
        @Schema(description = "Entre 8 y 72 caracteres, con una mayuscula, una minuscula y un numero",
                example = "Clave1234")
        String password
) {}