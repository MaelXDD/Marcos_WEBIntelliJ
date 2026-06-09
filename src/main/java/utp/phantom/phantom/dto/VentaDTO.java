package utp.phantom.phantom.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaDTO {

    private Long id;

    private LocalDateTime fecha;

    private BigDecimal total;

    private Integer cantidadItems;

    private String numeroOrden;

    private String estado;

    private Long usuarioId;

    private String usuarioNombre;

    private String usuarioEmail;

    private List<DetalleVentaDTO> detalles;
}
