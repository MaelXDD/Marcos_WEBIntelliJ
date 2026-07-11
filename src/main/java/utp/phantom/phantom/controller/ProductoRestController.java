package utp.phantom.phantom.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.dto.ProductoDTO;
import utp.phantom.phantom.mapper.ProductoMapper;
import utp.phantom.phantom.model.Categoria;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.ProductoRepository;
import utp.phantom.phantom.service.ProductoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoMapper productoMapper;

    @GetMapping
    public List<ProductoDTO> listarTodos() {
        return productoService.listarProductos()
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerProducto(id)
                .map(productoMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO dto) {
        Producto producto = productoMapper.toEntity(dto);
        productoService.guardarProducto(producto);
        return ResponseEntity.ok(productoMapper.toDTO(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        return productoService.obtenerProducto(id).map(producto -> {
            productoMapper.updateEntityFromDTO(dto, producto);
            productoService.guardarProducto(producto);
            return ResponseEntity.ok(productoMapper.toDTO(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoService.obtenerProducto(id).isEmpty())
            return ResponseEntity.notFound().build();
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<String>> getKeywords(@RequestParam String term) {
        String lowerTerm = term.toLowerCase();

        List<String> productos = productoService.listarProductos().stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(lowerTerm)) ||
                        (p.getCategoria() != null && p.getCategoria().getNombre().toLowerCase().contains(lowerTerm)))
                .map(p -> p.getNombre())
                .distinct()
                .limit(4)
                .collect(Collectors.toList());

        List<String> categorias = productoService.listarCategorias().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().toLowerCase().contains(lowerTerm))
                .map(c -> c.getNombre())
                .distinct()
                .limit(2)
                .collect(Collectors.toList());

        productos.addAll(categorias);
        return ResponseEntity.ok(productos);
    }
}