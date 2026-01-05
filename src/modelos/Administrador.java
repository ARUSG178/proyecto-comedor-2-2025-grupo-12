package modelos;

public class Administrador extends Usuario {

    public Administrador(String cedula, String nombre, String apellido, String email, String contraseña) {
        super(cedula, nombre, apellido, email, contraseña);
    }

    @Override
    public String getTipo() {
        return "Administrador";
    }

    public void gestionarUsuarios() {
        // lógica 
    }

    public void gestionarReservas() {
        // lógica 
    }
}