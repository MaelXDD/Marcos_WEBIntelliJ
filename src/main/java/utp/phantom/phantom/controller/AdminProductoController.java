package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.phantom.phantom.model.Categoria;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.service.ProductoService;

import java.util.List;

@Controller
@RequestMapping("/admin/productos")
public class    AdminProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listar(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String filtro,
                         Model model) {
        List<Producto> listaFinal;
        if ("stock_bajo".equals(filtro)) {
            listaFinal = productoService.obtenerStockBajo(5);
        } else {
            listaFinal = productoService.buscarProductos(keyword);
        }
        model.addAttribute("productos", listaFinal);
        model.addAttribute("keyword", keyword);
        return "admin/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto productoNuevo = new Producto();
        productoNuevo.setCategoria(new Categoria());
        model.addAttribute("producto", productoNuevo);
        model.addAttribute("categorias", productoService.listarCategorias());
        model.addAttribute("titulo", "Nuevo Producto");
        return "admin/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoService.guardarProducto(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerProducto(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", productoService.listarCategorias());
        model.addAttribute("titulo", "Editar Producto");
        return "admin/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/admin/productos";
    }

    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", productoService.listarCategorias());
        return "admin/categorias";
    }

    @GetMapping("/categorias/nueva")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categoria-form";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        productoService.guardarCategoria(categoria);
        return "redirect:/admin/productos/categorias";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        if (!productoService.categoriaEstaVacia(id)) {
            redirectAttrs.addFlashAttribute("errorCat", "No se puede eliminar: la categoría tiene productos asociados.");
            return "redirect:/admin/productos/categorias";
        }
        productoService.eliminarCategoria(id);
        return "redirect:/admin/productos/categorias";
    }
}
