package cl.duoc.productos_service.service;

import cl.duoc.productos_service.DatosPrueba;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("findAll devuelve todos los productos")
    void findAllDevuelveLista() {
        when(productoRepository.findAll())
                .thenReturn(List.of(DatosPrueba.productoConId(1L), DatosPrueba.productoConId(2L)));

        assertEquals(2, productoService.findAll().size());
    }

    @Test
    @DisplayName("findById devuelve el producto cuando existe")
    void findByIdExistente() {
        Producto producto = DatosPrueba.productoConId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertEquals(producto, productoService.findById(1L));
    }

    @Test
    @DisplayName("findById devuelve null cuando no existe")
    void findByIdInexistente() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(productoService.findById(99L));
    }

    @Test
    @DisplayName("save deja destacado=false y activo=true cuando no vienen")
    void saveAplicaValoresPorDefecto() {
        Producto producto = DatosPrueba.producto();
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto guardado = productoService.save(producto);

        assertFalse(guardado.getDestacado());
        assertTrue(guardado.getActivo());
    }

    @Test
    @DisplayName("save respeta destacado y activo si vienen informados")
    void saveRespetaValoresEnviados() {
        Producto producto = DatosPrueba.producto();
        producto.setDestacado(true);
        producto.setActivo(false);
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto guardado = productoService.save(producto);

        assertTrue(guardado.getDestacado());
        assertFalse(guardado.getActivo());
    }

    @Test
    @DisplayName("update devuelve null si el producto no existe")
    void updateInexistente() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(productoService.update(99L, DatosPrueba.producto()));
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("update copia los campos nuevos y guarda")
    void updateActualizaCampos() {
        Producto existente = DatosPrueba.productoConId(1L);
        Producto cambios = DatosPrueba.producto();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(existente)).thenReturn(existente);

        Producto resultado = productoService.update(1L, cambios);

        assertEquals(1L, resultado.getId());
        assertEquals(cambios.getNombre(), resultado.getNombre());
        assertEquals(cambios.getPrecio(), resultado.getPrecio());
        assertEquals(cambios.getCategoria(), resultado.getCategoria());
    }

    @Test
    @DisplayName("delete elimina por id")
    void deleteEliminaPorId() {
        productoService.delete(1L);

        verify(productoRepository).deleteById(1L);
    }
}
