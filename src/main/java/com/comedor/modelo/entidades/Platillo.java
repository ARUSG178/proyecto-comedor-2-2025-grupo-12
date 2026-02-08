package com.comedor.modelo.entidades;

// Clase que representa un platillo en el menú del comedor
public class Platillo {
    private String nombre;
    private String descripcion;
    private double precio;
    private boolean disponible;

    public Platillo() {
        this.nombre = "";
        this.precio = 0.0;
    }
    
    public Platillo(String nombre, String descripcion, double precio) {// Constructor de la clase Platillo
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = true;
    }

    public String getNombre() { return nombre; }// Getter para el nombre
    public String getDescripcion() { return descripcion; }// Getter para la descripción
    public double getPrecio() { return precio; }/// Getter para el precio
    public boolean isDisponible() { return disponible; }/// Getter para disponibilidad
    public void setNombre(String nombre) { this.nombre = nombre; }// Setter para el nombre
    public void setDisponible(boolean disponible) { this.disponible = disponible; }// Setter para disponibilidad >>>>>>> ced746e1c5c1f179bdb8f594730ed1e2d5b917b5
    public void setPrecio(double precio) { this.precio = precio; }
}
