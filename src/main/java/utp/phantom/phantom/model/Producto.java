package utp.phantom.phantom.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 100)
    private String marca;

    // Mantenemos este campo primitivo read-only para compatibilidad con JS existente
    @Column(name = "categoria_id", insertable = false, updatable = false)
    private Long categoriaId;

    // Relación completa usada por el panel administrativo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "imagen_url", length = 1000)
    private String imagenUrl;

    @Column(nullable = false)
    private Integer stock;
}