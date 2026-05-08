package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import utp.phantom.phantom.repository.ProductoRepository;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private ProductoRepository productoRepository;

    // Mapeo amigable: slug de URL -> nombre de categoría en BD y título visible
    private static final Map<String, String[]> CATEGORIAS = Map.of(
        "consolas",  new String[]{"consolas",  "Consolas"},
        "juegos",    new String[]{"juegos",    "Videojuegos"},
        "perifericos", new String[]{"perifericos", "Periféricos"},
        "tarjetas",  new String[]{"tarjetas",  "Tarjetas Coleccionables"},
        "sillas",    new String[]{"sillas",    "Sillas Gamer"}
    );

    @GetMapping("/")
    public String index() {
        return "index"; // Carga index.html
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/mision")
    public String mision() {
        return "mision";
    }

    @GetMapping("/categoria/{slug}")
    public String categoria(@PathVariable String slug, Model model) {
        if (!CATEGORIAS.containsKey(slug)) {
            return "redirect:/"; // Si el slug no existe, redirige al inicio
        }

        String[] info = CATEGORIAS.get(slug);
        String nombreEnBD = info[0];
        String titulo     = info[1];

        model.addAttribute("titulo", titulo);
        model.addAttribute("slug", slug);
        model.addAttribute("productos", productoRepository.findByCategoriaIgnoreCase(nombreEnBD));

        return "categoria"; // Carga templates/categoria.html
    }
}
