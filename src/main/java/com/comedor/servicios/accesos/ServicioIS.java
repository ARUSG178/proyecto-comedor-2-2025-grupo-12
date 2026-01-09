package com.comedor.servicios.accesos;

import com.comedor.modelos.Usuario;
import com.comedor.servicios.validaciones.VSesion;
import com.comedor.servicios.excepciones.*;

public class ServicioIS {
    public void IniciarSesion(Usuario uIgresado, Usuario uBD) throws InvalidCredentialsException, InvalidEmailFormatException {
        VSesion persona = new VSesion(uIgresado, uBD);
        persona.validar();

        // temporal, no veo aún
        System.out.println("Inicio de sesión exitoso para: " + uBD.getEmail());
    }
}
