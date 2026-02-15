package com.comedor.modelo.entidades;

public class Estudiante extends Usuario {
    private String facultad;
    private String carrera;
   

    public Estudiante(String cedula, String contraseña, String carrera, String facultad) {
        super(cedula, contraseña);
        setCarrera(carrera);
        setFacultad(facultad);
    }

    @Override
    public String obtTipo() { return "Estudiante"; }

    // Getters
    public String obtCarrera() { return carrera; }
    public String obtFacultad() { return facultad; }
    
    // Setters
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setFacultad(String facultad) { this.facultad = facultad; }
    

}