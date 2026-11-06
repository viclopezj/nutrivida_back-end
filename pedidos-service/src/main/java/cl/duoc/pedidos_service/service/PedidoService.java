package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.exception.EstadoInvalidoException;
import cl.duoc.pedidos_service.exception.TotalInvalidoException;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private static final String ESTADO_POR_DEFECTO = "pendiente";

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public Pedido save(Pedido pedido) {
        validarYCompletar(pedido);
        return pedidoRepository.save(pedido);
    }

    public Pedido update(Long id, Pedido pedido) {
        Pedido pedidoActualizar = pedidoRepository.findById(id).orElse(null);
        if (pedidoActualizar == null) return null;

        validarYCompletar(pedido);

        // Mapear campos aqui
        pedidoActualizar.setIdPaciente(pedido.getIdPaciente());
        pedidoActualizar.setIdProducto(pedido.getIdProducto());
        pedidoActualizar.setCantidad(pedido.getCantidad());
        pedidoActualizar.setPrecioUnitario(pedido.getPrecioUnitario());
        pedidoActualizar.setTotal(pedido.getTotal());
        pedidoActualizar.setEstado(pedido.getEstado());
        pedidoActualizar.setFechaPedido(pedido.getFechaPedido());
        return pedidoRepository.save(pedidoActualizar);
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    // Reglas de negocio compartidas por save y update
    private void validarYCompletar(Pedido pedido) {
        // total == cantidad x precioUnitario
        long esperado = (long) pedido.getCantidad() * pedido.getPrecioUnitario();
        if (pedido.getTotal() != esperado) {
            throw new TotalInvalidoException("El total (" + pedido.getTotal()
                    + ") debe ser igual a cantidad x precio unitario (" + esperado + ").");
        }

        // estado por defecto y validacion (mismos valores del CHECK del SQL)
        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            pedido.setEstado(ESTADO_POR_DEFECTO);
        } else {
            String estado = pedido.getEstado().trim().toLowerCase();
            if (!estado.equals("pendiente") && !estado.equals("pagado")) {
                throw new EstadoInvalidoException("El estado '" + pedido.getEstado()
                        + "' es invalido. Los estados permitidos son: 'pendiente' o 'pagado'.");
            }
            pedido.setEstado(estado);
        }

        // fecha por defecto
        if (pedido.getFechaPedido() == null) {
            pedido.setFechaPedido(LocalDateTime.now());
        }
    }
}