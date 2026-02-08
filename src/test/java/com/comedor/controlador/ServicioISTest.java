package com.comedor.controlador;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;

@DisplayName("Pruebas Unitarias - Servicio de Login")
class ServicioISTest {
    
    @TempDir
    Path tempDir;
    
    private ServicioIS servicioIS;
    private File testFile;
    
    @BeforeEach
    void setUp() throws IOException {
        // Crear archivo temporal para pruebas
        testFile = tempDir.resolve("usuarios_test.dat").toFile();
        
        // Configurar el servicio para usar el archivo temporal
        servicioIS = new ServicioIS();
        
    }
    
    @Test
    @DisplayName("Validar tipos de usuario")
    void testValidarTiposUsuario() {
        // Prueba básica de instanciación
        Estudiante estudiante = new Estudiante(
            "11111111", "Estudiante", "Test", "est@test.com",
            "pass111", "20241111", "Ciencias"
        );
        
        Administrador admin = new Administrador(
            "22222222", "Admin", "Test", "admin@test.com",
            "pass222", "ADMIN222"
        );
        
        assertEquals("Estudiante", estudiante.getTipo());
        assertEquals("Administrador", admin.getTipo());
    }
}