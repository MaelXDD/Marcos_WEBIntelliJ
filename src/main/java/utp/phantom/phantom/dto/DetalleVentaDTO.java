package utp.phantom.phantom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaDTO {

    private Long id;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    private Long productoId;

    private String productoNombre;

    private String productoImagenUrl;
}
