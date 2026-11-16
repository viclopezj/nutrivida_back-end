package cl.duoc.pacientes_service.controller;

import cl.duoc.pacientes_service.DatosPrueba;
import cl.duoc.pacientes_service.exception.RutExistenteException;
import cl.duoc.pacientes_service.model.Paciente;
import cl.duoc.pacientes_service.security.JwtFilter;
import cl.duoc.pacientes_service.security.JwtUtil;
import cl.duoc.pacientes_service.security.SecurityConfig;
import cl.duoc.pacientes_service.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PacienteController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class PacienteControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    // MockMvc armado a mano con SOLO la cadena de Spring Security. El MockMvc automatico ademas
    // registra JwtFilter como filtro suelto: corria antes de la cadena y la autenticacion se
    // perdia (todo respondia 401). Asi JwtFilter corre una sola vez, dentro de la cadena.
    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @MockitoBean
    private PacienteService pacienteService;

    // Misma clave que usan los servicios (viene del application.yml)
    @Value("${jwt.secret}")
    private String secret;

    private String bearer(String rol) {
        return "Bearer " + DatosPrueba.token(secret, "usuario@duocuc.cl", rol);
    }

    // ---------- JWT ----------

    @Test
    @DisplayName("Sin token responde 401")
    void sinTokenResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/pacientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token invalido responde 401")
    void tokenInvalidoResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/pacientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET con HATEOAS ----------

    @Test
    @DisplayName("GET lista pacientes con enlaces HATEOAS")
    void listarConEnlaces() throws Exception {
        Paciente paciente = DatosPrueba.pacienteConId(1L);
        when(pacienteService.findAll()).thenReturn(List.of(paciente));

        mockMvc.perform(get("/api/v1/pacientes")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pacienteList[0].nombre").value(paciente.getNombre()))
                .andExpect(jsonPath("$._embedded.pacienteList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET por id devuelve el paciente con sus enlaces")
    void buscarPorIdExistente() throws Exception {
        Paciente paciente = DatosPrueba.pacienteConId(1L);
        when(pacienteService.findById(1L)).thenReturn(paciente);

        mockMvc.perform(get("/api/v1/pacientes/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value(paciente.getRut()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.pacientes.href").exists());
    }

    @Test
    @DisplayName("GET por id responde 404 si no existe")
    void buscarPorIdInexistente() throws Exception {
        when(pacienteService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/pacientes/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isNotFound());
    }

    // ---------- POST ----------

    @Test
    @DisplayName("POST con datos validos responde 201")
    void registrarValido() throws Exception {
        Paciente paciente = DatosPrueba.paciente();
        Paciente guardado = DatosPrueba.pacienteConId(1L);
        when(pacienteService.save(any(Paciente.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/pacientes")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(paciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST con datos invalidos responde 400 de validacion")
    void registrarInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/pacientes")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.jsonInvalido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"));
    }

    @Test
    @DisplayName("POST con RUT repetido responde 400 de regla de negocio")
    void registrarRutDuplicado() throws Exception {
        when(pacienteService.save(any(Paciente.class)))
                .thenThrow(new RutExistenteException("El RUT ya se encuentra registrado."));

        mockMvc.perform(post("/api/v1/pacientes")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.paciente())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error en reglas de negocio (RUT)"));
    }

    // ---------- PUT y DELETE ----------

    @Test
    @DisplayName("PUT responde 404 si el paciente no existe")
    void actualizarInexistente() throws Exception {
        when(pacienteService.update(any(Long.class), any(Paciente.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/pacientes/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.paciente())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE responde 204")
    void borrarResponde204() throws Exception {
        mockMvc.perform(delete("/api/v1/pacientes/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isNoContent());
    }
}
