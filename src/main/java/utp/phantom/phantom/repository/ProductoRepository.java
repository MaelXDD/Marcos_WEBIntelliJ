package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.phantom.phantom.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria_NombreIgnoreCase(String nombre);
}