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

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    private String marca;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "imagen_url")
    private String imagenUrl;

    private Integer stock;
}