package utp.phantom.phantom.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "perfiles")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "biografia")
    private String biografia;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude
    private Usuario usuario;
}