package utp.phantom.phantom.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private Double precio;

    private String marca;

    private String imagenUrl;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private String categoriaNombre;
}