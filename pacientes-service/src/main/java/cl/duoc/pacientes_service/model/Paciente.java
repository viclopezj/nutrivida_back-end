package cl.duoc.pacientes_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacientes")
@Schema(description = "Paciente de la clinica NutriVida")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador generado por el sistema", example = "1")
    private Long id;

    @NotBlank(message = "RUT no puede estar vacio.")
    @Size(max = 12, message = "RUT debe tener un maximo de 12 caracteres.")
    @Pattern(regexp = "^[0-9]{1,2}\\.[0-9]{3}\\.[0-9]{3}-[0-9kK]$",
            message = "RUT con formato invalido (ejemplo: 12.345.678-9).")
    @Schema(description = "RUT con puntos y guion. No puede repetirse", example = "12.345.678-9")
    private String rut;

    @NotBlank(message = "Nombre no puede estar vacio.")
    @Size(max = 100, message = "Nombre debe tener un maximo de 100 caracteres.")
    @Schema(description = "Nombre del paciente", example = "Ana")
    private String nombre;

    @NotBlank(message = "Apellido no puede estar vacio.")
    @Size(max = 100, message = "Apellido debe tener un maximo de 100 caracteres.")
    @Schema(description = "Apellido del paciente", example = "Pérez González")
    private String apellido;

    @NotBlank(message = "Email no puede estar vacio.")
    @Size(max = 150, message = "Email debe tener un maximo de 150 caracteres.")
    @Schema(description = "Correo electronico. No puede repetirse", example = "paciente@gmail.com")
    private String email;

    @NotBlank(message = "Telefono no puede estar vacio.")
    @Size(max = 12, message = "Telefono debe tener un maximo de 12 caracteres.")
    @Schema(description = "Celular chileno con prefijo +569", example = "+56912345678")
    private String telefono;

    @NotNull(message = "Fecha de nacimiento no puede estar vacia.")
    @Past(message = "Fecha de nacimiento debe ser una fecha pasada.")
    @Schema(description = "Fecha de nacimiento (AAAA-MM-DD)", example = "1995-08-20")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Sexo biologico no puede estar vacio.")
    @Size(max = 30, message = "Sexo biologico debe tener un maximo de 30 caracteres.")
    @Schema(description = "Sexo biologico", example = "Femenino")
    private String sexoBiologico;

    @NotBlank(message = "Identidad de genero no puede estar vacia.")
    @Size(max = 30, message = "Identidad de genero debe tener un maximo de 30 caracteres.")
    @Schema(description = "Identidad de genero", example = "Mujer")
    private String identidadGenero;

    @NotNull(message = "Peso no puede estar vacio.")
    @Positive(message = "Peso debe ser mayor a 0.")
    @Schema(description = "Peso en kilogramos", example = "68.5")
    private BigDecimal peso;

    @NotNull(message = "Estatura no puede estar vacia.")
    @Positive(message = "Estatura debe ser mayor a 0.")
    @Schema(description = "Estatura en centimetros", example = "165")
    private BigDecimal estatura;

    @NotNull(message = "Cintura no puede estar vacia.")
    @Positive(message = "Cintura debe ser mayor a 0.")
    @Schema(description = "Circunferencia de cintura en centimetros", example = "80")
    private BigDecimal cintura;

    @Size(max = 255, message = "Alergias debe tener un maximo de 255 caracteres.")
    @Schema(description = "Opcional. Alergias o restricciones alimentarias",
            example = "Alergia a los frutos secos", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String alergias;

    @Size(max = 255, message = "Enfermedades debe tener un maximo de 255 caracteres.")
    @Schema(description = "Opcional. Enfermedades diagnosticadas",
            example = "Diabetes tipo 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String enfermedades;

    @NotBlank(message = "Patron alimenticio no puede estar vacio.")
    @Size(max = 50, message = "Patron alimenticio debe tener un maximo de 50 caracteres.")
    @Schema(description = "Patron alimenticio", example = "Omnívoro")
    private String patronAlimenticio;
}