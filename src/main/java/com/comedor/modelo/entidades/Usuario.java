package com.comedor.modelo.entidades;

// Clase abstracta que representa un usuario del sistema

public abstract class Usuario {
    private final String cedula;  
    private String contraseña;
    private boolean estado;
    private int intentosFallidos;
    private double saldo;

// Constructor de la clase Usuario

    public Usuario() {
        this.cedula = "";
        this.contraseña = "";
    }
    
    public Usuario(String cedula, String contraseña) {
        this.cedula = cedula;
        this.contraseña = contraseña;
        this.estado = true;      
        this.intentosFallidos = 0;
        this.saldo = 0.0;
    }

    // Getters
    public String obtCedula() { return cedula; }
    public String obtContraseña() { return contraseña; }
    public boolean obtEstado() { return estado; }
    public int obtIntentosFallidos() { return intentosFallidos; }
    public double obtSaldo() { return saldo; }

    // Setters
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    // Setter abstracto
    public abstract String obtTipo();
}