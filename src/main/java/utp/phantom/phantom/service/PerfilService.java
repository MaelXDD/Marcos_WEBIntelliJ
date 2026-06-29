package utp.phantom.phantom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.phantom.phantom.model.Perfil;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.repository.PerfilRepository;
import utp.phantom.phantom.repository.UsuarioRepository;
import utp.phantom.phantom.repository.VentaRepository;

import java.util.List;

@Service
public class PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene el usuario autenticado por email.
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    /**
     * Obtiene o crea el perfil del usuario si aún no tiene uno.
     */
    public Perfil obtenerOCrearPerfil(Usuario usuario) {
        return perfilRepository.findByUsuario(usuario).orElseGet(() -> {
            Perfil nuevo = new Perfil();
            nuevo.setUsuario(usuario);
            nuevo.setBiografia("");
            nuevo.setAvatarUrl("");
            return perfilRepository.save(nuevo);
        });
    }

    /**
     * Actualiza los datos personales del usuario (nombre, dirección, teléfono).
     */
    @Transactional
    public void actualizarDatosPersonales(String email, String nombre,
                                          String direccion, String telefono,
                                          String biografia) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        usuario.setNombre(nombre);
        usuario.setDireccion(direccion);
        usuario.setNumeroTelefono(telefono);
        usuarioRepository.save(usuario);

        Perfil perfil = obtenerOCrearPerfil(usuario);
        perfil.setBiografia(biografia);
        perfilRepository.save(perfil);
    }

    /**
     * Cambia la contraseña del usuario validando la contraseña actual.
     * Retorna null si fue exitoso, o un mensaje de error si falló.
     */
    @Transactional
    public String cambiarPassword(String email, String passwordActual,
                                  String passwordNuevo, String passwordConfirm) {
        Usuario usuario = obtenerUsuarioPorEmail(email);

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            return "La contraseña actual es incorrecta";
        }
        if (!passwordNuevo.equals(passwordConfirm)) {
            return "Las contraseñas nuevas no coinciden";
        }
        if (passwordNuevo.length() < 8) {
            return "La nueva contraseña debe tener al menos 8 caracteres";
        }

        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
        return null;
    }

    /**
     * Retorna el historial de compras del usuario ordenado por fecha descendente.
     */
    public List<Venta> obtenerHistorialCompras(Usuario usuario) {
        return ventaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }
}
