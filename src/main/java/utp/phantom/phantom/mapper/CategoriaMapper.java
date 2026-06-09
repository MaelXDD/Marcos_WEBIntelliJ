package utp.phantom.phantom.mapper;

import org.springframework.stereotype.Component;
import utp.phantom.phantom.dto.CategoriaDTO;
import utp.phantom.phantom.model.Categoria;

@Component
public class CategoriaMapper {

    // Entidad → DTO (para respuestas al cliente)
    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) return null;

        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    // DTO → Entidad (para guardar en base de datos)
    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) return null;

        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

    // Actualizar entidad existente con datos del DTO (para PUT/PATCH)
    public void updateEntityFromDTO(CategoriaDTO dto, Categoria categoria) {
        if (dto.getNombre() != null)      categoria.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) categoria.setDescripcion(dto.getDescripcion());
    }
}
