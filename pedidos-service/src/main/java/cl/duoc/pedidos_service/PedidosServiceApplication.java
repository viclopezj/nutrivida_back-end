package cl.duoc.pedidos_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(
				title = "API NutriVida 'Pedidos'",
				version = "1.0",
				description = "API que gestiona el CRUD de los pedidos",
				contact = @Contact(name = "Vicente Lopez", email = "vic.lopez@estudiante.duoc.cl")
		)
)
@SpringBootApplication
public class PedidosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosServiceApplication.class, args);
	}

}
