package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.CarritoService;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    private void agregarUsuarioAutenticado(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                String nombreCompleto = userDetails.getNombre();
                String primerNombre = nombreCompleto.split(" ")[0];
                model.addAttribute("usuarioNombre", primerNombre);
            } else {
                model.addAttribute("usuarioNombre", auth.getName());
            }
        }
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("items",  carritoService.obtenerCarrito(session));
        model.addAttribute("total",  carritoService.calcularTotal(session));
        model.addAttribute("cuenta", carritoService.contarItems(session));
        agregarUsuarioAutenticado(model);
        return "carrito";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long productoId,
                          @RequestParam(defaultValue = "/") String origen,
                          HttpSession session,
                          RedirectAttributes flash) {

        Optional<Producto> opt = productoRepository.findById(productoId);
        if (opt.isPresent()) {
            Producto p = opt.get();
            carritoService.agregar(session,
                    p.getId(), p.getNombre(), p.getPrecio(), p.getImagenUrl(), p.getStock());

            flash.addFlashAttribute("mensajeOk",
                    "\"" + p.getNombre() + "\" agregado al carrito.");
        } else {
            flash.addFlashAttribute("mensajeError", "Producto no encontrado.");
        }
        return "redirect:" + origen;
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long productoId,
                           HttpSession session) {
        carritoService.eliminar(session, productoId);
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                             @RequestParam int cantidad,
                             HttpSession session) {
        carritoService.actualizarCantidad(session, productoId, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {
        carritoService.vaciar(session);
        return "redirect:/carrito";
    }

    @GetMapping("/count")
    @ResponseBody
    public int contarItems(HttpSession session) {
        return carritoService.contarItems(session);
    }
}