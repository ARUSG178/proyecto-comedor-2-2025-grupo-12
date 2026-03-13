package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.comedor.controlador.ServicioRegistro;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.DuplicateUserException;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.modelo.persistencia.IRepositorioAdminCdg;
import com.comedor.modelo.persistencia.IRepositorioSecretaria;
import com.comedor.modelo.persistencia.IRepositorioUsuarios;

/**
 * Pruebas unitarias para ServicioRegistro
 * Cubre:
 * - Registro de usuarios consumidores (estudiantes, empleados)
 * - Registro de administradores con código especial
 */
@ExtendWith(MockitoExtension.class)
public class ServicioRegistroTest {

    @Mock
    private IRepositorioUsuarios mockRepoUsuarios;

    @Mock
    private IRepositorioSecretaria mockRepoSecretaria;

    @Mock
    private IRepositorioAdminCdg mockRepoAdminCdg;

    private ServicioRegistro servicioRegistro;

    private Usuario estudianteExistente;
    private Usuario empleadoExistente;
    private Usuario adminExistente;

    @BeforeEach
    void setUp() throws IOException {
        servicioRegistro = new ServicioRegistro(mockRepoUsuarios, mockRepoSecretaria, mockRepoAdminCdg);

        // Configurar usuarios existentes para pruebas de duplicados
        estudianteExistente = new Estudiante("V-12345678", "pass123", "Computación", "Ciencias");
        empleadoExistente = new Empleado("V-87654321", "pass456", "Analista", "Recursos Humanos", "EMP001");
        adminExistente = new Administrador("V-11223344", "admin123", "ADMIN001");

        // Configurar mocks
        List<Usuario> usuariosExistentes = new ArrayList<>();
        usuariosExistentes.add(estudianteExistente);
        usuariosExistentes.add(empleadoExistente);
        usuariosExistentes.add(adminExistente);

        when(mockRepoUsuarios.listarUsuarios()).thenReturn(usuariosExistentes);
    }

    // ============================================
    // PRUEBA 1: Registro de Estudiante (Consumidor)
    // ============================================

    @Test
    @DisplayName("SR1: Registro de estudiante válido - Partición de equivalencia")
    void testRegistrarEstudianteValido() throws Exception {
        // Given: Estudiante válido en secretaría
        Usuario estudianteSecretaria = new Estudiante("V-99999999", "pass", "Medicina", "Salud");
        estudianteSecretaria.setNombre("Juan Pérez");

        when(mockRepoSecretaria.buscarRegistroUCV("V-99999999")).thenReturn(estudianteSecretaria);
        doNothing().when(mockRepoUsuarios).guardarUsuario(any(Usuario.class));

        // When: Registrar estudiante
        servicioRegistro.registrarUsuario("V-99999999", "password123", null);

        // Then: Se guarda correctamente
        verify(mockRepoUsuarios).guardarUsuario(any(Usuario.class));
    }

    @Test
    @DisplayName("SR2: Registro de estudiante duplicado - Error de validación")
    void testRegistrarEstudianteDuplicado() {
        // When: Intentar registrar estudiante ya existente
        // Then: Debe lanzar DuplicateUserException
        assertThrows(DuplicateUserException.class, () -> {
            servicioRegistro.registrarUsuario("V-12345678", "password123", null);
        });
    }

    @Test
    @DisplayName("SR3: Registro de estudiante no encontrado en secretaría")
    void testRegistrarEstudianteNoEncontradoEnSecretaria() throws IOException {
        // Given: Estudiante no existe en secretaría
        when(mockRepoSecretaria.buscarRegistroUCV("V-99999999")).thenReturn(null);

        // When & Then: Debe lanzar InvalidCredentialsException
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioRegistro.registrarUsuario("V-99999999", "password123", null);
        });
    }

    // ============================================
    // PRUEBA 2: Registro de Empleado (Consumidor)
    // ============================================

    @Test
    @DisplayName("SR4: Registro de empleado válido")
    void testRegistrarEmpleadoValido() throws Exception {
        // Given: Empleado válido en secretaría
        Usuario empleadoSecretaria = new Empleado("V-88888888", "pass", "Profesor", "Computación", "EMP002");
        empleadoSecretaria.setNombre("María García");

        when(mockRepoSecretaria.buscarRegistroUCV("V-88888888")).thenReturn(empleadoSecretaria);
        doNothing().when(mockRepoUsuarios).guardarUsuario(any(Usuario.class));

        // When: Registrar empleado
        servicioRegistro.registrarUsuario("V-88888888", "password123", null);

        // Then: Se guarda correctamente
        verify(mockRepoUsuarios).guardarUsuario(any(Usuario.class));
    }

    // ============================================
    // PRUEBA 3: Registro de Administrador
    // ============================================

    @Test
    @DisplayName("SR5: Registro de administrador válido con código correcto")
    void testRegistrarAdministradorValido() throws Exception {
        // Given: Código de admin válido
        when(mockRepoAdminCdg.codigoValido("ADMIN-NEW")).thenReturn(true);
        doNothing().when(mockRepoAdminCdg).consumirCodigo("ADMIN-NEW");
        doNothing().when(mockRepoUsuarios).guardarUsuario(any(Usuario.class));

        // When: Registrar administrador
        servicioRegistro.registrarUsuario("V-77777777", "adminpass", "ADMIN-NEW");

        // Then: Se guarda y consume el código
        verify(mockRepoUsuarios).guardarUsuario(any(Usuario.class));
        verify(mockRepoAdminCdg).consumirCodigo("ADMIN-NEW");
    }

    @Test
    @DisplayName("SR6: Registro de administrador con código inválido")
    void testRegistrarAdministradorCodigoInvalido() throws IOException {
        // Given: Código de admin inválido
        when(mockRepoAdminCdg.codigoValido("INVALID")).thenReturn(false);

        // When & Then: Debe lanzar InvalidCredentialsException
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioRegistro.registrarUsuario("V-77777777", "adminpass", "INVALID");
        });
    }

    // ============================================
    // PRUEBA 4: Validaciones comunes
    // ============================================

    @Test
    @DisplayName("SR7: Registro con cédula inválida")
    void testRegistroCedulaInvalida() throws IOException {
        // When & Then: Cédula sin formato correcto
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioRegistro.registrarUsuario("INVALID-CEDULA", "password123", null);
        });
    }

    @Test
    @DisplayName("SR8: Registro con contraseña inválida")
    void testRegistroContrasenaInvalida() throws IOException {
        // Given: Estudiante válido en secretaría
        Usuario estudianteSecretaria = new Estudiante("V-99999999", "pass", "Medicina", "Salud");
        when(mockRepoSecretaria.buscarRegistroUCV("V-99999999")).thenReturn(estudianteSecretaria);

        // When & Then: Contraseña muy corta
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioRegistro.registrarUsuario("V-99999999", "123", null);
        });
    }
}
