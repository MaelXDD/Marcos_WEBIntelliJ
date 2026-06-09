package utp.phantom.phantom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.phantom.phantom.model.DetalleVenta;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.repository.DetalleVentaRepository;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.repository.VentaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Venta procesarVenta(List<ItemCarrito> items, Usuario usuario) {

        //  Descontar stock de cada producto
        for (ItemCarrito item : items) {
            Optional<Producto> opt = productoRepository.findById(item.getProductoId());
            if (opt.isPresent()) {
                Producto p = opt.get();
                int nuevoStock = Math.max(0, (p.getStock() != null ? p.getStock() : 0) - item.getCantidad());
                p.setStock(nuevoStock);
                productoRepository.save(p);
            }
        }

        // Calcular totales
        double total = items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        int cantidadItems = items.stream().mapToInt(ItemCarrito::getCantidad).sum();

        // Generar número de orden único
        int randomNum = (int) (Math.random() * 900000) + 100000;
        String numeroOrden = "PH-" + randomNum;

        //  Crear la Venta
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.valueOf(total));
        venta.setCantidadItems(cantidadItems);
        venta.setNumeroOrden(numeroOrden);
        venta.setUsuario(usuario);

        // Crear los DetalleVenta
        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemCarrito item : items) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.valueOf(item.getPrecio()));
            detalle.setSubtotal(BigDecimal.valueOf(item.getCantidad() * item.getPrecio()));
            detalles.add(detalle);
        }
        venta.setDetalles(detalles);
        return ventaRepository.save(venta);
    }

     // Retorna todas las ventas registradas.//

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

     //Busca una venta por su ID.//

    public Optional<Venta> obtenerVenta(Long id) {
        return ventaRepository.findById(id);
    }

     //Retorna los detalles de una venta específica//

    public List<DetalleVenta> obtenerDetallesPorVenta(Long ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }
     //Cuenta el total de ventas registradas//

    public long contarVentas() {
        return ventaRepository.count();
    }
}
