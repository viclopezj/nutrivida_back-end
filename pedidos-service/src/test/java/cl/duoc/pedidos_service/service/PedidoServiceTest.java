package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.DatosPrueba;
import cl.duoc.pedidos_service.exception.EstadoInvalidoException;
import cl.duoc.pedidos_service.exception.TotalInvalidoException;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("findAll devuelve todos los pedidos")
    void findAllDevuelveLista() {
        when(pedidoRepository.findAll())
                .thenReturn(List.of(DatosPrueba.pedidoConId(1L), DatosPrueba.pedidoConId(2L)));

        assertEquals(2, pedidoService.findAll().size());
    }

    @Test
    @DisplayName("findById devuelve el pedido cuando existe")
    void findByIdExistente() {
        Pedido pedido = DatosPrueba.pedidoConId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertEquals(pedido, pedidoService.findById(1L));
    }

    @Test
    @DisplayName("findById devuelve null cuando no existe")
    void findByIdInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(pedidoService.findById(99L));
    }

    @Test
    @DisplayName("save guarda el pedido si el total es cantidad x precio unitario")
    void saveGuardaPedido() {
        Pedido pedido = DatosPrueba.pedido();
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        assertEquals(pedido, pedidoService.save(pedido));
        verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("save lanza TotalInvalidoException si el total no cuadra")
    void saveTotalInvalido() {
        Pedido pedido = DatosPrueba.pedido();
        pedido.setTotal(pedido.getTotal() + 1);

        assertThrows(TotalInvalidoException.class, () -> pedidoService.save(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("save lanza EstadoInvalidoException si el estado no es pendiente ni pagado")
    void saveEstadoInvalido() {
        Pedido pedido = DatosPrueba.pedido();
        pedido.setEstado("cancelado");

        assertThrows(EstadoInvalidoException.class, () -> pedidoService.save(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("save deja estado 'pendiente' y fecha actual cuando no vienen")
    void saveAplicaValoresPorDefecto() {
        Pedido pedido = DatosPrueba.pedido();
        pedido.setEstado(null);
        pedido.setFechaPedido(null);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido guardado = pedidoService.save(pedido);

        assertEquals("pendiente", guardado.getEstado());
        assertNotNull(guardado.getFechaPedido());
    }

    @Test
    @DisplayName("save normaliza el estado a minusculas")
    void saveNormalizaEstado() {
        Pedido pedido = DatosPrueba.pedido();
        pedido.setEstado("  PAGADO ");
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        assertEquals("pagado", pedidoService.save(pedido).getEstado());
    }

    @Test
    @DisplayName("update devuelve null si el pedido no existe")
    void updateInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(pedidoService.update(99L, DatosPrueba.pedido()));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("update copia los campos nuevos y guarda")
    void updateActualizaCampos() {
        Pedido existente = DatosPrueba.pedidoConId(1L);
        Pedido cambios = DatosPrueba.pedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(existente)).thenReturn(existente);

        Pedido resultado = pedidoService.update(1L, cambios);

        assertEquals(1L, resultado.getId());
        assertEquals(cambios.getCantidad(), resultado.getCantidad());
        assertEquals(cambios.getTotal(), resultado.getTotal());
        assertEquals(cambios.getIdPaciente(), resultado.getIdPaciente());
    }

    @Test
    @DisplayName("update lanza TotalInvalidoException si el total no cuadra")
    void updateTotalInvalido() {
        Pedido existente = DatosPrueba.pedidoConId(1L);
        Pedido cambios = DatosPrueba.pedido();
        cambios.setTotal(cambios.getTotal() + 100);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));

        assertThrows(TotalInvalidoException.class, () -> pedidoService.update(1L, cambios));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete elimina por id")
    void deleteEliminaPorId() {
        pedidoService.delete(1L);

        verify(pedidoRepository).deleteById(1L);
    }
}
