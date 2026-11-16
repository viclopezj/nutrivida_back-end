package cl.duoc.productos_service.controller;

import cl.duoc.productos_service.DatosPrueba;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.security.JwtFilter;
import cl.duoc.productos_service.security.JwtUtil;
import cl.duoc.productos_service.security.SecurityConfig;
import cl.duoc.productos_service.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class ProductoControllerTest {

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
    private ProductoService productoService;

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
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token invalido responde 401")
    void tokenInvalidoResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET con HATEOAS (cualquier usuario autenticado) ----------

    @Test
    @DisplayName("GET lista productos con enlaces HATEOAS")
    void listarConEnlaces() throws Exception {
        Producto producto = DatosPrueba.productoConId(1L);
        when(productoService.findAll()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList[0].nombre").value(producto.getNombre()))
                .andExpect(jsonPath("$._embedded.productoList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET por id devuelve el producto con sus enlaces")
    void buscarPorIdExistente() throws Exception {
        Producto producto = DatosPrueba.productoConId(1L);
        when(productoService.findById(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(producto.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.productos.href").exists());
    }

    @Test
    @DisplayName("GET por id responde 404 si no existe")
    void buscarPorIdInexistente() throws Exception {
        when(productoService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/productos/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isNotFound());
    }

    // ---------- POST, PUT y DELETE (solo ADMIN) ----------

    @Test
    @DisplayName("POST con rol PACIENTE responde 403")
    void registrarSinRolAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.producto())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST con rol ADMIN y datos validos responde 201")
    void registrarComoAdmin() throws Exception {
        when(productoService.save(any(Producto.class))).thenReturn(DatosPrueba.productoConId(1L));

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.producto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST con datos invalidos responde 400 de validacion")
    void registrarInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearer("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.jsonInvalido()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT como ADMIN responde 404 si el producto no existe")
    void actualizarInexistente() throws Exception {
        when(productoService.update(any(Long.class), any(Producto.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/productos/99")
                        .header(HttpHeaders.AUTHORIZATION, bearer("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosPrueba.json(DatosPrueba.producto())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE con rol PACIENTE responde 403")
    void borrarSinRolAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("PACIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE como ADMIN responde 204")
    void borrarComoAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("ADMIN")))
                .andExpect(status().isNoContent());
    }
}
