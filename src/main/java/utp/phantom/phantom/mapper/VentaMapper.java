package utp.phantom.phantom.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utp.phantom.phantom.dto.VentaDTO;
import utp.phantom.phantom.model.Venta;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class VentaMapper {

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    // Entidad → DTO
    public VentaDTO toDTO(Venta venta) {
        if (venta == null) return null;

        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setCantidadItems(venta.getCantidadItems());
        dto.setNumeroOrden(venta.getNumeroOrden());
        dto.setEstado(venta.getEstado());

        if (venta.getUsuario() != null) {
            dto.setUsuarioId(venta.getUsuario().getId());
            dto.setUsuarioNombre(venta.getUsuario().getNombre());
            dto.setUsuarioEmail(venta.getUsuario().getEmail());
        }

        // Mapear los detalles usando DetalleVentaMapper
        if (venta.getDetalles() != null) {
            dto.setDetalles(
                venta.getDetalles().stream()
                    .map(detalleVentaMapper::toDTO)
                    .collect(Collectors.toList())
            );
        } else {
            dto.setDetalles(Collections.emptyList());
        }

        return dto;
    }
}
