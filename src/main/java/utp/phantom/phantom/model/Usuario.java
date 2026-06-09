package utp.phantom.phantom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String rol = "USER";

    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
    @Column(length = 8, unique = true)
    private String dni;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String direccion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    @Column(name = "numero_telefono", length = 9, unique = true)
    private String numeroTelefono;

    // RELACIÓN @OneToOne
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita bucles infinitos al imprimir el objeto
    private Perfil perfil;
}