package com.comedor.modelo.entidades;

public class Administrador extends Usuario {
    private String responsabilidad;

    public Administrador(String cedula, String nombre, String apellido, String email, String contraseña, String responsabilidad) {
        super(cedula, nombre, apellido, email, contraseña);
        setResposabilidad(responsabilidad);
    }

    @Override
    public String getTipo() { return "Administrador"; }

    // Getter
    public String getResponsabilidad() { return responsabilidad; }
    // Setter
    public void setResposabilidad(String responsabilidad) { this.responsabilidad = responsabilidad; } 

    // Por poner algo
    public void gestionarUsuarios() { }
    public void gestionarReservas() { }
}