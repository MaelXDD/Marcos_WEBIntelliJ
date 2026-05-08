package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.phantom.phantom.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar todos los productos de una categoría (ignorando mayúsculas)
    List<Producto> findByCategoriaIgnoreCase(String categoria);
}
