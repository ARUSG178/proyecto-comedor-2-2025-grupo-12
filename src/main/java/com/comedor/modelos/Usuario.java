package com.comedor.modelos;

public abstract class Usuario {
    private final String cedula;  
    private String nombre;
    private String apellido;
    private String email;
    private String contraseña;
    private boolean estado;
    private int intentosFallidos;

    public Usuario(String cedula, String nombre, String apellido, String email, String contraseña) {
        this.cedula = cedula;
        setNombre(nombre);
        setApellido(apellido);
        setEmail(email);
        setContraseña(contraseña);
        this.estado = true;      
        this.intentosFallidos = 0;  
    }

    // Getters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContraseña() { return contraseña; }
    public boolean isEstado() { return estado; }
    public int getIntentosFallidos() { return intentosFallidos; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    // Setter abstracto
    public abstract String getTipo();
}