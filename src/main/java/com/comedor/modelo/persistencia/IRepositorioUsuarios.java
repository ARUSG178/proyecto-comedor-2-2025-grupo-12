package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Usuario;
import java.io.IOException;
import java.util.List;

// Interfaz que define el contrato para el repositorio de usuarios.
public interface IRepositorioUsuarios {
    
    // Guarda un usuario en el repositorio.
    void guardarUsuario(Usuario usuario) throws IOException;
    
    // Obtiene todos los usuarios del repositorio.
    List<Usuario> listarUsuarios() throws IOException;
    
    // Sobrescribe el repositorio con la lista completa de usuarios.
    void guardarTodos(List<Usuario> usuarios) throws IOException;
    
    // Busca un usuario por su cédula.
    default Usuario buscarPorCedula(String cedula) throws IOException {
        return listarUsuarios().stream()
            .filter(u -> u != null && u.obtCedula() != null && 
                         u.obtCedula().trim().equalsIgnoreCase(cedula.trim()))
            .findFirst()
            .orElse(null);
    }
}
