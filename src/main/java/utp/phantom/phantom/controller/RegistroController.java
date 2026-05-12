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
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "registro";
        }

        if (usuarioService.existeEmail(email)) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        usuarioService.registrar(nombre, email, password, dni, direccion);
        return "redirect:/registro?exitoso=true";
    }
}