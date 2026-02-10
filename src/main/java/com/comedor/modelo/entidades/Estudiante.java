package com.comedor.modelo.entidades;

public class Estudiante extends Usuario {
    private String facultad;
    private String carrera;
   

    public Estudiante(String cedula, String nombre, String apellido, String email, String contraseña, String carrera, String facultad) {
        super(cedula, nombre, apellido, email, contraseña);
        setCarrera(carrera);
        setFacultad(facultad);
    }

    @Override
    public String getTipo() { return "Estudiante"; }

    // Getters
    public String getCarrera() { return carrera; }
    public String getFacultad() { return facultad; }
    
    // Setters
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setFacultad(String facultad) { this.facultad = facultad; }
    

}