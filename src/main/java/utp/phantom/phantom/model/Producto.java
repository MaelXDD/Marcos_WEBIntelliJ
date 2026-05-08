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

    // La categoría debe coincidir con los slugs: consolas, juegos, perifericos, tarjetas, sillas
    @Column(nullable = false)
    private String categoria;

    // URL de imagen (puede ser de Supabase Storage o externa)
    private String imagenUrl;

    private Integer stock;
}
