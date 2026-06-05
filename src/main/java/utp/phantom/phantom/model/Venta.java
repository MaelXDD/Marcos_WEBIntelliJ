package utp.phantom.phantom.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private BigDecimal total;

    private Integer cantidadItems;

    @OneToMany(mappedBy = "venta",
            cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}