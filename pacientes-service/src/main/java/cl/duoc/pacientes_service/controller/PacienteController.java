package cl.duoc.pacientes_service.controller;

import cl.duoc.pacientes_service.exception.ErrorResponse;
import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Pacientes", description = "CRUD de pacientes")
@RestController
@RequestMapping("api/v1/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    //1
    @Operation(
            summary = "Listar pacientes",
            description = "Metodo que lista todos los pacientes del sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pacientes encontrados",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = Paciente.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    //2
    @Operation(
            summary = "Buscar paciente por ID",
            description = "Metodo que busca un paciente a traves del ID. Este id es de tipo numerico (LONG)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paciente encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Paciente.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Paciente no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del paciente", example = "1")
            @PathVariable Long id) {
        Paciente paciente = pacienteService.findById(id);
        if (paciente == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(paciente);
    }

    //3
    @Operation(
            summary = "Registrar un paciente",
            description = "Metodo que registra un paciente en el sistema. El RUT y el email no pueden repetirse"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Paciente creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Paciente.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos o RUT/email repetido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Paciente paciente) {
        Paciente pacienteNuevo = pacienteService.save(paciente);
        return new ResponseEntity<>(pacienteNuevo, HttpStatus.CREATED);
    }

    //4
    @Operation(
            summary = "Actualizar un paciente por ID",
            description = "Metodo que actualiza un paciente en el sistema a traves del ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paciente actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Paciente.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Paciente no encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del paciente", example = "1") @PathVariable Long id,
            @Valid @RequestBody Paciente paciente) {
        Paciente pacienteActualizado = pacienteService.update(id, paciente);
        if (pacienteActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pacienteActualizado);
    }

    //5
    @Operation(
            summary = "Eliminar un paciente por ID",
            description = "Metodo que elimina un paciente del sistema a traves del ID"
    )
    @ApiResponse(responseCode = "204", description = "Paciente eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del paciente", example = "1") @PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}