package utp.phantom.phantom.mapper;

import org.springframework.stereotype.Component;
import utp.phantom.phantom.dto.ProductoDTO;
import utp.phantom.phantom.model.Categoria;
import utp.phantom.phantom.model.Producto;

@Component
public class ProductoMapper {

    // Entidad → DTO
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setMarca(producto.getMarca());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setStock(producto.getStock());

        // Extraer solo id y nombre de la categoría (evita exponer toda la lista de productos)
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return dto;
    }

    // DTO → Entidad
    public Producto toEntity(ProductoDTO dto) {
        if (dto == null) return null;

        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setMarca(dto.getMarca());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setStock(dto.getStock());

        // Asignar solo el ID de categoría; el service resuelve la entidad completa
        if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        }

        return producto;
    }

    // Actualizar entidad existente con datos del DTO
    public void updateEntityFromDTO(ProductoDTO dto, Producto producto) {
        if (dto.getNombre() != null)      producto.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) producto.setDescripcion(dto.getDescripcion());
        if (dto.getPrecio() != null)      producto.setPrecio(dto.getPrecio());
        if (dto.getMarca() != null)       producto.setMarca(dto.getMarca());
        if (dto.getImagenUrl() != null)   producto.setImagenUrl(dto.getImagenUrl());
        if (dto.getStock() != null)       producto.setStock(dto.getStock());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        }
    }
}
