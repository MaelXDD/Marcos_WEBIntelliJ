package utp.phantom.phantom.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utp.phantom.phantom.model.ItemCarrito;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.repository.UsuarioRepository;
import utp.phantom.phantom.service.CarritoService;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;
import utp.phantom.phantom.service.VentaService;
import utp.phantom.phantom.exception.StockInsuficienteException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    private void agregarUsuarioAutenticado(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                String primerNombre = userDetails.getNombre().split(" ")[0];
                model.addAttribute("usuarioNombre", primerNombre);
            } else {
                model.addAttribute("usuarioNombre", auth.getName());
            }
        }
    }


    @PostMapping("/procesar")
    public String procesarPago(HttpSession session, Model model) {

        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/carrito";
        }

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (ItemCarrito item : items) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getCantidad())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("pen")
                                    .setUnitAmount((long) (item.getPrecio() * 100))
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getNombre())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            lineItems.add(lineItem);
        }

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8089/pago/confirmar?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:8089/carrito")
                    .addAllLineItem(lineItems)
                    .build();

            Session checkoutSession = Session.create(params);
            return "redirect:" + checkoutSession.getUrl();

        } catch (StripeException e) {
            model.addAttribute("erroresPago", List.of("Error al conectar con Stripe: " + e.getMessage()));
            agregarUsuarioAutenticado(model);
            model.addAttribute("items", items);
            model.addAttribute("total", carritoService.calcularTotal(session));
            model.addAttribute("cuenta", carritoService.contarItems(session));
            return "carrito";
        }
    }


    @GetMapping("/confirmar")
    public String confirmarPago(@RequestParam("session_id") String sessionId,
                                HttpSession session,
                                Model model,
                                RedirectAttributes flash) {

        try {
            Session checkoutSession = Session.retrieve(sessionId);

            if (!"paid".equals(checkoutSession.getPaymentStatus())) {
                return "redirect:/carrito";
            }

        } catch (StripeException e) {
            return "redirect:/carrito";
        }

        List<ItemCarrito> items = carritoService.obtenerCarrito(session);

        if (items.isEmpty()) {
            return "redirect:/";
        }

        Usuario usuarioActual = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            usuarioActual = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        Venta venta;
        try {
            venta = ventaService.procesarVenta(items, usuarioActual);
        } catch (StockInsuficienteException e) {
            flash.addFlashAttribute("erroresPago", e.getErrores());
            return "redirect:/carrito";                               
        }

        carritoService.vaciar(session);
        model.addAttribute("total", venta.getTotal());
        model.addAttribute("cantidadItems", venta.getCantidadItems());
        model.addAttribute("numeroOrden", venta.getNumeroOrden());
        model.addAttribute("carritoCount", 0);
        agregarUsuarioAutenticado(model);

        return "pago-exitoso";
    }
}