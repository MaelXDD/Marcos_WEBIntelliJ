package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utp.phantom.phantom.model.Perfil;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.model.Venta;
import utp.phantom.phantom.service.CarritoService;
import utp.phantom.phantom.service.CustomUserDetailsService.CustomUserDetails;
import utp.phantom.phantom.service.PerfilService;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private CarritoService carritoService;

    // ─── Utilidad: obtiene el email del usuario autenticado ───────────────────
    private String getEmailAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    // ─── Utilidad: agrega atributos comunes al modelo ─────────────────────────
    private void agregarAtributosComunes(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            model.addAttribute("usuarioNombre", userDetails.getNombre().split(" ")[0]);
        }
        model.addAttribute("carritoCount", carritoService.obtenerCarrito(session).size());
    }

    // ─── GET /perfil ──────────────────────────────────────────────────────────
    @GetMapping
    public String verPerfil(Model model, HttpSession session) {
        String email = getEmailAutenticado();
        if (email == null) return "redirect:/login";

        Usuario usuario = perfilService.obtenerUsuarioPorEmail(email);
        Perfil perfil = perfilService.obtenerOCrearPerfil(usuario);
        List<Venta> historial = perfilService.obtenerHistorialCompras(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        model.addAttribute("historial", historial);
        agregarAtributosComunes(model, session);

        return "perfil";
    }

    // ─── POST /perfil/actualizar ──────────────────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizarDatos(
            @RequestParam String nombre,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String biografia,
            Model model, HttpSession session) {

        String email = getEmailAutenticado();
        if (email == null) return "redirect:/login";

        perfilService.actualizarDatosPersonales(email, nombre, direccion, telefono, biografia);

        return "redirect:/perfil?actualizado=true";
    }

    // ─── POST /perfil/cambiar-password ────────────────────────────────────────
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNuevo,
            @RequestParam String passwordConfirm,
            Model model, HttpSession session) {

        String email = getEmailAutenticado();
        if (email == null) return "redirect:/login";

        String error = perfilService.cambiarPassword(email, passwordActual, passwordNuevo, passwordConfirm);

        if (error != null) {
            return "redirect:/perfil?errorPassword=" + error;
        }

        return "redirect:/perfil?passwordCambiado=true";
    }
}
