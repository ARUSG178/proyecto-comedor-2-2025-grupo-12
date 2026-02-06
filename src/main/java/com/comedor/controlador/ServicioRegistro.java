package com.comedor.controlador;// paquete del controlador

<<<<<<< HEAD
import com.comedor.modelo.entidades.Usuario;// importa entidad usuario
import com.comedor.modelo.excepciones.*;// importa excepciones
import com.comedor.modelo.persistencia.RepositorioUsuarios;// importa repositorio
import com.comedor.modelo.validaciones.VRegistro;// importa validador
=======
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.RepositorioUsuarios;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.validaciones.VRegistro;
>>>>>>> 6959cdca3baeb7b6d2fb95dc2c71d36cd1d9c6b2

import java.io.IOException;// importa excepcion de entrada salida
import java.util.List;// importa lista

public class ServicioRegistro {// servicio para registrar usuarios
    private final RepositorioUsuarios repositorio;// repositorio de usuarios

    public ServicioRegistro() {// constructor de la clase
        this.repositorio = new RepositorioUsuarios(); // inicializa el repositorio
    }

    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidEmailFormatException, InvalidCredentialsException, DuplicateUserException, IOException {// registra usuario con validaciones
        // 1. cargar usuarios para verificar duplicados
        List<Usuario> usuariosRegistrados = repositorio.listarUsuarios();// obtiene usuarios actuales

        // 2. preparar validacion
        VRegistro validador = new VRegistro(nuevoUsuario, usuariosRegistrados);// crea el validador
        validador.validar();// ejecuta las validaciones

<<<<<<< HEAD
        // 3. si todo esta bien guardar
        repositorio.guardarUsuario(nuevoUsuario);// guarda en el archivo
        
        System.out.println("Usuario registrado exitosamente en el sistema: " + nuevoUsuario.getEmail());// mensaje de exito
=======
        // 3. Si pasa las validaciones, guardar en el archivo TXT
        repositorio.guardarUsuario(nuevoUsuario);//Guarda el nuevo usuario en el archivo TXT si pasa todas las validaciones
        // Si era administrador y se guardó correctamente, pedir al validador que consuma el código
        if (nuevoUsuario instanceof Administrador) {
            try {
                validador.consumirCodigoAdministrador();
            } catch (IOException ex) {
                // No detener el registro por fallo al actualizar el archivo de códigos
                System.err.println("Advertencia: no se pudo actualizar codigos_admin.txt: " + ex.getMessage());
            }
        }

        System.out.println("Usuario registrado exitosamente en el sistema: " + nuevoUsuario.getEmail());//Mensaje de confirmacion de registro exitoso
>>>>>>> 6959cdca3baeb7b6d2fb95dc2c71d36cd1d9c6b2
    }

    
}
