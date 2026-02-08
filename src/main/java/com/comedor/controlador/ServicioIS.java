package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.validaciones.VSesion;
import com.comedor.modelo.persistencia.RepositorioUsuarios;

import java.io.IOException;
import java.util.List;

public class ServicioIS {

    public Usuario IniciarSesion(Usuario uIngresado)
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

        // Verificar que el tipo seleccionado coincida con el tipo en BD
        if (uBD instanceof Administrador && uIngresado instanceof Estudiante) {
            throw new InvalidCredentialsException(
                "Esta cuenta es de administrador. " +
                "Por favor, seleccione 'Administrador' en el tipo de usuario.");
        }
        
        if (uBD instanceof Estudiante && uIngresado instanceof Administrador) {
            throw new InvalidCredentialsException(
                "Esta cuenta es de estudiante. " +
                "Por favor, seleccione 'Estudiante' en el tipo de usuario.");
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
        
        // CAMBIO: Retornar el usuario encontrado
        return uBD;
    }
}