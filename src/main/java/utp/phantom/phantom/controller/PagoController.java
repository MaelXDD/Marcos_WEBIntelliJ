package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.CarritoService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping("/procesar")
    @Transactional
    public String procesarPago(HttpSession session, Model model) {

        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        double total         = carritoService.calcularTotal(session);
        int    cantidadItems = items.stream().mapToInt(ItemCarrito::getCantidad).sum();

        for (ItemCarrito item : items) {
            Optional<Producto> opt = productoRepository.findById(item.getProductoId());
            if (opt.isPresent()) {
                Producto p = opt.get();
                int nuevoStock = Math.max(0, (p.getStock() != null ? p.getStock() : 0) - item.getCantidad());
                p.setStock(nuevoStock);
                productoRepository.save(p);
            }
        }

        carritoService.vaciar(session);
        model.addAttribute("total",         total);
        model.addAttribute("cantidadItems", cantidadItems);
        return "pago-exitoso";
    }
}