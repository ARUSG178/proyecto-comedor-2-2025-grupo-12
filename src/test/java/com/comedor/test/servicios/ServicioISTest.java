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

import com.comedor.controlador.ServicioIS;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.modelo.persistencia.IRepositorioUsuarios;

/**
 * Pruebas unitarias para ServicioIS (Inicio de Sesion)
 * Cubre:
 * - Inicio de sesion de consumidores (estudiantes, empleados)
 * - Inicio de sesion de administradores
 * - Validacion de credenciales
 */
@ExtendWith(MockitoExtension.class)
public class ServicioISTest {

    @Mock
    private IRepositorioUsuarios mockRepoUsuarios;

    private ServicioIS servicioIS;

    private Usuario estudianteValido;
    private Usuario empleadoValido;
    private Usuario adminValido;

    @BeforeEach
    void setUp() throws IOException {
        servicioIS = new ServicioIS(mockRepoUsuarios);

        estudianteValido = new Estudiante("V-12345678", "password123", "Computacion", "Ciencias");
        estudianteValido.setEstado(true);
        estudianteValido.setIntentosFallidos(0);

        empleadoValido = new Empleado("V-87654321", "password456", "Analista", "RRHH", "EMP001");
        empleadoValido.setEstado(true);
        empleadoValido.setIntentosFallidos(0);

        adminValido = new Administrador("V-11223344", "admin123", "ADMIN001");
        adminValido.setEstado(true);
        adminValido.setIntentosFallidos(0);

        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(estudianteValido);
        usuarios.add(empleadoValido);
        usuarios.add(adminValido);

        when(mockRepoUsuarios.listarUsuarios()).thenReturn(usuarios);
    }

    @Test
    @DisplayName("IS1: Login estudiante valido")
    void testLoginEstudianteValido() throws Exception {
        Usuario credenciales = new Estudiante("V-12345678", "password123", "", "");
        Usuario resultado = servicioIS.IniciarSesion(credenciales);
        assertNotNull(resultado);
        assertEquals("V-12345678", resultado.obtCedula());
        assertTrue(resultado instanceof Estudiante);
    }

    @Test
    @DisplayName("IS2: Login estudiante contrasena incorrecta")
    void testLoginEstudianteContrasenaIncorrecta() throws IOException {
        Usuario credenciales = new Estudiante("V-12345678", "wrongpassword", "", "");
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioIS.IniciarSesion(credenciales);
        });
    }

    @Test
    @DisplayName("IS3: Login empleado valido")
    void testLoginEmpleadoValido() throws Exception {
        Usuario credenciales = new Empleado("V-87654321", "password456", "", "", "");
        Usuario resultado = servicioIS.IniciarSesion(credenciales);
        assertNotNull(resultado);
        assertTrue(resultado instanceof Empleado);
    }

    @Test
    @DisplayName("IS4: Login administrador valido")
    void testLoginAdministradorValido() throws Exception {
        Usuario credenciales = new Administrador("V-11223344", "admin123", "");
        Usuario resultado = servicioIS.IniciarSesion(credenciales);
        assertNotNull(resultado);
        assertTrue(resultado instanceof Administrador);
    }

    @Test
    @DisplayName("IS5: Login usuario no existente")
    void testLoginUsuarioNoExistente() {
        Usuario credenciales = new Estudiante("V-99999999", "password", "", "");
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioIS.IniciarSesion(credenciales);
        });
    }

    @Test
    @DisplayName("IS6: Login usuario bloqueado")
    void testLoginUsuarioBloqueado() {
        estudianteValido.setIntentosFallidos(5);
        estudianteValido.setEstado(false);
        Usuario credenciales = new Estudiante("V-12345678", "password123", "", "");
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioIS.IniciarSesion(credenciales);
        });
    }

    @Test
    @DisplayName("IS7: Login cedula vacia")
    void testLoginCedulaVacia() {
        Usuario credenciales = new Estudiante("", "password", "", "");
        assertThrows(InvalidCredentialsException.class, () -> {
            servicioIS.IniciarSesion(credenciales);
        });
    }
}
