package com.comedor.modelo.entidades;

// Clase abstracta que representa un usuario del sistema

public abstract class Usuario {
    private final String cedula;  
    private String nombre;
    private String apellido;
    private String email;
    private String contraseña;
    private boolean estado;
    private int intentosFallidos;
    private double saldo;

// Constructor de la clase Usuario

    public Usuario() {
        this.cedula = "";
        this.nombre = "";
        this.apellido = "";
        this.email = "";
        this.contraseña = "";
    }
    
    public Usuario(String cedula, String nombre, String apellido, String email, String contraseña) {
        this.cedula = cedula;
        setNombre(nombre);
        setApellido(apellido);
        setEmail(email);
        setContraseña(contraseña);
        this.estado = true;      
        this.intentosFallidos = 0;
        this.saldo = 0.0;
    }

    // Getters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContraseña() { return contraseña; }
    public boolean isEstado() { return estado; }
    public int getIntentosFallidos() { return intentosFallidos; }

    public double getSaldo() { return saldo; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    // Setter abstracto
    public abstract String getTipo();
}