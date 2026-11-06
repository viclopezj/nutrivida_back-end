package cl.duoc.productos_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "API NutriVida 'Productos'",
				version = "1.0",
				description = "API que gestiona el CRUD de los productos. "
						+ "Desarrollada por Vicente Lopez e Igor Rojas",
				contact = @Contact(
						name = "Vicente Lopez e Igor Rojas",
						email = "vic.lopez@estudiante.duoc.cl"
				)
		)
)
@SpringBootApplication
public class ProductosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductosServiceApplication.class, args);
	}

}
