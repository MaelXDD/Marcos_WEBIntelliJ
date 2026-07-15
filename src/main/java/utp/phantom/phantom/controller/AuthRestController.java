package utp.phantom.phantom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utp.phantom.phantom.model.Usuario;
import utp.phantom.phantom.repository.UsuarioRepository;
import utp.phantom.phantom.security.JwtUtil;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * POST /api/auth/login
     * Body: { "email": "...", "password": "..." }
     * Responde con el token JWT si las credenciales son correctas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {

        String email    = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email y password son requeridos"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }

        // Genera el token con email y rol del usuario
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

        return ResponseEntity.ok(Map.of(
                "token",  token,
                "email",  usuario.getEmail(),
                "nombre", usuario.getNombre(),
                "rol",    usuario.getRol()
        ));
    }
}
