package com.comedor.modelo.entidades;

public class Administrador extends Usuario {
    private String codigoAdministrador; // código de verificación para registro

    public Administrador(String cedula, String nombre, String apellido, String email, String contraseña, String codigoAdministrador) {
        super(cedula, nombre, apellido, email, contraseña);
        this.codigoAdministrador = codigoAdministrador;
    }

    @Override
    public String getTipo() { return "Administrador"; }

    public String getCodigoAdministrador() { return codigoAdministrador; }
    public void setCodigoAdministrador(String codigoAdministrador) { this.codigoAdministrador = codigoAdministrador; }

    // Por poner algo
    public void gestionarUsuarios() { }
    public void gestionarReservas() { }
}