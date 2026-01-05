package servicios.validaciones;

import modelos.Usuario;

public class ValidarInicioSesion {
    private final Usuario usuarioIngresado;
    private final Usuario usuarioBD;

    // Constructor recibe ambos objetos
    public ValidarInicioSesion(Usuario usuarioIngresado, Usuario usuarioBD) {
        this.usuarioIngresado = usuarioIngresado;
        this.usuarioBD = usuarioBD;
    }

    boolean vFCorreo() { return usuarioIngresado.getEmail().endsWith("@ciens.ucv.ve"); }

    boolean veriCorreo() { return usuarioIngresado.getEmail().equals(usuarioBD.getEmail()); }
     
    boolean veriContraseña() { return usuarioIngresado.getContraseña().equals(usuarioBD.getContraseña()); }

    public String validarCredenciales() {
        if (!vFCorreo()) { return "Correo no institucional"; }
        // Comprar con base de datos
        if (!(veriCorreo()) || veriContraseña()) { return "Usuario o contraseña incorrectos"; }

        return "Inicio de sesión exitoso";
    }
}