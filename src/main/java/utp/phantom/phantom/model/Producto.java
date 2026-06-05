package utp.phantom.phantom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    // @NotNull se usa para objetos numéricos, @PositiveOrZero evita precios negativos
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    @Column(nullable = false)
    private Double precio;

    @Column(length = 100)
    private String marca;

    @Column(name = "categoria_id", insertable = false, updatable = false)
    private Long categoriaId;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "imagen_url", length = 1000)
    private String imagenUrl;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // RELACIÓN @ManyToMany: Genera una tabla intermedia automáticamente
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_etiqueta",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    @ToString.Exclude
    private List<Etiqueta> etiquetas;
}