package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.phantom.phantom.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {
}