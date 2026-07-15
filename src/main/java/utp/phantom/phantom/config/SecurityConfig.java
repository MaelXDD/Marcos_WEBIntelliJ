package utp.phantom.phantom.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import utp.phantom.phantom.security.JwtAuthenticationFilter;
import utp.phantom.phantom.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication) -> {

            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (esAdmin) {
                response.sendRedirect("/admin/productos");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // DESHABILITAR CSRF PARA PERMITIR POST, PUT Y DELETE EN LA API
                .csrf(csrf -> csrf.disable())

                // CONFIGURACIÓN DE RUTAS
                .authorizeHttpRequests(auth -> auth

                        // ── Rutas públicas (Thymeleaf + buscador) ──────────────
                        .requestMatchers(
                                "/", "/buscar", "/api/productos/buscar", "/nosotros", "/mision",
                                "/registro", "/login",
                                "/CSS/**", "/Imagenes/**", "/JS/**",
                                "/imagenes/**", "/categoria/**",
                                "/carrito/**",
                                "/producto/**"
                        ).permitAll()

                        // ── Login JWT (obtener token) — público ────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── API REST v1: Spring Security las deja pasar,
                        //    pero el JwtAuthenticationFilter valida el token ────
                        .requestMatchers("/api/v1/**").permitAll()

                        // ── Rutas Thymeleaf protegidas con sesión ──────────────
                        .requestMatchers("/pago/**").authenticated()
                        .requestMatchers("/perfil/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // Login web con formulario (Thymeleaf) — no afecta a la API
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                // Filtro JWT se ejecuta ANTES del filtro de autenticación de Spring.
                // Intercepta /api/v1/** y valida el token manualmente.
                // Las rutas Thymeleaf no son afectadas porque el filtro
                // hace return sin tocarlas si el path no empieza con /api/v1/
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
