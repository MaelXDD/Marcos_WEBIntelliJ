package utp.phantom.phantom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Permitir todas las rutas sin login
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // Deshabilitar CSRF para que el fetch AJAX funcione
                .csrf(csrf -> csrf.disable())
                // Deshabilitar el login automático de Spring Security
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}