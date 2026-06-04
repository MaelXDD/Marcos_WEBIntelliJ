package utp.phantom.phantom.repository;

import utp.phantom.phantom.entity.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



@Repository

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
