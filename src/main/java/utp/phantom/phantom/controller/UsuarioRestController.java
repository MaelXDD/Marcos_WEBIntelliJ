package utp.phantom.phantom.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.dto.UsuarioDTO;
import utp.phantom.phantom.mapper.UsuarioMapper;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @GetMapping
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ResponseEntity.ok(usuarioMapper.toDTO(usuarioRepository.save(usuario)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO dto) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuarioMapper.updateEntityFromDTO(dto, usuario);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            return ResponseEntity.ok(usuarioMapper.toDTO(usuarioRepository.save(usuario)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> actualizaciones) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (actualizaciones.containsKey("nombre"))
                usuario.setNombre((String) actualizaciones.get("nombre"));
            if (actualizaciones.containsKey("email"))
                usuario.setEmail((String) actualizaciones.get("email"));
            if (actualizaciones.containsKey("direccion"))
                usuario.setDireccion((String) actualizaciones.get("direccion"));
            if (actualizaciones.containsKey("numeroTelefono"))
                usuario.setNumeroTelefono((String) actualizaciones.get("numeroTelefono"));
            return ResponseEntity.ok(usuarioMapper.toDTO(usuarioRepository.save(usuario)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}