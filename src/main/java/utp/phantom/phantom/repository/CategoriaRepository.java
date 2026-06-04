package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.phantom.phantom.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}