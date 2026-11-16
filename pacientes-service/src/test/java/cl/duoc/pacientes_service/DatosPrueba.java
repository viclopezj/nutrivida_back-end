package cl.duoc.pacientes_service;

import cl.duoc.pacientes_service.model.Paciente;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

/** Datos de prueba generados con DataFaker, y utilidades para armar JSON y tokens JWT en los tests. */
public final class DatosPrueba {

    private static final Faker FAKER = new Faker(Locale.of("es", "CL"));

    private DatosPrueba() {
    }

    /** Paciente valido (pasa todas las validaciones Jakarta) y sin id. */
    public static Paciente paciente() {
        Paciente p = new Paciente();
        p.setRut(rutAleatorio());
        p.setNombre(FAKER.name().firstName());
        p.setApellido(FAKER.name().lastName());
        p.setEmail("paciente" + FAKER.number().digits(6) + "@nutrivida.cl");
        p.setTelefono("+569" + FAKER.number().digits(8));
        p.setFechaNacimiento(LocalDate.now()
                .minusYears(FAKER.number().numberBetween(18, 80))
                .minusDays(FAKER.number().numberBetween(1, 300)));
        p.setSexoBiologico(FAKER.options().option("Femenino", "Masculino"));
        p.setIdentidadGenero(FAKER.options().option("Mujer", "Hombre", "No binario"));
        p.setPeso(BigDecimal.valueOf(FAKER.number().randomDouble(1, 45, 120)));
        p.setEstatura(BigDecimal.valueOf(FAKER.number().numberBetween(150, 195)));
        p.setCintura(BigDecimal.valueOf(FAKER.number().numberBetween(60, 110)));
        p.setAlergias(null);
        p.setEnfermedades(null);
        p.setPatronAlimenticio(FAKER.options().option("Omnivoro", "Vegetariano", "Vegano", "Pescetariano"));
        return p;
    }

    public static Paciente pacienteConId(Long id) {
        Paciente p = paciente();
        p.setId(id);
        return p;
    }

    /** RUT con formato 12.345.678-9 y digito verificador correcto (modulo 11). */
    public static String rutAleatorio() {
        int numero = FAKER.number().numberBetween(5_000_000, 25_000_000);
        String s = String.valueOf(numero);
        int n = s.length();
        String conPuntos = s.substring(0, n - 6) + "." + s.substring(n - 6, n - 3) + "." + s.substring(n - 3);

        int suma = 0;
        int factor = 2;
        for (int i = n - 1; i >= 0; i--) {
            suma += Character.getNumericValue(s.charAt(i)) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int resto = 11 - (suma % 11);
        String dv = resto == 11 ? "0" : resto == 10 ? "K" : String.valueOf(resto);
        return conPuntos + "-" + dv;
    }

    /** Cuerpo JSON del paciente (sin id), listo para enviar en POST y PUT. */
    public static String json(Paciente p) {
        return "{"
                + "\"rut\":" + q(p.getRut()) + ","
                + "\"nombre\":" + q(p.getNombre()) + ","
                + "\"apellido\":" + q(p.getApellido()) + ","
                + "\"email\":" + q(p.getEmail()) + ","
                + "\"telefono\":" + q(p.getTelefono()) + ","
                + "\"fechaNacimiento\":" + q(p.getFechaNacimiento().toString()) + ","
                + "\"sexoBiologico\":" + q(p.getSexoBiologico()) + ","
                + "\"identidadGenero\":" + q(p.getIdentidadGenero()) + ","
                + "\"peso\":" + p.getPeso() + ","
                + "\"estatura\":" + p.getEstatura() + ","
                + "\"cintura\":" + p.getCintura() + ","
                + "\"alergias\":" + q(p.getAlergias()) + ","
                + "\"enfermedades\":" + q(p.getEnfermedades()) + ","
                + "\"patronAlimenticio\":" + q(p.getPatronAlimenticio())
                + "}";
    }

    /** Cuerpo que incumple las validaciones (campos obligatorios vacios). */
    public static String jsonInvalido() {
        return "{\"rut\":\"\",\"nombre\":\"\",\"email\":\"\"}";
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
