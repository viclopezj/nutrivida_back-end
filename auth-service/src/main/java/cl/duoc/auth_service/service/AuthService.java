package cl.duoc.auth_service.service;

import cl.duoc.auth_service.dto.LoginRequest;
import cl.duoc.auth_service.dto.LoginResponse;
import cl.duoc.auth_service.dto.RegistroRequest;
import cl.duoc.auth_service.dto.UsuarioResponse;
import cl.duoc.auth_service.exception.CredencialesInvalidasException;
import cl.duoc.auth_service.exception.EmailExistenteException;
import cl.duoc.auth_service.model.Usuario;
import cl.duoc.auth_service.repository.UsuarioRepository;
import cl.duoc.auth_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String ROL_REGISTRO = "PACIENTE";

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // El mismo mensaje para "no existe" y "clave incorrecta": no se revela si el correo esta registrado
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));

        String token = jwtUtil.generar(usuario.getEmail(), usuario.getRol());
        return new LoginResponse(token, "Bearer", usuario.getRol());
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();

        // VALIDACIONES
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailExistenteException("El email '" + email + "' ya se encuentra registrado.");
        }

        // Quien se registra siempre es PACIENTE: ADMIN y SECRETARIA solo existen por los datos iniciales
        Usuario nuevo = new Usuario(null, email, passwordEncoder.encode(request.password()), ROL_REGISTRO);
        Usuario guardado = usuarioRepository.save(nuevo);
        return new UsuarioResponse(guardado.getId(), guardado.getEmail(), guardado.getRol());
    }
}