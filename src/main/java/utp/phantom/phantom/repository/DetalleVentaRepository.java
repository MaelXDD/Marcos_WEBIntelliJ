package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utp.phantom.phantom.model.DetalleVenta;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);
}