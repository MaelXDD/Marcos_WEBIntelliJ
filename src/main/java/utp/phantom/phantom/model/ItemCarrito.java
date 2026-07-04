package utp.phantom.phantom.model;

import java.io.Serializable;

public class ItemCarrito implements Serializable {

    private Long productoId;
    private String nombre;
    private Double precio;
    private String imagenUrl;
    private int cantidad;
    private int stock;

    public ItemCarrito(Long productoId, String nombre, Double precio,
                       String imagenUrl, int cantidad, int stock) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.cantidad = cantidad;
        this.stock = stock;
    }

    public Double getSubtotal() {
        return precio * cantidad;
    }

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Getter y Setter del stock
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}