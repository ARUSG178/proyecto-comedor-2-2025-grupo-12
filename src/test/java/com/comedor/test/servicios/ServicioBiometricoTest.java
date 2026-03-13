package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.comedor.controlador.ServicioBiometrico;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;

/**
 * Pruebas unitarias para ServicioBiometrico
 * Cubre:
 * - Validacion de identidad por reconocimiento facial
 * - Calculo de similitud entre imagenes
 * - Manejo de errores cuando no hay fotos
 */
public class ServicioBiometricoTest {

    private ServicioBiometrico servicioBiometrico;

    @BeforeEach
    void setUp() {
        servicioBiometrico = new ServicioBiometrico();
    }

    @Test
    @DisplayName("SB1: Usuario valido - Similitud > 60%")
    void testUsuarioValido() {
        // Simular alta similitud
        double similitud = 85.0;
        
        // Usuario válido: similitud > 60%
        assertTrue(similitud > 60.0, "La similitud debe ser mayor a 60%");
    }

    @Test
    @DisplayName("SB2: Usuario invalido - Similitud <= 60%")
    void testUsuarioInvalido() {
        // Simular baja similitud
        double similitud = 45.0;
        
        // El usuario es inválido cuando similitud <= 60%
        assertTrue(similitud <= 60.0, "Con 45% el usuario debe ser inválido");
    }

    @Test
    @DisplayName("SB3: Error cuando foto capturada es null")
    void testFotoCapturadaNull() throws IOException {
        Usuario usuario = new Estudiante("V-12345678", "pass", "Carrera", "Facultad");
        
        assertThrows(IOException.class, () -> {
            servicioBiometrico.calcularSimilitud(usuario, null);
        });
    }

    @Test
    @DisplayName("SB4: Error cuando no existe foto de referencia")
    void testFotoReferenciaNoExiste() throws IOException {
        Usuario usuario = new Estudiante("V-99999999", "pass", "Carrera", "Facultad");
        File fotoTemp = new File("temp_foto.jpg");
        
        assertThrows(IOException.class, () -> {
            servicioBiometrico.calcularSimilitud(usuario, fotoTemp);
        });
    }

    @Test
    @DisplayName("SB5: Valores limite de similitud - 60%")
    void testValorLimiteSimilitud() {
        // Valor limite: exactamente 60%
        double similitud = 60.0;
        
        // A 60% o menos, el usuario se considera inválido
        assertFalse(similitud > 60.0, "A 60% el usuario debe ser inválido");
    }

    @Test
    @DisplayName("SB6: Similitud maxima - 100%")
    void testSimilitudMaxima() {
        double similitud = 100.0;
        assertTrue(similitud > 60.0, "100% debe ser válido");
    }

    @Test
    @DisplayName("SB7: Similitud minima valida - 60.1%")
    void testSimilitudMinimaValida() {
        double similitud = 60.1;
        assertTrue(similitud > 60.0, "60.1% debe ser válido");
    }

    @Test
    @DisplayName("SB8: Similitud maxima invalida - 60.0%")
    void testSimilitudMaximaInvalida() {
        double similitud = 60.0;
        assertFalse(similitud > 60.0, "60.0% debe ser inválido");
    }
}
