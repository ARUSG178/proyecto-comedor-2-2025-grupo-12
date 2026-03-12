package com.comedor.modelo.persistencia;

import java.io.IOException;
import java.util.List;

// Interfaz que define el contrato para el repositorio de códigos de administrador.
public interface IRepositorioAdminCdg {
    
    // Verifica si un código de administrador es válido.
    boolean codigoValido(String codigo) throws IOException;
    
    // Consume (elimina) un código de administrador después de su uso.
    void consumirCodigo(String codigo) throws IOException;
    
    // Obtiene todos los códigos de administrador disponibles.
    List<String> listarCodigos() throws IOException;
}
