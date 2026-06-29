package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.phantom.phantom.model.Perfil;
import utp.phantom.phantom.model.Usuario;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByUsuario(Usuario usuario);

    Optional<Perfil> findByUsuarioId(Long usuarioId);

    boolean existsByUsuario(Usuario usuario);
}
