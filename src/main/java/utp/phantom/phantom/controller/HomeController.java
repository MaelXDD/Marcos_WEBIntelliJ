package utp.phantom.phantom.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.CarritoService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoService carritoService;

    @Value("${google.maps.api-key}")
    private String mapsApiKey;

    private static final Map<String, Long> CATEGORIA_IDS = Map.of(
            "consolas",    1L,
            "juegos",      2L,
            "perifericos", 3L,
            "tarjetas",    4L,
            "sillas",      5L
    );

    private static final Map<String, String> CATEGORIA_TITULOS = Map.of(
            "consolas",    "Consolas",
            "juegos",      "Videojuegos",
            "perifericos", "Periféricos",
            "tarjetas",    "Tarjetas Coleccionables",
            "sillas",      "Sillas Gamer"
    );

    private void agregarContadorCarrito(Model model, HttpSession session) {
        model.addAttribute("carritoCount", carritoService.contarItems(session));
    }

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

    @GetMapping("/")
    public String index(@RequestParam(required = false) String loginError,
                        Model model, HttpSession session) {
        if (loginError != null) {
            model.addAttribute("loginError", true);
        }
        agregarContadorCarrito(model, session);
        agregarUsuarioAutenticado(model);
        return "index";
    }

    @GetMapping("/api/productos/buscar")
    @ResponseBody
    public List<Producto> buscarProductosAjax(@RequestParam String term) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(term, term);
    }

    // ── Nueva ruta: detalle de producto ──────────────────────────
    @GetMapping("/producto/{id}")
    public String detalleProducto(@PathVariable Long id,
                                  Model model, HttpSession session) {
        return productoRepository.findById(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    agregarContadorCarrito(model, session);
                    agregarUsuarioAutenticado(model);
                    return "producto";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model, HttpSession session) {
        agregarContadorCarrito(model, session);
        agregarUsuarioAutenticado(model);
        model.addAttribute("mapsApiKey", mapsApiKey);
        return "nosotros";
    }

    @GetMapping("/mision")
    public String mision(Model model, HttpSession session) {
        agregarContadorCarrito(model, session);
        agregarUsuarioAutenticado(model);
        return "mision";
    }

    @GetMapping("/categoria/{slug}")
    public String categoria(@PathVariable String slug,
                            Model model, HttpSession session) {

        if (!CATEGORIA_IDS.containsKey(slug)) {
            return "redirect:/";
        }

        Long   categoriaId = CATEGORIA_IDS.get(slug);
        String titulo      = CATEGORIA_TITULOS.get(slug);

        model.addAttribute("titulo",    titulo);
        model.addAttribute("slug",      slug);
        model.addAttribute("productos", productoRepository.findByCategoriaId(categoriaId));

        agregarContadorCarrito(model, session);
        agregarUsuarioAutenticado(model);

        return "categoria";
    }
}