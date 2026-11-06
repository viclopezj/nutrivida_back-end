package cl.duoc.productos_service.service;

import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto save(Producto producto) {
        completarPorDefecto(producto);
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
        Producto productoActualizar = productoRepository.findById(id).orElse(null);
        if (productoActualizar == null) return null;

        completarPorDefecto(producto);

        // Mapear campos aqui
        productoActualizar.setNombre(producto.getNombre());
        productoActualizar.setDescripcion(producto.getDescripcion());
        productoActualizar.setPrecio(producto.getPrecio());
        productoActualizar.setCategoria(producto.getCategoria());
        productoActualizar.setDestacado(producto.getDestacado());
        productoActualizar.setActivo(producto.getActivo());
        return productoRepository.save(productoActualizar);
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    // Mismos valores por defecto del SQL: destacado = false, activo = true
    private void completarPorDefecto(Producto producto) {
        if (producto.getDestacado() == null) producto.setDestacado(false);
        if (producto.getActivo() == null) producto.setActivo(true);
    }
}