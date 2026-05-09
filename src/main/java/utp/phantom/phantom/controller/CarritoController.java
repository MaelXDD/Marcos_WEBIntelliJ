package utp.phantom.phantom.controller;

// ═══════════════════════════════════════════════════════════════
//  CarritoController.java
//  Maneja todas las rutas relacionadas al carrito:
//    GET  /carrito          → ver el carrito
//    POST /carrito/agregar  → agregar producto
//    POST /carrito/eliminar → eliminar un ítem
//    POST /carrito/actualizar → cambiar cantidad
//    POST /carrito/vaciar   → vaciar todo
// ═══════════════════════════════════════════════════════════════

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.CarritoService;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")   // todas las rutas de este controller empiezan con /carrito
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    // ── GET /carrito → mostrar la página del carrito ─────────────
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("items",  carritoService.obtenerCarrito(session));
        model.addAttribute("total",  carritoService.calcularTotal(session));
        model.addAttribute("cuenta", carritoService.contarItems(session));
        return "carrito";   // carga templates/carrito.html
    }

    // ── POST /carrito/agregar → agregar producto al carrito ──────
    //    Recibe el id del producto por parámetro de formulario
    @PostMapping("/agregar")
    public String agregar(@RequestParam Long productoId,
                          @RequestParam(defaultValue = "/") String origen,
                          HttpSession session,
                          RedirectAttributes flash) {

        Optional<Producto> opt = productoRepository.findById(productoId);

        if (opt.isPresent()) {
            Producto p = opt.get();
            carritoService.agregar(session,
                    p.getId(), p.getNombre(), p.getPrecio(), p.getImagenUrl());
            flash.addFlashAttribute("mensajeOk", "\"" + p.getNombre() + "\" agregado al carrito.");
        } else {
            flash.addFlashAttribute("mensajeError", "Producto no encontrado.");
        }

        // Redirige de vuelta a la página de donde vino el usuario
        return "redirect:" + origen;
    }

    // ── POST /carrito/eliminar → quitar un ítem ──────────────────
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long productoId,
                           HttpSession session) {
        carritoService.eliminar(session, productoId);
        return "redirect:/carrito";
    }

    // ── POST /carrito/actualizar → cambiar cantidad ──────────────
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                             @RequestParam int cantidad,
                             HttpSession session) {
        carritoService.actualizarCantidad(session, productoId, cantidad);
        return "redirect:/carrito";
    }

    // ── POST /carrito/vaciar → eliminar todo ─────────────────────
    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {
        carritoService.vaciar(session);
        return "redirect:/carrito";
    }
}