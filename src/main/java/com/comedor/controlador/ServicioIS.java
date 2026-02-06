package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.validaciones.VSesion;
import com.comedor.modelo.persistencia.RepositorioUsuarios;

import java.io.IOException;
import java.util.List;

public class ServicioIS {
<<<<<<< HEAD
    public void IniciarSesion(Usuario uIngresado, Usuario uBD) throws InvalidCredentialsException, InvalidEmailFormatException {
        VSesion persona = new VSesion(uIngresado, uBD);
        persona.validar();
=======
>>>>>>> 6959cdca3baeb7b6d2fb95dc2c71d36cd1d9c6b2

    public void IniciarSesion(Usuario uIngresado)
            throws InvalidCredentialsException, InvalidEmailFormatException, IOException {

        RepositorioUsuarios repo = new RepositorioUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();

        Usuario uBD = null;
        for (Usuario u : usuarios) {
            if (u != null && u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(uIngresado.getEmail().trim())) {
                uBD = u;
                break;
            }
        }

        if (uBD == null) {
            throw new InvalidCredentialsException("Usuario no encontrado");
        }

        VSesion validador = new VSesion(uIngresado, uBD);
        try {
            validador.validar();
            System.out.println("LOG: Inicio de sesión exitoso para: " + uBD.getEmail());
        } catch (InvalidCredentialsException | InvalidEmailFormatException ex) {
            // Aun en fallo, VSesion puede haber modificado intentos/estado -> persistir y re-lanzar
            // actualizamos la lista y guardamos
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario x = usuarios.get(i);
                if (x != null && x.getEmail() != null && x.getEmail().trim().equalsIgnoreCase(uBD.getEmail().trim())) {
                    usuarios.set(i, uBD);
                    break;
                }
            }
            repo.guardarTodos(usuarios);
            throw ex;
        }

        // Si ha sido exitoso, también guardamos posibles cambios (ej. intentosFallidos = 0)
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario x = usuarios.get(i);
            if (x != null && x.getEmail() != null && x.getEmail().trim().equalsIgnoreCase(uBD.getEmail().trim())) {
                usuarios.set(i, uBD);
                break;
            }
        }
        repo.guardarTodos(usuarios);
    }
}