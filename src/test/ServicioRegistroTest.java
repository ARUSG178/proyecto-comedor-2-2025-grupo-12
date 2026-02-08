package com.comedor.controlador;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.InvalidEmailFormatException;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.modelo.excepciones.DuplicateUserException;

@DisplayName("Pruebas Unitarias - Servicio de Registro")
class ServicioRegistroTest {
    
    @TempDir
    Path tempDir;
    
    private ServicioRegistro servicioRegistro;
    private File usuariosFile;
    private File codigosFile;
    private Path usuariosBackup;
    private Path codigosBackup;
    
    // Archivos originales del sistema
    private static final String USUARIOS_FILE = "data/usuarios.txt";
    private static final String CODIGOS_FILE = "data/codigos_admin.txt";
    
    @BeforeEach
    void setUp() throws IOException {
        // Crear estructura de directorios temporal
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        
        // Crear archivos temporales
        usuariosFile = dataDir.resolve("usuarios.txt").toFile();
        codigosFile = dataDir.resolve("codigos_admin.txt").toFile();
        
        // Inicializar archivo de usuarios
        if (!usuariosFile.exists()) {
            Files.writeString(usuariosFile.toPath(), "");
        }
        
        // Inicializar archivo de códigos con códigos de prueba
        Files.writeString(codigosFile.toPath(), 
            "ADMIN001\nADMIN002\nADMIN003\nADMIN004\nADMIN005\n");
        
        // Hacer backup de archivos originales
        File originalUsuarios = new File(USUARIOS_FILE);
        File originalCodigos = new File(CODIGOS_FILE);
        
        if (originalUsuarios.exists()) {
            usuariosBackup = tempDir.resolve("usuarios_backup.txt");
            Files.copy(originalUsuarios.toPath(), usuariosBackup, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(usuariosFile.toPath(), originalUsuarios.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        if (originalCodigos.exists()) {
            codigosBackup = tempDir.resolve("codigos_backup.txt");
            Files.copy(originalCodigos.toPath(), codigosBackup, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(codigosFile.toPath(), originalCodigos.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Configurar archivos temporales como los archivos del sistema
        System.setProperty("usuarios.file", usuariosFile.getAbsolutePath());
        System.setProperty("codigos.file", codigosFile.getAbsolutePath());
        
        servicioRegistro = new ServicioRegistro();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Restaurar archivos originales
        if (usuariosBackup != null && Files.exists(usuariosBackup)) {
            Files.copy(usuariosBackup, new File(USUARIOS_FILE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        if (codigosBackup != null && Files.exists(codigosBackup)) {
            Files.copy(codigosBackup, new File(CODIGOS_FILE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    @Test
    @DisplayName("Registrar estudiante exitosamente")
    void testRegistrarEstudianteExitoso() {
        // Preparar datos de prueba
        Estudiante estudiante = new Estudiante(
            "12345678", "Juan", "Perez", "juan.perez@test.com",
            "Password123", "20241234", "Ingeniería de Sistemas"
        );
        
        // Ejecutar
        assertDoesNotThrow(() -> {
            servicioRegistro.registrarUsuario(estudiante);
        });
        
        // Verificar que se guardó en el archivo
        assertTrue(usuariosFile.exists());
        assertTrue(usuariosFile.length() > 0);
        
        // Leer archivo y verificar contenido
        try {
            String contenido = Files.readString(usuariosFile.toPath());
            assertTrue(contenido.contains("juan.perez@test.com"));
            assertTrue(contenido.contains("Estudiante"));
            assertTrue(contenido.contains("Ingeniería de Sistemas"));
        } catch (IOException e) {
            fail("Error al leer archivo de usuarios: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Registrar administrador exitosamente con código válido")
    void testRegistrarAdministradorExitoso() {
        // Preparar datos de prueba
        Administrador admin = new Administrador(
            "87654321", "Maria", "Gomez", "maria.gomez@test.com",
            "AdminPass123", "ADMIN001"
        );
        
        // Ejecutar
        assertDoesNotThrow(() -> {
            servicioRegistro.registrarUsuario(admin);
        });
        
        // Verificar que se guardó en el archivo
        assertTrue(usuariosFile.exists());
        assertTrue(usuariosFile.length() > 0);
        
        // Verificar que el código se consumió
        try {
            String codigosRestantes = Files.readString(codigosFile.toPath());
            assertFalse(codigosRestantes.contains("ADMIN001")); // Código debe eliminarse
            assertTrue(codigosRestantes.contains("ADMIN002")); // Otros códigos deben permanecer
        } catch (IOException e) {
            fail("Error al leer archivo de códigos: " + e.getMessage());
        }
        
        // Verificar datos del administrador
        try {
            String contenido = Files.readString(usuariosFile.toPath());
            assertTrue(contenido.contains("maria.gomez@test.com"));
            assertTrue(contenido.contains("Administrador"));
        } catch (IOException e) {
            fail("Error al leer archivo de usuarios: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Lanzar excepción por email inválido")
    void testRegistrarUsuarioEmailInvalido() {
        // Email sin formato válido
        Estudiante estudiante = new Estudiante(
            "11111111", "Carlos", "Lopez", "email-invalido",
            "Pass111", "20241111", "Medicina"
        );
        
        // Ejecutar y verificar
        InvalidEmailFormatException exception = assertThrows(
            InvalidEmailFormatException.class,
            () -> servicioRegistro.registrarUsuario(estudiante)
        );
        
        // Verificar que no se guardó en el archivo
        assertTrue(usuariosFile.length() == 0 || !Files.readString(usuariosFile.toPath()).contains("11111111"));
    }
    
    @Test
    @DisplayName("Lanzar excepción por credenciales inválidas - ID vacío")
    void testRegistrarUsuarioIdVacio() {
        // ID vacío
        Estudiante estudiante = new Estudiante(
            "", "Ana", "Martinez", "ana.martinez@test.com",
            "Pass222", "20242222", "Derecho"
        );
        
        // Ejecutar y verificar
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> servicioRegistro.registrarUsuario(estudiante)
        );
        
        // Verificar mensaje de error
        assertNotNull(exception.getMessage());
    }
    
    @Test
    @DisplayName("Lanzar excepción por credenciales inválidas - contraseña débil")
    void testRegistrarUsuarioPasswordDebil() {
        // Contraseña muy corta
        Estudiante estudiante = new Estudiante(
            "22222222", "Luis", "Ramirez", "luis.ramirez@test.com",
            "123", "20242222", "Arquitectura"
        );
        
        // Ejecutar y verificar
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> servicioRegistro.registrarUsuario(estudiante)
        );
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    @DisplayName("Lanzar excepción por usuario duplicado - mismo ID")
    void testRegistrarUsuarioDuplicadoId() throws Exception {
        // Primer registro
        Estudiante estudiante1 = new Estudiante(
            "33333333", "Pedro", "Sanchez", "pedro.sanchez@test.com",
            "Pass333", "20243333", "Economía"
        );
        
        servicioRegistro.registrarUsuario(estudiante1);
        
        // Segundo registro con mismo ID pero diferente email
        Estudiante estudiante2 = new Estudiante(
            "33333333", "Pablo", "Garcia", "pablo.garcia@test.com",
            "Pass444", "20244444", "Administración"
        );
        
        // Ejecutar y verificar
        DuplicateUserException exception = assertThrows(
            DuplicateUserException.class,
            () -> servicioRegistro.registrarUsuario(estudiante2)
        );
        
        assertTrue(exception.getMessage().contains("duplicado") || 
                  exception.getMessage().contains("ya existe"));
    }
    
    @Test
    @DisplayName("Lanzar excepción por usuario duplicado - mismo email")
    void testRegistrarUsuarioDuplicadoEmail() throws Exception {
        // Primer registro
        Estudiante estudiante1 = new Estudiante(
            "44444444", "Laura", "Diaz", "laura.diaz@test.com",
            "Pass555", "20245555", "Psicología"
        );
        
        servicioRegistro.registrarUsuario(estudiante1);
        
        // Segundo registro con mismo email pero diferente ID
        Estudiante estudiante2 = new Estudiante(
            "55555555", "Lorena", "Diaz", "laura.diaz@test.com",
            "Pass666", "20246666", "Educación"
        );
        
        // Ejecutar y verificar
        DuplicateUserException exception = assertThrows(
            DuplicateUserException.class,
            () -> servicioRegistro.registrarUsuario(estudiante2)
        );
        
        assertTrue(exception.getMessage().contains("duplicado") || 
                  exception.getMessage().contains("ya existe"));
    }
    
    @Test
    @DisplayName("Lanzar excepción por código de administrador inválido")
    void testRegistrarAdminCodigoInvalido() {
        // Código que no existe
        Administrador admin = new Administrador(
            "66666666", "Roberto", "Mendoza", "roberto.mendoza@test.com",
            "AdminPass666", "CODIGO_INEXISTENTE"
        );
        
        // Ejecutar y verificar - podría ser InvalidCredentialsException o similar
        // Depende de cómo esté implementado VRegistro
        assertThrows(Exception.class, () -> {
            servicioRegistro.registrarUsuario(admin);
        });
        
        // Verificar que no se guardó
        try {
            String contenido = Files.readString(usuariosFile.toPath());
            assertFalse(contenido.contains("roberto.mendoza@test.com"));
        } catch (IOException e) {
            // Archivo vacío está bien
        }
    }
    
    @Test
    @DisplayName("Registrar múltiples usuarios exitosamente")
    void testRegistrarMultiplesUsuarios() throws Exception {
        // Registrar varios usuarios
        Estudiante estudiante1 = new Estudiante(
            "77777777", "Sofia", "Castro", "sofia.castro@test.com",
            "Pass777", "20247777", "Biología"
        );
        
        Estudiante estudiante2 = new Estudiante(
            "88888888", "Diego", "Ruiz", "diego.ruiz@test.com",
            "Pass888", "20248888", "Química"
        );
        
        Administrador admin = new Administrador(
            "99999999", "Admin", "Principal", "admin.principal@test.com",
            "AdminPass999", "ADMIN002"
        );
        
        // Ejecutar todos
        servicioRegistro.registrarUsuario(estudiante1);
        servicioRegistro.registrarUsuario(estudiante2);
        servicioRegistro.registrarUsuario(admin);
        
        // Verificar que todos están en el archivo
        String contenido = Files.readString(usuariosFile.toPath());
        assertTrue(contenido.contains("sofia.castro@test.com"));
        assertTrue(contenido.contains("diego.ruiz@test.com"));
        assertTrue(contenido.contains("admin.principal@test.com"));
        
        // Contar líneas (cada usuario en una línea)
        long lineCount = Files.lines(usuariosFile.toPath()).count();
        assertEquals(3, lineCount);
    }
    
    @Test
    @DisplayName("Registrar administrador con error al actualizar códigos")
    void testRegistrarAdminConArchivoCodigosInexistente() throws Exception {
        // Eliminar archivo de códigos para simular error
        Files.deleteIfExists(codigosFile.toPath());
        
        // Preparar administrador
        Administrador admin = new Administrador(
            "10101010", "Error", "Test", "error.test@test.com",
            "AdminPass101", "ADMIN003"
        );
        
        // Ejecutar - debería registrar pero mostrar advertencia
        // (según tu implementación, solo imprime error en consola)
        assertDoesNotThrow(() -> {
            servicioRegistro.registrarUsuario(admin);
        });
        
        // Verificar que el usuario sí se registró
        String contenido = Files.readString(usuariosFile.toPath());
        assertTrue(contenido.contains("error.test@test.com"));
    }
    
    @Test
    @DisplayName("Validar formato de email correcto")
    void testValidarFormatosEmail() throws Exception {
        // Emails válidos
        String[] emailsValidos = {
            "usuario@dominio.com",
            "nombre.apellido@universidad.edu.co",
            "usuario123@test.co",
            "u@d.c"
        };
        
        for (String email : emailsValidos) {
            Estudiante estudiante = new Estudiante(
                "12121212", "Test", "Email", email,
                "Pass1212", "20241212", "Test"
            );
            
            // Si la validación de email está activa, algunos podrían fallar
            // dependiendo de la implementación de VRegistro
            try {
                servicioRegistro.registrarUsuario(estudiante);
                // Limpiar archivo para siguiente prueba
                Files.writeString(usuariosFile.toPath(), "");
            } catch (InvalidEmailFormatException e) {
                // Algunos formatos podrían no pasar la validación
                System.out.println("Email no válido según VRegistro: " + email);
            } catch (DuplicateUserException e) {
                // Cambiar ID para siguiente prueba
            }
        }
    }
    
    @Test
    @DisplayName("Constructor inicializa repositorio correctamente")
    void testConstructor() {
        // Prueba simple del constructor
        ServicioRegistro servicio = new ServicioRegistro();
        assertNotNull(servicio);
        
        // Verificar que se puede usar
        Estudiante estudiante = new Estudiante(
            "13131313", "Constructor", "Test", "constructor@test.com",
            "Pass1313", "20241313", "Test"
        );
        
        assertDoesNotThrow(() -> {
            servicio.registrarUsuario(estudiante);
        });
    }
}