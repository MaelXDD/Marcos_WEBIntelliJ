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

    private static final Map<String, String[]> CATEGORIAS = Map.of(
            "consolas", new String[]{"Consolas", "Consolas"},
            "juegos", new String[]{"Juegos", "Videojuegos"},
            "perifericos", new String[]{"Periféricos", "Periféricos"},
            "tarjetas", new String[]{"Tarjetas", "Tarjetas Coleccionables"},
            "sillas", new String[]{"Sillas", "Sillas Gamer"}
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
            return "redirect:/";
        }

        String[] info = CATEGORIAS.get(slug);
        String nombreEnBD = info[0];
        String titulo = info[1];

        model.addAttribute("titulo", titulo);
        model.addAttribute("slug", slug);
        var productos = productoRepository.findByCategoria_NombreIgnoreCase(nombreEnBD);
        model.addAttribute("productos", productos);

        return "categoria";
    }
}
