package utp.phantom.phantom.service;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import utp.phantom.phantom.model.ItemCarrito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private static final String SESSION_KEY = "carrito";

    @SuppressWarnings("unchecked")
    public List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito =
                (List<ItemCarrito>) session.getAttribute(SESSION_KEY);

        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(SESSION_KEY, carrito);
        }
        return carrito;
    }

    public void agregar(HttpSession session,
                        Long productoId, String nombre,
                        Double precio, String imagenUrl) {

        List<ItemCarrito> carrito = obtenerCarrito(session);

        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            // Ya existe → solo incrementar cantidad
            existente.get().setCantidad(existente.get().getCantidad() + 1);
        } else {
            // No existe → agregar nuevo ítem con cantidad 1
            carrito.add(new ItemCarrito(productoId, nombre, precio, imagenUrl, 1));
        }

        session.setAttribute(SESSION_KEY, carrito); // guardar en sesión
    }

    public void eliminar(HttpSession session, Long productoId) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(i -> i.getProductoId().equals(productoId));
        session.setAttribute(SESSION_KEY, carrito);
    }


    public void actualizarCantidad(HttpSession session,
                                   Long productoId, int cantidad) {
        List<ItemCarrito> carrito = obtenerCarrito(session);

        if (cantidad <= 0) {
            // Si la cantidad llega a 0 o menos, eliminar el ítem
            eliminar(session, productoId);
            return;
        }

        carrito.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(i -> i.setCantidad(cantidad));

        session.setAttribute(SESSION_KEY, carrito);
    }

    public void vaciar(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public Double calcularTotal(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    public int contarItems(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
}