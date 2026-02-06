package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.validaciones.VSesion;

public class ServicioIS {
    public void IniciarSesion(Usuario uIngresado, Usuario uBD) throws InvalidCredentialsException, InvalidEmailFormatException {
        VSesion persona = new VSesion(uIngresado, uBD);
        persona.validar();

        // temporal, no veo aún
        System.out.println("Inicio de sesión exitoso para: " + uBD.getEmail());
    }
}
