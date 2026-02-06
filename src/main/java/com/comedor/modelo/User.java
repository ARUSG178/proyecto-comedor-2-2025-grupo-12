package com.comedor.modelo;// paquete del modelo

public class User {// clase publica user para autenticacion simple
    private String username;// almacena el nombre de usuario
    private String password;// almacena la contrasena
    private String role; // Ej: "ADMIN", "COMENSAL"// almacena el rol del usuario

    public User(String username, String password, String role) {// constructor para crear un nuevo usuario
        this.username = username;// asigna el nombre recibido
        this.password = password;// asigna la contrasena recibida
        this.role = role;// asigna el rol recibido
    }

    public String getUsername() { return username; }// devuelve el nombre de usuario
    public boolean checkPassword(String password) { return this.password.equals(password); }// compara la contrasena ingresada con la guardada
    public String getRole() { return role; }// devuelve el rol del usuario
}