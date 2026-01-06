package servicios.accesos;

import modelos.Usuario;
import servicios.validaciones.VSesion;
import servicios.excepciones.*;

public class ServicioIS {
    public void IniciarSesion(Usuario uIgresado, Usuario uBD) throws InvalidCredentialsException, InvalidEmailFormatException {
        VSesion persona = new VSesion(uIgresado, uBD);
        persona.validar();

        System.out.println("Inicio de sesión exitoso para: " + uBD.getEmail());
    }
}
