package cl.duoc.productos_service;

import cl.duoc.productos_service.model.Producto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.datafaker.Faker;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;

/** Datos de prueba generados con DataFaker, y utilidades para armar JSON y tokens JWT en los tests. */
public final class DatosPrueba {

    private static final Faker FAKER = new Faker(Locale.of("es", "CL"));

    private DatosPrueba() {
    }

    /** Producto valido (pasa todas las validaciones Jakarta), sin id y con destacado/activo sin definir. */
    public static Producto producto() {
        Producto p = new Producto();
        p.setNombre(FAKER.commerce().productName());
        p.setDescripcion(FAKER.lorem().sentence());
        p.setPrecio(FAKER.number().numberBetween(500, 50_000));
        p.setCategoria(FAKER.options().option("Despensa", "Suplementos", "Snacks", "Bebidas"));
        return p;
    }

    public static Producto productoConId(Long id) {
        Producto p = producto();
        p.setId(id);
        p.setDestacado(false);
        p.setActivo(true);
        return p;
    }

    /** Cuerpo JSON del producto (sin id), listo para enviar en POST y PUT. */
    public static String json(Producto p) {
        return "{"
                + "\"nombre\":" + q(p.getNombre()) + ","
                + "\"descripcion\":" + q(p.getDescripcion()) + ","
                + "\"precio\":" + p.getPrecio() + ","
                + "\"categoria\":" + q(p.getCategoria())
                + "}";
    }

    /** Cuerpo que incumple las validaciones (nombre vacio y precio negativo). */
    public static String jsonInvalido() {
        return "{\"nombre\":\"\",\"descripcion\":\"\",\"precio\":-5,\"categoria\":\"\"}";
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
