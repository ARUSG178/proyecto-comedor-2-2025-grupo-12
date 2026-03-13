package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Usuario;
import java.io.IOException;

// Interfaz que define el contrato para el repositorio de datos de secretaría.
public interface IRepositorioSecretaria {
    
    // Busca un registro en la base de datos de la UCV por cédula.
    Usuario buscarRegistroUCV(String cedulaBuscada) throws IOException;
}
