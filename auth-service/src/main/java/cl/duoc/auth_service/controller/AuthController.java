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
                    + "El token se usa en los demas servicios (boton Authorize de Swagger, "
                    + "pegando solo el token, sin la palabra Bearer). "
                    + "Usuarios de prueba: admin@duocuc.cl / Admin1234, "
                    + "paciente@gmail.com / Paciente1234 y secretaria@duocuc.cl / Secretaria1234"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login correcto",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Correo o contraseña vacios",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
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
                    + "@gmail.com, @duocuc.cl o @outlook.com. Los roles ADMIN y SECRETARIA no se pueden "
                    + "crear desde este endpoint"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Usuario creado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos invalidos o correo repetido",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Correo repetido", value = """
                            {
                              "status": 400,
                              "error": "Error en reglas de negocio (Email)",
                              "mensaje": "El email 'nuevo@gmail.com' ya se encuentra registrado.",
                              "fechaHora": "2026-11-10T09:00:00"
                            }
                            """)
            )
    )
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }
}