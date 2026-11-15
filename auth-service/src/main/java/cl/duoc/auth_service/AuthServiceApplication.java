package cl.duoc.auth_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "API NutriVida 'Auth'",
				version = "1.0",
				description = "API de autenticacion con JWT: login y registro de usuarios. "
						+ "Desarrollada por Vicente Lopez e Igor Rojas",
				contact = @Contact(
						name = "Vicente Lopez e Igor Rojas",
						email = "vic.lopez@estudiante.duoc.cl"
				)
		)
)
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}