package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.CarritoService;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    // ── VER CARRITO ───────────────────────────────────────────────
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {

        model.addAttribute(
                "items",
                carritoService.obtenerCarrito(session)
        );

        model.addAttribute(
                "total",
                carritoService.calcularTotal(session)
        );

        model.addAttribute(
                "cuenta",
                carritoService.contarItems(session)
        );

        return "carrito";
    }

    // ── AGREGAR PRODUCTO ─────────────────────────────────────────
    @PostMapping("/agregar")
    public String agregar(@RequestParam Long productoId,
                          @RequestParam(defaultValue = "/") String origen,
                          HttpSession session,
                          RedirectAttributes flash) {

        Optional<Producto> opt =
                productoRepository.findById(productoId);

        if (opt.isPresent()) {

            Producto p = opt.get();

            carritoService.agregar(
                    session,
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getImagenUrl()
            );

            flash.addFlashAttribute(
                    "mensajeOk",
                    "\"" + p.getNombre() + "\" agregado al carrito."
            );

        } else {

            flash.addFlashAttribute(
                    "mensajeError",
                    "Producto no encontrado."
            );
        }

        return "redirect:" + origen;
    }

    // ── ELIMINAR ITEM ────────────────────────────────────────────
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long productoId,
                           HttpSession session) {

        carritoService.eliminar(session, productoId);

        return "redirect:/carrito";
    }

    // ── ACTUALIZAR CANTIDAD ──────────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                             @RequestParam int cantidad,
                             HttpSession session) {

        carritoService.actualizarCantidad(
                session,
                productoId,
                cantidad
        );

        return "redirect:/carrito";
    }

    // ── VACIAR CARRITO ───────────────────────────────────────────
    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {

        carritoService.vaciar(session);

        return "redirect:/carrito";
    }

    // ── COMPRAR Y DESCONTAR STOCK ────────────────────────────────
    @PostMapping("/comprar")
    public String comprar(HttpSession session,
                          RedirectAttributes flash) {

        var items = carritoService.obtenerCarrito(session);

        // VALIDAR STOCK
        for (ItemCarrito item : items) {

            Optional<Producto> opt =
                    productoRepository.findById(
                            item.getProductoId()
                    );

            if (opt.isEmpty()) {

                flash.addFlashAttribute(
                        "mensajeError",
                        "Producto no encontrado."
                );

                return "redirect:/carrito";
            }

            Producto producto = opt.get();

            if (producto.getStock() < item.getCantidad()) {

                flash.addFlashAttribute(
                        "mensajeError",
                        "No hay suficiente stock de: "
                                + producto.getNombre()
                );

                return "redirect:/carrito";
            }
        }

        // DESCONTAR STOCK
        for (ItemCarrito item : items) {

            Producto producto =
                    productoRepository.findById(
                            item.getProductoId()
                    ).get();

            producto.setStock(
                    producto.getStock() - item.getCantidad()
            );

            productoRepository.save(producto);
        }

        // VACIAR CARRITO
        carritoService.vaciar(session);

        flash.addFlashAttribute(
                "mensajeOk",
                "Compra realizada correctamente."
        );

        return "redirect:/";
    }

    // ── CONTADOR AJAX ────────────────────────────────────────────
    @GetMapping("/count")
    @ResponseBody
    public int contarItems(HttpSession session) {

        return carritoService.contarItems(session);
    }
}