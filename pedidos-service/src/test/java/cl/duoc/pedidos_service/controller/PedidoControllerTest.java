package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.DatosPrueba;
import cl.duoc.pedidos_service.exception.TotalInvalidoException;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.security.JwtFilter;
import cl.duoc.pedidos_service.security.JwtUtil;
import cl.duoc.pedidos_service.security.SecurityConfig;
import cl.duoc.pedidos_service.service.PedidoService;
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

@WebMvcTest(PedidoController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class PedidoControllerTest {

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
    private PedidoService pedidoService;

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
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token invalido responde 401")
    void tokenInvalidoResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET con HATEOAS ----------

    @Test
    @DisplayName("GET lista pedidos con enlaces HATEOAS")
    void listarConEnlaces() throws Exception {
        Pedido pedido = DatosPrueba.pedidoConId(1L);
        when(pedidoService.findAll()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pedidoList[0].cantidad").value(pedido.getCantidad()))
                .andExpect(jsonPath("$._embedded.pedidoList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET por id devuelve el pedido con enlaces a pedidos, paciente y producto")
    void buscarPorIdExistente() throws Exception {
        Pedido pedido = DatosPrueba.pedidoConId(1L);
        when(pedidoService.findById(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/v1/pedidos/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(pedido.getTotal()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.pedidos.href").exists())
                .andExpect(jsonPath("$._links.paciente.href").exists())
                .andExpect(jsonPath("$._links.producto.href").exists());
    }

    @Test
    @DisplayName("GET por id responde 404 si no existe")
    void buscarPorIdInexistente() throws Exception {
        when(pedidoService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/pedidos/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isNotFound());
    }

    // ---------- POST ----------

    @Test
    @DisplayName("POST con datos validos responde 201")
    void registrarValido() throws Exception {
        when(pedidoService.save(any(Pedido.class))).thenReturn(DatosPrueba.pedidoConId(1L));

        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.pedido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST con datos invalidos responde 400 de validacion")
    void registrarInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.jsonInvalido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST con total que no cuadra responde 400 de regla de negocio")
    void registrarTotalInvalido() throws Exception {
        when(pedidoService.save(any(Pedido.class)))
                .thenThrow(new TotalInvalidoException("El total debe ser igual a cantidad x precio unitario."));

        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.pedido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ---------- PUT y DELETE ----------

    @Test
    @DisplayName("PUT responde 404 si el pedido no existe")
    void actualizarInexistente() throws Exception {
        when(pedidoService.update(any(Long.class), any(Pedido.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/pedidos/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.pedido())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE responde 204")
    void borrarResponde204() throws Exception {
        mockMvc.perform(delete("/api/v1/pedidos/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isNoContent());
    }
}
