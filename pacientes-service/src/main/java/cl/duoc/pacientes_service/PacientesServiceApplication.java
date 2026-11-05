package cl.duoc.pacientes_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(
				title = "API NutriVida 'Pacientes'",
				version = "1.0",
				description = "API que gestiona el CRUD de los pacientes",
				contact = @Contact(name = "Vicente Lopez", email = "vic.lopez@estudiante.duoc.cl")
		)
)
@SpringBootApplication
public class PacientesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesServiceApplication.class, args);
	}

}
