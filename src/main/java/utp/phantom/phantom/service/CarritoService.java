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
                        Double precio, String imagenUrl, int stock) { // <-- Añadido int stock

        List<ItemCarrito> carrito = obtenerCarrito(session);
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();

            if (item.getCantidad() < item.getStock()) {
                item.setCantidad(item.getCantidad() + 1);
            }
        } else {

            carrito.add(new ItemCarrito(productoId, nombre, precio, imagenUrl, 1, stock));
        }
        session.setAttribute(SESSION_KEY, carrito);
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
            eliminar(session, productoId);
            return;
        }

        carrito.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(i -> {

                    if (cantidad > i.getStock()) {
                        i.setCantidad(i.getStock());
                    } else {
                        i.setCantidad(cantidad);
                    }
                });

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