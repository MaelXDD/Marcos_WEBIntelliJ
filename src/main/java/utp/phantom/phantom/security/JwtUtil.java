package utp.phantom.phantom.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta de al menos 256 bits para HS256
    private static final String SECRET = "phantom-secret-key-super-segura-2025-utp";
    private static final long EXPIRATION_MS = 86_400_000; // 24 horas

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Genera un token JWT con el email y rol del usuario.
     */
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el email (subject) del token.
     */
    public String extraerEmail(String token) {
        return parsear(token).getPayload().getSubject();
    }

    /**
     * Extrae el rol del token.
     */
    public String extraerRol(String token) {
        return parsear(token).getPayload().get("rol", String.class);
    }

    /**
     * Valida que el token sea correcto y no haya expirado.
     */
    public boolean validarToken(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
