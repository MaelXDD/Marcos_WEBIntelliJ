package utp.phantom.phantom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.phantom.phantom.model.Venta;

@Repository
public interface VentaRepository
        extends JpaRepository<Venta, Long> {
}