package cl.duoc.pacientes_service.controller;

import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pacientes", description = "CRUD de pacientes. Los GET devuelven enlaces HATEOAS; "
        + "requiere token JWT de cualquier rol")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
@RestController
@RequestMapping("api/v1/pacientes")
public class PacienteEmailController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(
            summary = "Buscar paciente por correo",
            description = "Busca la ficha de un paciente a traves de su correo. Un paciente solo puede consultar "
                    + "su propia ficha; ADMIN y SECRETARIA pueden consultar la de cualquiera. "
                    + "El front lo usa al iniciar sesion para cargar los datos del perfil"
    )
    @ApiResponse(responseCode = "200", description = "Paciente encontrado (incluye enlaces HATEOAS)")
    @ApiResponse(responseCode = "403", description = "Un paciente intento ver la ficha de otro (respuesta sin cuerpo)")
    @ApiResponse(responseCode = "404", description = "No hay un paciente con ese correo (respuesta sin cuerpo)")
    @GetMapping("/email/{email:.+}")
    public ResponseEntity<?> buscarPorEmail(
            @Parameter(description = "Correo del paciente", example = "paciente@gmail.com") @PathVariable String email,
            Authentication authentication) {
        // ADMIN y SECRETARIA ven cualquier ficha; el resto, solo la suya (el token trae el correo como nombre)
        boolean personalClinica = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SECRETARIA"));
        if (!personalClinica && !authentication.getName().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Paciente paciente = pacienteService.findByEmail(email);
        if (paciente == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(EntityModel.of(paciente,
                linkTo(methodOn(PacienteController.class).buscarPorId(paciente.getId())).withSelfRel(),
                linkTo(methodOn(PacienteController.class).listar()).withRel("pacientes")));
    }
}