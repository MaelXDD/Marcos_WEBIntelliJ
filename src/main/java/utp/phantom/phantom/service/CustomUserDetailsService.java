package utp.phantom.phantom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utp.phantom.phantom.repository.UsuarioRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(u -> new CustomUserDetails(
                        u.getEmail(),
                        u.getPassword(),
                        u.getNombre(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    public static class CustomUserDetails implements UserDetails {

        private final String email;
        private final String password;
        private final String nombre;
        private final Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetails(String email, String password, String nombre,
                                 Collection<? extends GrantedAuthority> authorities) {
            this.email = email;
            this.password = password;
            this.nombre = nombre;
            this.authorities = authorities;
        }

        public String getNombre() { return nombre; }

        @Override public String getUsername() { return email; }
        @Override public String getPassword() { return password; }
        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
    }
}