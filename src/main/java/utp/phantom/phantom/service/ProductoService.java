package utp.phantom.phantom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utp.phantom.phantom.model.Categoria;
import utp.phantom.phantom.model.Producto;
import utp.phantom.phantom.repository.CategoriaRepository;
import utp.phantom.phantom.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public List<Producto> buscarProductos(String keyword) {
        if (keyword == null || keyword.isBlank()) return listarProductos();
        return productoRepository.buscarPorNombre(keyword);
    }

    public Optional<Producto> obtenerProducto(Long id) {
        return productoRepository.findById(id);
    }

    public void guardarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerCategoria(Long id) {
        return categoriaRepository.findById(id);
    }

    public void guardarCategoria(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    public List<Producto> obtenerStockBajo(Integer limite) {
        return productoRepository.findByStockLessThanEqual(limite);
    }

    public boolean categoriaEstaVacia(Long id) {
        return productoRepository.findByCategoriaId(id).isEmpty();
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }
}