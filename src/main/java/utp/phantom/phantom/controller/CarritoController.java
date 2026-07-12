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
import utp.phantom.phantom.exception.ResourceNotFoundException;
import utp.phantom.phantom.exception.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

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
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregar(@RequestParam Long productoId,
                                                       @RequestParam(defaultValue = "/") String origen,
                                                       HttpSession session) {

        Optional<Producto> opt = productoRepository.findById(productoId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Producto no encontrado."));
        }

        Producto p = opt.get();
        try {
            carritoService.agregar(session,
                    p.getId(), p.getNombre(), p.getPrecio(), p.getImagenUrl(), p.getStock());
            return ResponseEntity.ok(Map.of("success", true, "message",
                    "\"" + p.getNombre() + "\" agregado al carrito."));
        } catch (StockInsuficienteException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
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
                             HttpSession session,
                             RedirectAttributes flash) {
        try {
            carritoService.actualizarCantidad(session, productoId, cantidad);
        } catch (StockInsuficienteException | ResourceNotFoundException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
        }
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