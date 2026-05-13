package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utp.phantom.phantom.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByNumeroTelefono(String numeroTelefono);
}