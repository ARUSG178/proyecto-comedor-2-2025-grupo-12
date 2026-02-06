package com.comedor.controlador;// paquete donde se encuentra la clase

import com.comedor.modelo.User;// importa la clase user del modelo
import java.util.ArrayList;// importa arraylist para manejar listas dinamicas
import java.util.List;// importa la interfaz list

public class AuthService {// clase publica para el servicio de autenticacion
    private List<User> users = new ArrayList<>();// lista privada para almacenar los usuarios registrados

    public void register(String username, String password, String role) {// metodo publico para registrar un nuevo usuario
        users.add(new User(username, password, role));// crea un nuevo usuario y lo agrega a la lista
        System.out.println("Usuario registrado: " + username);// imprime un mensaje confirmando el registro
    }

    public boolean login(String username, String password) {// metodo para iniciar sesion verificando credenciales
        for (User user : users) {// recorre la lista de usuarios registrados
            if (user.getUsername().equals(username) && user.checkPassword(password)) {// verifica si el nombre y la contrasena coinciden
                System.out.println("Login exitoso: " + username);// imprime mensaje de exito si coincide
                return true;// retorna verdadero indicando exito
            }
        }
        System.out.println("Login fallido.");// imprime mensaje de fallo si no encuentra coincidencia
        return false;// retorna falso indicando error
    }
}