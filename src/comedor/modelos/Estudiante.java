package modelos;

public class Estudiante extends Usuario {
    private String carrera;
    private String codigoEstudiante;

    public Estudiante(String cedula, String nombre, String apellido, String email, String contraseña, String carrera, int semestre, String codigoEstudiante) {
        super(cedula, nombre, apellido, email, contraseña);
        setCarrera(carrera);
        set codigoEstudiante(codigoEstudiante);
    }

    @Override
    public String getTipo() { return "Estudiante"; }

    // Getters
    public String getCarrera() { return carrera; }
    public int getSemestre() { return semestre; }
    public String getCodigoEstudiante() { return codigoEstudiante; }
    
    // Setters
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }
    

}