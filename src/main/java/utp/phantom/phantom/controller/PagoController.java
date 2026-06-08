package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.repository.UsuarioRepository;
import utp.phantom.phantom.service.CarritoService;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;
import utp.phantom.phantom.service.VentaService;

import java.util.List;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VentaService ventaService;

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
    public String procesarPago(HttpSession session, Model model) {

        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        // Obtener usuario autenticado
        Usuario usuarioActual = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            usuarioActual = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        // Delegar toda la lógica al VentaService
        Venta venta = ventaService.procesarVenta(items, usuarioActual);

        carritoService.vaciar(session);
        model.addAttribute("total", venta.getTotal());
        model.addAttribute("cantidadItems", venta.getCantidadItems());
        model.addAttribute("numeroOrden", venta.getNumeroOrden());
        model.addAttribute("carritoCount", 0);
        agregarUsuarioAutenticado(model);

        return "pago-exitoso";
    }
}
