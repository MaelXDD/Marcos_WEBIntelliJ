package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.service.ProductoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService productoService;

    // GET: Obtener todos los productos
    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarProductos();
    }

    // GET: Obtener un producto específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProducto(id);

        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        productoService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // PUT: Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        Optional<Producto> productoExistente = productoService.obtenerProducto(id);

        if (productoExistente.isPresent()) {
            Producto p = productoExistente.get();
            p.setNombre(productoDetalles.getNombre());
            p.setDescripcion(productoDetalles.getDescripcion());
            p.setPrecio(productoDetalles.getPrecio());
            p.setMarca(productoDetalles.getMarca());
            p.setStock(productoDetalles.getStock());
            p.setImagenUrl(productoDetalles.getImagenUrl());
            if(productoDetalles.getCategoria() != null) {
                p.setCategoria(productoDetalles.getCategoria());
            }

            productoService.guardarProducto(p);
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Eliminar un producto por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Optional<Producto> productoExistente = productoService.obtenerProducto(id);

        if (productoExistente.isPresent()) {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}