package cl.duoc.auth_service.config;

import cl.duoc.auth_service.model.Usuario;
import cl.duoc.auth_service.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner crearUsuarios(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            crear(repo, encoder, "admin@duocuc.cl", "Admin1234", "ADMIN");
            crear(repo, encoder, "paciente@gmail.com", "Paciente1234", "PACIENTE");
            crear(repo, encoder, "secretaria@duocuc.cl", "Secretaria1234", "SECRETARIA");
        };
    }

    private void crear(UsuarioRepository repo, PasswordEncoder encoder,
                       String email, String password, String rol) {
        if (!repo.existsByEmail(email)) {
            repo.save(new Usuario(null, email, encoder.encode(password), rol));
        }
    }
}