package utp.phantom.phantom.mapper;

import org.springframework.stereotype.Component;
import utp.phantom.phantom.dto.UsuarioDTO;
import utp.phantom.phantom.model.Usuario;

@Component
public class UsuarioMapper {

    // Entidad → DTO
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setDni(usuario.getDni());
        dto.setDireccion(usuario.getDireccion());
        dto.setNumeroTelefono(usuario.getNumeroTelefono());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        return dto;
    }

    // DTO → Entidad
    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        if (dto.getPassword() != null) usuario.setPassword(dto.getPassword());
        if (dto.getRol() != null) usuario.setRol(dto.getRol());
        usuario.setDni(dto.getDni());
        usuario.setDireccion(dto.getDireccion());
        usuario.setNumeroTelefono(dto.getNumeroTelefono());
        return usuario;
    }

    // Actualizar entidad existente con datos del DTO
    public void updateEntityFromDTO(UsuarioDTO dto, Usuario usuario) {
        if (dto.getNombre() != null)          usuario.setNombre(dto.getNombre());
        if (dto.getEmail() != null)           usuario.setEmail(dto.getEmail());
        if (dto.getDireccion() != null)       usuario.setDireccion(dto.getDireccion());
        if (dto.getNumeroTelefono() != null)  usuario.setNumeroTelefono(dto.getNumeroTelefono());
        if (dto.getRol() != null)             usuario.setRol(dto.getRol());
    }
}
