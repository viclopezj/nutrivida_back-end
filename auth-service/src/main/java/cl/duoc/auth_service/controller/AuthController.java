package cl.duoc.auth_service.controller;

import cl.duoc.auth_service.dto.LoginRequest;
import cl.duoc.auth_service.dto.LoginResponse;
import cl.duoc.auth_service.dto.RegistroRequest;
import cl.duoc.auth_service.dto.UsuarioResponse;
import cl.duoc.auth_service.exception.ErrorResponse;
import cl.duoc.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacion", description = "Login y registro de usuarios con JWT. "
        + "Estos endpoints son publicos: no requieren token")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //1
    @Operation(
            summary = "Iniciar sesion",
            description = "Valida correo y contraseña y devuelve un token JWT con una duracion de 1 hora. "
                    + "Copia el valor de 'token' y pegalo en el boton Authorize de los otros servicios "
                    + "(solo el token, sin la palabra Bearer)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(name = "Administrador", value = """
                                            {
                                              "email": "admin@duocuc.cl",
                                              "password": "Admin1234"
                                            }
                                            """),
                                    @ExampleObject(name = "Paciente", value = """
                                            {
                                              "email": "paciente@gmail.com",
                                              "password": "Paciente1234"
                                            }
                                            """),
                                    @ExampleObject(name = "Secretaria", value = """
                                            {
                                              "email": "secretaria@duocuc.cl",
                                              "password": "Secretaria1234"
                                            }
                                            """)
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login correcto. Devuelve el token y el rol del usuario",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(name = "Login correcto", value = """
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBkdW9jdWMuY2wiLCJyb2wiOiJBRE1JTiIsImlhdCI6MTc2Mjc2NzYwMCwiZXhwIjoxNzYyNzcxMjAwfQ.firma",
                              "tipo": "Bearer",
                              "rol": "ADMIN"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Correo o contraseña vacios",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Campos vacios", value = """
                            {
                              "status": 400,
                              "error": "Error de validacion",
                              "mensaje": "[Email no puede estar vacio., Password no puede estar vacio.]",
                              "fechaHora": "2026-11-10T09:00:00"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Correo o contraseña incorrectos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Credenciales invalidas", value = """
                            {
                              "status": 401,
                              "error": "Error de autenticacion",
                              "mensaje": "Correo o contraseña incorrectos.",
                              "fechaHora": "2026-11-10T09:00:00"
                            }
                            """)
            )
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //2
    @Operation(
            summary = "Registrar un usuario",
            description = "Crea un usuario con rol PACIENTE. El correo no puede repetirse y debe ser de "
                    + "@gmail.com, @duocuc.cl o @outlook.com. La contraseña debe tener entre 8 y 72 caracteres, "
                    + "con una mayuscula, una minuscula y un numero. Los roles ADMIN y SECRETARIA no se pueden "
                    + "crear desde este endpoint",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistroRequest.class),
                            examples = @ExampleObject(name = "Usuario nuevo", value = """
                                    {
                                      "email": "nuevo@gmail.com",
                                      "password": "Clave1234"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Usuario creado con rol PACIENTE. Nunca devuelve la contraseña",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioResponse.class),
                    examples = @ExampleObject(name = "Usuario creado", value = """
                            {
                              "id": 4,
                              "email": "nuevo@gmail.com",
                              "rol": "PACIENTE"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos o correo repetido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Correo repetido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error en reglas de negocio (Email)",
                                      "mensaje": "El email 'nuevo@gmail.com' ya se encuentra registrado.",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Dominio de correo no permitido", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[El correo debe pertenecer a @gmail.com, @duocuc.cl o @outlook.com.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """),
                            @ExampleObject(name = "Contraseña invalida", value = """
                                    {
                                      "status": 400,
                                      "error": "Error de validacion",
                                      "mensaje": "[La contraseña debe tener entre 8 y 72 caracteres, una mayuscula, una minuscula y un numero.]",
                                      "fechaHora": "2026-11-10T09:00:00"
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }
}