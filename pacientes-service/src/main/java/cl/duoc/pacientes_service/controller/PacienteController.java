package cl.duoc.pacientes_service.controller;

import cl.duoc.pacientes_service.exception.ErrorResponse;
import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pacientes", description = "CRUD de pacientes. Los GET devuelven enlaces HATEOAS; "
        + "requiere token JWT de cualquier rol")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
@RestController
@RequestMapping("api/v1/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    //1
    @Operation(
            summary = "Listar pacientes",
            description = "Lista todos los pacientes del sistema. Cada paciente incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Pacientes encontrados",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Lista de pacientes", value = """
                            {
                              "_embedded": {
                                "pacienteList": [
                                  {
                                    "id": 1,
                                    "rut": "12.345.678-5",
                                    "nombre": "Ana",
                                    "apellido": "Perez Gonzalez",
                                    "email": "paciente@gmail.com",
                                    "telefono": "+56912345678",
                                    "fechaNacimiento": "1995-08-20",
                                    "sexoBiologico": "Femenino",
                                    "identidadGenero": "Mujer",
                                    "peso": 68.50,
                                    "estatura": 165.00,
                                    "cintura": 80.00,
                                    "alergias": "Alergia a los frutos secos",
                                    "enfermedades": null,
                                    "patronAlimenticio": "Omnivoro",
                                    "_links": {
                                      "self": { "href": "http://localhost:8081/api/v1/pacientes/1" },
                                      "pacientes": { "href": "http://localhost:8081/api/v1/pacientes" }
                                    }
                                  }
                                ]
                              },
                              "_links": {
                                "self": { "href": "http://localhost:8081/api/v1/pacientes" }
                              }
                            }
                            """)
            )
    )
    @GetMapping
    public ResponseEntity<?> listar() {
        List<EntityModel<Paciente>> pacientes = pacienteService.findAll().stream()
                .map(this::conEnlaces)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(pacientes,
                linkTo(methodOn(PacienteController.class).listar()).withSelfRel()));
    }

    //2
    @Operation(
            summary = "Buscar paciente por ID",
            description = "Busca un paciente a traves del ID (numerico, tipo Long). Incluye enlaces HATEOAS (_links)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paciente encontrado",
            content = @Content(
                    mediaType = "application/hal+json",
                    examples = @ExampleObject(name = "Paciente encontrado", value = """
                            {
                              "id": 1,
                              "rut": "12.345.678-5",
                              "nombre": "Ana",
                              "apellido": "Perez Gonzalez",
                              "email": "paciente@gmail.com",
                              "telefono": "+56912345678",
                              "fechaNacimiento": "1995-08-20",
                              "sexoBiologico": "Femenino",
                              "identidadGenero": "Mujer",
                              "peso": 68.50,
                              "estatura": 165.00,
                              "cintura": 80.00,
                              "alergias": "Alergia a los frutos secos",
                              "enfermedades": null,
                              "patronAlimenticio": "Omnivoro",
                              "_links": {
                                "self": { "href": "http://localhost:8081/api/v1/pacientes/1" },
                                "pacientes": { "href": "http://localhost:8081/api/v1/pacientes" }
                              }
                            }
                            """)
            )
    )
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado (respuesta sin cuerpo)")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del paciente", example = "1") @PathVariable Long id) {
        Paciente paciente = pacienteService.findById(id);
        if (paciente == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(conEnlaces(paciente));
    }

    //3
    @Operation(
            summary = "Registrar un paciente",
            description = "Registra un paciente en el sistema. El RUT (formato 12.345.678-9) y el email "
                    + "no pueden repetirse. Alergias y enfermedades son opcionales",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Paciente.class),
                            examples = {
                                    @ExampleObject(name = "Paciente con alergias", value = """
                                            {
                                              "rut": "20.123.456-7",
                                              "nombre": "Camila",
                                              "apellido": "Reyes Vidal",
                                              "email": "camila.reyes@gmail.com",
                                              "telefono": "+56933445566",
                                              "fechaNacimiento": "1998-04-12",
                                              "sexoBiologico": "Femenino",
                                              "identidadGenero": "Mujer",
                                              "peso": 62.5,
                                              "estatura": 168,
                                              "cintura": 74,
                                              "alergias": "Intolerancia a la lactosa",
                                              "enfermedades": null,
                                              "patronAlimenticio": "Vegetariano"
                                            }
                                            """),
                                    @ExampleObject(name = "Solo datos obligatorios", value = """
                                            {
                                              "rut": "17.654.321-K",
                                              "nombre": "Diego",
                                              "apellido": "Herrera Lagos",
                                              "email": "diego.herrera@outlook.com",
                                              "telefono": "+56977889900",
                                              "fechaNacimiento": "1991-11-03",
                                              "sexoBiologico": "Masculino",
                                              "identidadGenero": "Hombre",
                                              "peso": 78,
                                              "estatura": 180,
                                              "cintura": 88,
                                              "patronAlimenticio": "Omnivoro"
                                            }
                                            """)
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Paciente creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Paciente.class),
                    examples = @ExampleObject(name = "Paciente creado", value = """
                            {
                              "id": 5,
                              "rut": "20.123.456-7",
                              "nombre": "Camila",
                              "apellido": "Reyes Vidal",
                              "email": "camila.reyes@gmail.com",
                              "telefono": "+56933445566",
                              "fechaNacimiento": "1998-04-12",
                              "sexoBiologico": "Femenino",
                              "identidadGenero": "Mujer",
                              "peso": 62.5,
                              "estatura": 168,
                              "cintura": 74,
                              "alergias": "Intolerancia a la lactosa",
                              "enfermedades": null,
                              "patronAlimenticio": "Vegetariano"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos, RUT repetido o email repetido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "RUT con formato invalido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[RUT con formato invalido (ejemplo: 12.345.678-9).]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Campos obligatorios vacios", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Nombre no puede estar vacio., Email no puede estar vacio.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "RUT repetido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (RUT)",
                                      "mensaje": "El RUT '12.345.678-5' ya se encuentra registrado.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Email repetido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Email)",
                                      "mensaje": "El email 'paciente@gmail.com' ya se encuentra registrado.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
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
            description = "Actualiza un paciente a traves del ID. El RUT y el email solo se validan "
                    + "como repetidos si cambian respecto a los actuales",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Paciente.class),
                            examples = @ExampleObject(name = "Actualizar peso y cintura de Ana", value = """
                                    {
                                      "rut": "12.345.678-5",
                                      "nombre": "Ana",
                                      "apellido": "Perez Gonzalez",
                                      "email": "paciente@gmail.com",
                                      "telefono": "+56912345678",
                                      "fechaNacimiento": "1995-08-20",
                                      "sexoBiologico": "Femenino",
                                      "identidadGenero": "Mujer",
                                      "peso": 66.0,
                                      "estatura": 165,
                                      "cintura": 77,
                                      "alergias": "Alergia a los frutos secos",
                                      "enfermedades": null,
                                      "patronAlimenticio": "Omnivoro"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paciente actualizado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Paciente.class),
                    examples = @ExampleObject(name = "Paciente actualizado", value = """
                            {
                              "id": 1,
                              "rut": "12.345.678-5",
                              "nombre": "Ana",
                              "apellido": "Perez Gonzalez",
                              "email": "paciente@gmail.com",
                              "telefono": "+56912345678",
                              "fechaNacimiento": "1995-08-20",
                              "sexoBiologico": "Femenino",
                              "identidadGenero": "Mujer",
                              "peso": 66.0,
                              "estatura": 165,
                              "cintura": 77,
                              "alergias": "Alergia a los frutos secos",
                              "enfermedades": null,
                              "patronAlimenticio": "Omnivoro"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos, RUT repetido o email repetido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Datos invalidos", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[Peso debe ser mayor a 0.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "RUT de otro paciente", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (RUT)",
                                      "mensaje": "No se puede actualizar. El RUT '15.678.234-1' ya existe.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Email de otro paciente", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Email)",
                                      "mensaje": "No se puede actualizar. El email 'matias.fuentes@nutrivida.cl' ya existe.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
            )
    )
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado (respuesta sin cuerpo)")
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
            description = "Elimina un paciente del sistema a traves del ID"
    )
    @ApiResponse(responseCode = "204", description = "Paciente eliminado (respuesta sin cuerpo)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @Parameter(description = "ID del paciente", example = "1") @PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Envuelve el paciente agregando sus enlaces HATEOAS: el propio recurso y la coleccion
    private EntityModel<Paciente> conEnlaces(Paciente paciente) {
        return EntityModel.of(paciente,
                linkTo(methodOn(PacienteController.class).buscarPorId(paciente.getId())).withSelfRel(),
                linkTo(methodOn(PacienteController.class).listar()).withRel("pacientes"));
    }
}