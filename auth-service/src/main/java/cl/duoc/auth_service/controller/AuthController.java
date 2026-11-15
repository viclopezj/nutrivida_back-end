package cl.duoc.auth_service.controller;

import cl.duoc.auth_service.dto.LoginRequest;
import cl.duoc.auth_service.dto.LoginResponse;
import cl.duoc.auth_service.dto.RegistroRequest;
import cl.duoc.auth_service.dto.UsuarioResponse;
import cl.duoc.auth_service.exception.ErrorResponse;
import cl.duoc.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacion", description = "Login y registro de usuarios con JWT")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //1
    @Operation(
            summary = "Iniciar sesion",
            description = "Valida correo y contraseña y devuelve un token JWT. "
                    + "El token se envia en los demas servicios con la cabecera Authorization: Bearer <token>"
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
            responseCode = "401",
            description = "Correo o contraseña incorrectos",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //2
    @Operation(
            summary = "Registrar un usuario",
            description = "Crea un usuario con rol PACIENTE. El correo no puede repetirse"
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
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }
}