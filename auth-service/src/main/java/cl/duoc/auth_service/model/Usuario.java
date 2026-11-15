package cl.duoc.auth_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Email no puede estar vacio.")
    @Size(max = 150, message = "Email debe tener un maximo de 150 caracteres.")
    private String email;

    // Aqui se guarda el hash BCrypt (60 caracteres), nunca la contraseña en texto plano
    @NotBlank(message = "Password no puede estar vacio.")
    @Size(max = 100, message = "Password debe tener un maximo de 100 caracteres.")
    private String password;

    @NotBlank(message = "Rol no puede estar vacio.")
    @Size(max = 20, message = "Rol debe tener un maximo de 20 caracteres.")
    private String rol;
}