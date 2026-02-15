package com.comedor.modelo.validaciones;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;

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

    boolean validarCedula() {
        return uIngresado.obtCedula().trim().equals(uBaseDatos.obtCedula().trim());
    }

    boolean validarContraseña() {
        return uIngresado.obtContraseña().equals(uBaseDatos.obtContraseña());
    }

    public void validar() throws InvalidCredentialsException {

        if (!(validarCedula() && validarContraseña())) {
            uBaseDatos.setIntentosFallidos(uBaseDatos.obtIntentosFallidos() + 1);

            if (uBaseDatos.obtIntentosFallidos() >= 3) {
                uBaseDatos.setEstado(false);
                throw new InvalidCredentialsException("Cuenta bloqueada por múltiples intentos fallidos");
            }

            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }
    }
}