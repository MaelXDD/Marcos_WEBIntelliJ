package utp.phantom.phantom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utp.phantom.phantom.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    private final UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarRegistro(@RequestParam(required = false) String exitoso,
                                  Model model) {
        if (exitoso != null) {
            model.addAttribute("registroExitoso", true);
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String numeroTelefono,
            Model model) {

        model.addAttribute("nombre", nombre);
        model.addAttribute("email", email);
        model.addAttribute("dni", dni);
        model.addAttribute("direccion", direccion);
        model.addAttribute("numeroTelefono", numeroTelefono);

        if (dni == null || dni.trim().isEmpty()) {
            model.addAttribute("error", "El DNI es obligatorio");
            return "registro";
        }

        if (!dni.matches("\\d{8}")) {
            model.addAttribute("error", "El DNI debe tener exactamente 8 dígitos");
            return "registro";
        }

        if (usuarioService.existeDni(dni)) {
            model.addAttribute("error", "El DNI ya está registrado");
            return "registro";
        }

        if (numeroTelefono == null || numeroTelefono.trim().isEmpty()) {
            model.addAttribute("error", "El número de teléfono es obligatorio");
            return "registro";
        }

        if (usuarioService.existeTelefono(numeroTelefono)) {
            model.addAttribute("error", "El número de teléfono ya está registrado");
            return "registro";
        }

        if (usuarioService.existeEmail(email)) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "registro";
        }

        if (!contrasenaSegura(password)) {
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un símbolo (ej: !, @, #)");
            return "registro";
        }

        usuarioService.registrar(nombre, email, password, dni, direccion, numeroTelefono);
        return "redirect:/registro?exitoso=true";
    }

    private boolean contrasenaSegura(String password) {
        if (password.length() < 8) return false;
        boolean tieneMayuscula = password.chars().anyMatch(Character::isUpperCase);
        boolean tieneMinuscula = password.chars().anyMatch(Character::isLowerCase);
        boolean tieneNumero    = password.chars().anyMatch(Character::isDigit);
        boolean tieneSymbolo   = password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0);
        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneSymbolo;
    }
}