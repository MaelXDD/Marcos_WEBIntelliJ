package utp.phantom.phantom.mapper;

import org.springframework.stereotype.Component;
import utp.phantom.phantom.dto.DetalleVentaDTO;
import utp.phantom.phantom.model.DetalleVenta;

@Component
public class DetalleVentaMapper {

    // Entidad → DTO
    public DetalleVentaDTO toDTO(DetalleVenta detalle) {
        if (detalle == null) return null;

        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());

        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
            dto.setProductoNombre(detalle.getProducto().getNombre());
            dto.setProductoImagenUrl(detalle.getProducto().getImagenUrl());
        }

        return dto;
    }
}
