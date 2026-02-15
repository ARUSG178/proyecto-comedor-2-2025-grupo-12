package com.comedor.controlador;// paquete del controlador

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.RepositorioUsuarios;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.validaciones.VRegistro;
import com.comedor.modelo.persistencia.RepoAdminCdg;

import java.io.IOException;// importa excepcion de entrada salida
import java.util.List;// importa lista

public class ServicioRegistro {// servicio para registrar usuarios
    private final RepositorioUsuarios repositorio;// repositorio de usuarios

    public ServicioRegistro() {// constructor de la clase
        this.repositorio = new RepositorioUsuarios(); // inicializa el repositorio
    }

    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidCredentialsException, DuplicateUserException, IOException {// registra usuario con validaciones
        // 1. cargar usuarios para verificar duplicados
        List<Usuario> usuariosRegistrados = repositorio.listarUsuarios();// obtiene usuarios actuales

        // 2. preparar validacion
        VRegistro validador = new VRegistro(nuevoUsuario, usuariosRegistrados);// crea el validador
        validador.validar();// ejecuta las validaciones

        // 3. Si pasa las validaciones, guardar en el archivo TXT
        repositorio.guardarUsuario(nuevoUsuario);//Guarda el nuevo usuario en el archivo TXT si pasa todas las validaciones
        // Si era administrador y se guardó correctamente, pedir al validador que consuma el código
        if (nuevoUsuario instanceof Administrador) {
            RepoAdminCdg repoCdg = new RepoAdminCdg();
            repoCdg.consumirCodigo(((Administrador) nuevoUsuario).obtCodigoAdministrador());
        }

        System.out.println("Usuario registrado exitosamente en el sistema. Cédula: " + nuevoUsuario.obtCedula());//Mensaje de confirmacion de registro exitoso
    }

    
}
