package com.comedor.controlador;

import com.comedor.modelo.persistencia.*;

 // Centraliza la creación de instancias.

public class ServicioFactory {
    private static ServicioFactory instance;
    
    // Repositorios compartidos
    private final IRepositorioUsuarios repoUsuarios;
    private final IRepositorioSecretaria repoSecretaria;
    private final IRepositorioAdminCdg repoAdminCdg;
    
    private ServicioFactory() {
        // Inicializar repositorios concretos
        this.repoUsuarios = new RepoUsuarios();
        this.repoSecretaria = new RepoSecretaria();
        this.repoAdminCdg = new RepoAdminCdg();
    }
    
    // Obtiene la instancia única de la fábrica.
    public static synchronized ServicioFactory getInstance() {
        if (instance == null) {
            instance = new ServicioFactory();
        }
        return instance;
    }
    
    // Crea un servicio de inicio de sesión con las dependencias inyectadas.
    public ServicioIS crearServicioIS() {
        return new ServicioIS(repoUsuarios);
    }
    
    // Crea un servicio de registro con las dependencias inyectadas.
    public ServicioRegistro crearServicioRegistro() {
        return new ServicioRegistro(repoUsuarios, repoSecretaria, repoAdminCdg);
    }
    
    // Obtiene el repositorio de usuarios.
    public IRepositorioUsuarios getRepositorioUsuarios() {
        return repoUsuarios;
    }
    
    // Obtiene el repositorio de secretaria.
    public IRepositorioSecretaria getRepositorioSecretaria() {
        return repoSecretaria;
    }
    
    // Obtiene el repositorio de códigos de administrador.
    public IRepositorioAdminCdg getRepositorioAdminCdg() {
        return repoAdminCdg;
    }
}
