package utp.phantom.phantom.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.dto.ProductoDTO;
import utp.phantom.phantom.mapper.ProductoMapper;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.service.ProductoService;

import java.util.List;
import java.util.stream.Collectors;

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
}