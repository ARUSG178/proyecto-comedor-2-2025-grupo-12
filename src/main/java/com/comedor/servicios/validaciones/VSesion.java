package com.comedor.servicios.validaciones;

import com.comedor.modelos.Usuario;
import com.comedor.servicios.excepciones.*;
import com.comedor.util.ValidacionUtil;

public class VSesion {
    private final Usuario uIngresado;
    private final Usuario uBaseDatos;

    public VSesion(Usuario uIngresado, Usuario uBaseDatos) {
        if (uIngresado == null || uBaseDatos == null) {
            throw new IllegalArgumentException("Los objetos de usuario no pueden ser nulos");
        }
        this.uIngresado = uIngresado;
        this.uBaseDatos = uBaseDatos;
    }

    boolean validarCorreo() {
        return uIngresado.getEmail().trim().equals(uBaseDatos.getEmail().trim());
    }

    boolean validarContraseña() {
        return uIngresado.getContraseña().equals(uBaseDatos.getContraseña());
    }

    public void validar() throws InvalidEmailFormatException, InvalidCredentialsException {

        if (!ValidacionUtil.formatoCorreo(uIngresado.getEmail())) {
            throw new InvalidEmailFormatException("El correo no es institucional");
        }

        if (!(validarCorreo() && validarContraseña())) {
            uBaseDatos.setIntentosFallidos(uBaseDatos.getIntentosFallidos() + 1);

            if (uBaseDatos.getIntentosFallidos() >= 3) {
                uBaseDatos.setEstado(false);
                throw new InvalidCredentialsException("Cuenta bloqueada por múltiples intentos fallidos");
            }

            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }
    }
}