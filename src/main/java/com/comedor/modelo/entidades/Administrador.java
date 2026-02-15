package com.comedor.modelo.entidades;

public class Administrador extends Usuario {
    private String codigoAdministrador; // código de verificación para registro

    public Administrador(String cedula, String contraseña, String codigoAdministrador) {
        super(cedula, contraseña);
        this.codigoAdministrador = codigoAdministrador;
    }

    public Administrador() {
        super(); // Llama al constructor sin parámetros de Usuario
        this.codigoAdministrador = "";
    }

    @Override
    public String obtTipo() { return "Administrador"; }

    public String obtCodigoAdministrador() { return codigoAdministrador; }
    public void setCodigoAdministrador(String codigoAdministrador) { this.codigoAdministrador = codigoAdministrador; }

    // Por poner algo
    public void gestionarUsuarios() { }
    public void gestionarReservas() { }
}