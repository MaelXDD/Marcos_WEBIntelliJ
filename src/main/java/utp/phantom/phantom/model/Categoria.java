package utp.phantom.phantom.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;
}