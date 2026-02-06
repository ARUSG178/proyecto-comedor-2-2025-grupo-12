package com.comedor.controlador;// paquete del controlador

import com.comedor.modelo.entidades.Usuario;// importa entidad usuario
import com.comedor.modelo.excepciones.*;// importa excepciones
import com.comedor.modelo.persistencia.RepositorioUsuarios;// importa repositorio
import com.comedor.modelo.validaciones.VRegistro;// importa validador

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

        // 3. si todo esta bien guardar
        repositorio.guardarUsuario(nuevoUsuario);// guarda en el archivo
        
        System.out.println("Usuario registrado exitosamente en el sistema: " + nuevoUsuario.getEmail());// mensaje de exito
    }
}
