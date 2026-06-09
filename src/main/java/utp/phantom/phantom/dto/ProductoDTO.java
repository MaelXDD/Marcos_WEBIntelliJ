package utp.phantom.phantom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private Double precio;

    private String marca;

    private String imagenUrl;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    // Solo se expone el ID y nombre de la categoría (sin traer toda la lista de productos)
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private String categoriaNombre;
}
