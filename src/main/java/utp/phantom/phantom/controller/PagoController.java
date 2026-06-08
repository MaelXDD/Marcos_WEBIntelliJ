package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import utp.phantom.phantom.model.DetalleVenta;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.repository.UsuarioRepository;
import utp.phantom.phantom.repository.VentaRepository;
import utp.phantom.phantom.service.CarritoService;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private void agregarUsuarioAutenticado(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                String primerNombre = userDetails.getNombre().split(" ")[0];
                model.addAttribute("usuarioNombre", primerNombre);
            } else {
                model.addAttribute("usuarioNombre", auth.getName());
            }
        }
    }

    @PostMapping("/procesar")
    @Transactional
    public String procesarPago(HttpSession session, Model model) {

        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        double total = carritoService.calcularTotal(session);
        int cantidadItems = items.stream().mapToInt(ItemCarrito::getCantidad).sum();

        for (ItemCarrito item : items) {
            Optional<Producto> opt = productoRepository.findById(item.getProductoId());
            if (opt.isPresent()) {
                Producto p = opt.get();
                int nuevoStock = Math.max(0, (p.getStock() != null ? p.getStock() : 0) - item.getCantidad());
                p.setStock(nuevoStock);
                productoRepository.save(p);
            }
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.valueOf(total));
        venta.setCantidadItems(cantidadItems);

        // ---- NUEVO CÓDIGO: Generar el número de orden ----
        int randomNum = (int) (Math.random() * 900000) + 100000;
        String numeroOrden = "PH-" + randomNum;
        venta.setNumeroOrden(numeroOrden);
        // --------------------------------------------------

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            usuarioRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(venta::setUsuario);
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        // ... (el ciclo for que crea los DetalleVenta se mantiene igual) ...
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
        ventaRepository.save(venta);

        carritoService.vaciar(session);
        model.addAttribute("total", total);
        model.addAttribute("cantidadItems", cantidadItems);
        model.addAttribute("carritoCount", 0);

        // ENVIAR EL NÚMERO DE ORDEN A LA VISTA
        model.addAttribute("numeroOrden", numeroOrden);

        agregarUsuarioAutenticado(model);

        return "pago-exitoso";
    }
}