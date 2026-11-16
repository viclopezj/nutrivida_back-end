package cl.duoc.pedidos_service;

import cl.duoc.pedidos_service.model.Pedido;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.datafaker.Faker;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

/** Datos de prueba generados con DataFaker, y utilidades para armar JSON y tokens JWT en los tests. */
public final class DatosPrueba {

    private static final Faker FAKER = new Faker(Locale.of("es", "CL"));

    private DatosPrueba() {
    }

    /** Pedido valido con total = cantidad x precio unitario, sin id. */
    public static Pedido pedido() {
        int cantidad = FAKER.number().numberBetween(1, 10);
        int precioUnitario = FAKER.number().numberBetween(500, 30_000);

        Pedido p = new Pedido();
        p.setIdPaciente((long) FAKER.number().numberBetween(1, 50));
        p.setIdProducto((long) FAKER.number().numberBetween(1, 50));
        p.setCantidad(cantidad);
        p.setPrecioUnitario(precioUnitario);
        p.setTotal(cantidad * precioUnitario);
        p.setEstado(FAKER.options().option("pendiente", "pagado"));
        p.setFechaPedido(LocalDateTime.now().minusDays(FAKER.number().numberBetween(0, 30)));
        return p;
    }

    public static Pedido pedidoConId(Long id) {
        Pedido p = pedido();
        p.setId(id);
        return p;
    }

    /** Cuerpo JSON del pedido (sin id), listo para enviar en POST y PUT. */
    public static String json(Pedido p) {
        return "{"
                + "\"idPaciente\":" + p.getIdPaciente() + ","
                + "\"idProducto\":" + p.getIdProducto() + ","
                + "\"cantidad\":" + p.getCantidad() + ","
                + "\"precioUnitario\":" + p.getPrecioUnitario() + ","
                + "\"total\":" + p.getTotal() + ","
                + "\"estado\":" + q(p.getEstado()) + ","
                + "\"fechaPedido\":" + q(p.getFechaPedido().withNano(0).toString())
                + "}";
    }

    /** Cuerpo que incumple las validaciones (sin paciente, cantidad cero). */
    public static String jsonInvalido() {
        return "{\"idProducto\":1,\"cantidad\":0,\"precioUnitario\":1000,\"total\":0}";
    }

    /** Token JWT firmado con la misma clave que usan los servicios (claim "rol", igual que auth-service). */
    public static String token(String secret, String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private static String q(String valor) {
        if (valor == null) return "null";
        return "\"" + valor.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
