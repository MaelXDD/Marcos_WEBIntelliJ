package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // PATCH: Actualización parcial de un usuario
    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> actualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> actualizaciones) {
        return usuarioRepository.findById(id).map(usuario -> {

            if (actualizaciones.containsKey("nombre")) {
                usuario.setNombre((String) actualizaciones.get("nombre"));
            }
            if (actualizaciones.containsKey("email")) {
                usuario.setEmail((String) actualizaciones.get("email"));
            }
            if (actualizaciones.containsKey("direccion")) {
                usuario.setDireccion((String) actualizaciones.get("direccion"));
            }
            if (actualizaciones.containsKey("numeroTelefono")) {
                usuario.setNumeroTelefono((String) actualizaciones.get("numeroTelefono"));
            }

            Usuario guardado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(guardado);

        }).orElse(ResponseEntity.notFound().build());
    }
    // GET: Obtener todos los usuarios
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // GET: Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Crear usuario
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    // PUT: Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario detalles) {
        Optional<Usuario> existente = usuarioRepository.findById(id);

        if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setNombre(detalles.getNombre());
            u.setEmail(detalles.getEmail());
            u.setPassword(detalles.getPassword());
            u.setRol(detalles.getRol());
            u.setDni(detalles.getDni());
            u.setDireccion(detalles.getDireccion());
            u.setNumeroTelefono(detalles.getNumeroTelefono());

            usuarioRepository.save(u);
            return ResponseEntity.ok(u);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}