package cl.duoc.pacientes_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "RUT no puede estar vacio.")
    @Size(max = 12, message = "RUT debe tener un maximo de 12 caracteres.")
    @Pattern(regexp = "^[0-9]{1,2}\\.[0-9]{3}\\.[0-9]{3}-[0-9kK]$",
            message = "RUT con formato invalido (ejemplo: 12.345.678-9).")
    private String rut;

    @NotBlank(message = "Nombre no puede estar vacio.")
    @Size(max = 100, message = "Nombre debe tener un maximo de 100 caracteres.")
    private String nombre;

    @NotBlank(message = "Apellido no puede estar vacio.")
    @Size(max = 100, message = "Apellido debe tener un maximo de 100 caracteres.")
    private String apellido;

    @NotBlank(message = "Email no puede estar vacio.")
    @Size(max = 150, message = "Email debe tener un maximo de 150 caracteres.")
    private String email;

    @NotBlank(message = "Telefono no puede estar vacio.")
    @Size(max = 12, message = "Telefono debe tener un maximo de 12 caracteres.")
    private String telefono;

    @NotNull(message = "Fecha de nacimiento no puede estar vacia.")
    @Past(message = "Fecha de nacimiento debe ser una fecha pasada.")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Sexo biologico no puede estar vacio.")
    @Size(max = 30, message = "Sexo biologico debe tener un maximo de 30 caracteres.")
    private String sexoBiologico;

    @NotBlank(message = "Identidad de genero no puede estar vacia.")
    @Size(max = 30, message = "Identidad de genero debe tener un maximo de 30 caracteres.")
    private String identidadGenero;

    @NotNull(message = "Peso no puede estar vacio.")
    @Positive(message = "Peso debe ser mayor a 0.")
    private BigDecimal peso;

    @NotNull(message = "Estatura no puede estar vacia.")
    @Positive(message = "Estatura debe ser mayor a 0.")
    private BigDecimal estatura;

    @NotNull(message = "Cintura no puede estar vacia.")
    @Positive(message = "Cintura debe ser mayor a 0.")
    private BigDecimal cintura;

    @Size(max = 255, message = "Alergias debe tener un maximo de 255 caracteres.")
    private String alergias;

    @Size(max = 255, message = "Enfermedades debe tener un maximo de 255 caracteres.")
    private String enfermedades;

    @NotBlank(message = "Patron alimenticio no puede estar vacio.")
    @Size(max = 50, message = "Patron alimenticio debe tener un maximo de 50 caracteres.")
    private String patronAlimenticio;
}