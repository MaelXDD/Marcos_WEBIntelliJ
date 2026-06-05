package utp.phantom.phantom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "etiquetas")
@Data
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la etiqueta no puede estar vacío")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    // RELACIÓN @ManyToMany (Lado inverso): Mapeado por el atributo 'etiquetas' en Producto
    @ManyToMany(mappedBy = "etiquetas", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Producto> productos;
}