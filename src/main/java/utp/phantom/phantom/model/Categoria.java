package utp.phantom.phantom.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Producto> productos;
}