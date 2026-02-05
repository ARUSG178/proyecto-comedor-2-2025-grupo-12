package com.comedor.controlador;

import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.persistencia.RepositorioUsuarios;
import com.comedor.modelo.validaciones.VRegistro;

import java.io.IOException;
import java.util.List;

public class ServicioRegistro {//Servicio para registrar nuevos usuarios en el sistema
    private final RepositorioUsuarios repositorio;//Repositorio para manejar el almacenamiento de usuarios

    public ServicioRegistro() {//Constructor que inicializa el repositorio de usuarios
        this.repositorio = new RepositorioUsuarios();//Instancia del repositorio de usuarios
    }

    public void registrarUsuario(Usuario nuevoUsuario) throws InvalidEmailFormatException, InvalidCredentialsException, DuplicateUserException, IOException {//Metodo para registrar un nuevo usuario, lanza varias excepciones en caso de errores
        // 1. Cargar usuarios desde el TXT para verificar duplicados
        List<Usuario> usuariosRegistrados = repositorio.listarUsuarios();//Carga la lista de usuarios ya registrados desde el archivo TXT

        // 2. Instanciar el validador existente y ejecutar validaciones
        VRegistro validador = new VRegistro(nuevoUsuario, usuariosRegistrados);//Crea una instancia del validador de registro con el nuevo usuario y la lista de usuarios existentes
        validador.validar();//Ejecuta las validaciones definidas en el validador

        // 3. Si pasa las validaciones, guardar en el archivo TXT
        repositorio.guardarUsuario(nuevoUsuario);//Guarda el nuevo usuario en el archivo TXT si pasa todas las validaciones
        
        System.out.println("Usuario registrado exitosamente en el sistema: " + nuevoUsuario.getEmail());//Mensaje de confirmacion de registro exitoso
    }
}
