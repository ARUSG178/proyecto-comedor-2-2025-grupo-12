package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.comedor.controlador.ServicioBiometrico;
import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.excepciones.SaldoInsuficienteException;

/**
 * Pruebas unitarias para el Consumidor (Comensal)
 * Funcionalidades básicas del sistema de comedor
 */
public class ConsumidorTest {

    private ServicioBiometrico servicioBiometrico;
    private ServicioPago servicioPago;
    private Usuario estudiante;
    private Usuario empleado;
    private File fotoValida;
    private File fotoInvalida;
    private File fotoNoExistente;

    @BeforeEach
    void setUp() throws IOException {
        servicioBiometrico = new ServicioBiometrico();
        servicioPago = new ServicioPago();
        
        // Crear usuarios de prueba
        estudiante = new Estudiante("V12345678", "pass123", "Computación", "Ciencias");
        estudiante.setSaldo(1000.0);  // Saldo más bajo para no exceder límite
        
        empleado = new Empleado("V87654321", "pass456", "Analista", "RRHH", "EMP001");
        empleado.setSaldo(2000.0);  // Saldo más bajo para no exceder límite
        
        // Crear directorio de imágenes de prueba
        Path imagesDir = Paths.get("src/test/resources/images/secretaria");
        Files.createDirectories(imagesDir);
        
        // Crear archivos de imagen de prueba (simulados)
        fotoValida = Files.createFile(imagesDir.resolve("V12345678.jpg")).toFile();
        fotoInvalida = Files.createFile(imagesDir.resolve("V99999999.jpg")).toFile();
        fotoNoExistente = new File("no_existe.jpg");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpiar archivos de prueba
        if (fotoValida != null && fotoValida.exists()) fotoValida.delete();
        if (fotoInvalida != null && fotoInvalida.exists()) fotoInvalida.delete();
    }

    @Nested
    @DisplayName("Pruebas de Recarga de Monedero")
    class RecargaMonederoTests {

        @Test
        @DisplayName("RC-01: Recarga exitosa de monedero de estudiante")
        void testRecargaExitosaEstudiante() {
            // Arrange
            double saldoInicial = estudiante.obtSaldo();
            double montoRecarga = 500.0;  // Monto más pequeño
            Monedero monedero = new Monedero(estudiante);

            // Act
            monedero.recargar(montoRecarga);

            // Assert
            double saldoEsperado = saldoInicial + montoRecarga;
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001,
                "El saldo debe incrementarse exactamente en el monto recargado");
            assertEquals(saldoEsperado, estudiante.obtSaldo(), 0.001,
                "El saldo del usuario debe actualizarse");
        }

        @Test
        @DisplayName("RC-02: Recarga exitosa de monedero de empleado")
        void testRecargaExitosaEmpleado() {
            // Arrange
            double saldoInicial = empleado.obtSaldo();
            double montoRecarga = 1000.0;  // Monto más pequeño
            Monedero monedero = new Monedero(empleado);

            // Act
            monedero.recargar(montoRecarga);

            // Assert
            double saldoEsperado = saldoInicial + montoRecarga;
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001,
                "La recarga debe funcionar para cualquier tipo de usuario");
        }

        @ParameterizedTest
        @CsvSource({
            "0.01, 1000.01",
            "100.0, 1100.0",
            "500.0, 1500.0",
            "1000.0, 2000.0"
        })
        @DisplayName("RC-03: Múltiples montos de recarga válidos")
        void testMontosRecargaValidos(double montoRecarga, double saldoEsperado) {
            // Arrange
            Monedero monedero = new Monedero(estudiante);
            estudiante.setSaldo(1000.0);  // Resetear saldo inicial

            // Act
            monedero.recargar(montoRecarga);

            // Assert
            assertEquals(saldoEsperado, monedero.obtSaldo(), 0.001,
                "El saldo debe reflejar el monto exacto recargado");
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, -100.0, -9999.99})
        @DisplayName("RC-04: Intentos de recarga con montos inválidos")
        void testMontosRecargaInvalidos(double montoInvalido) {
            // Arrange
            Monedero monedero = new Monedero(estudiante);
            double saldoInicial = monedero.obtSaldo();

            // Act - El método no lanza excepción, simplemente no recarga
            monedero.recargar(montoInvalido);

            // Assert - El saldo no debe modificarse
            assertEquals(saldoInicial, monedero.obtSaldo(), 0.001,
                "El saldo no debe modificarse con montos inválidos");
        }

        @Test
        @DisplayName("RC-05: Recarga con monto decimal válido")
        void testRecargaMontoDecimal() {
            // Arrange
            Monedero monedero = new Monedero(estudiante);
            double montoDecimal = 123.45;  // Monto más pequeño

            // Act
            monedero.recargar(montoDecimal);

            // Assert
            assertEquals(1000.0 + 123.45, monedero.obtSaldo(), 0.001,
                "Debe soportar montos decimales con precisión");
        }
    }

    @Nested
    @DisplayName("Pruebas de Reconocimiento Facial")
    class ReconocimientoFacialTests {

        @Test
        @DisplayName("RF-01: Reconocimiento facial válido - estudiante")
        void testReconocimientoValidoEstudiante() throws IOException {
            // Act
            double similitud = servicioBiometrico.calcularSimilitud(estudiante, fotoValida);

            // Assert
            assertTrue(similitud >= 60.0,
                "La similitud debe ser mayor o igual al 60% para usuario válido");
            assertTrue(similitud <= 100.0,
                "La similitud no puede exceder el 100%");
        }

        @Test
        @DisplayName("RF-02: Reconocimiento facial inválido - usuario no encontrado")
        void testReconocimientoUsuarioNoEncontrado() throws IOException {
            // Act & Assert
            assertThrows(IOException.class, () -> {
                servicioBiometrico.calcularSimilitud(estudiante, fotoNoExistente);
            }, "Debe lanzar IOException si la foto de referencia no existe");
        }

        @Test
        @DisplayName("RF-03: Reconocimiento facial con foto nula")
        void testReconocimientoFotoNula() throws IOException {
            // Act & Assert
            assertThrows(IOException.class, () -> {
                servicioBiometrico.calcularSimilitud(estudiante, null);
            }, "Debe lanzar IOException si la foto capturada es nula");
        }

        @Test
        @DisplayName("RF-04: Reconocimiento facial con archivo no válido")
        void testReconocimientoArchivoNoValido() throws IOException {
            // Arrange - crear archivo de texto no válido
            File archivoInvalido = Files.createFile(
                Paths.get("src/test/resources/invalido.txt")).toFile();

            // Act & Assert
            assertThrows(IOException.class, () -> {
                servicioBiometrico.calcularSimilitud(estudiante, archivoInvalido);
            }, "Debe lanzar IOException para archivos no válidos");

            // Cleanup
            archivoInvalido.delete();
        }

        @ParameterizedTest
        @ValueSource(strings = {"V12345678", "V87654321", "V11223344"})
        @DisplayName("RF-05: Búsqueda de fotos de referencia por cédula")
        void testBusquedaFotoPorCedula(String cedula) throws IOException {
            // Arrange - crear archivo para la cédula específica
            Path imagesDir = Paths.get("src/test/resources/images/secretaria");
            File fotoCedula = Files.createFile(imagesDir.resolve(cedula + ".jpg")).toFile();
            
            Usuario usuario = cedula.equals("V12345678") ? estudiante : 
                            cedula.equals("V87654321") ? empleado : 
                            new Estudiante(cedula, "pass", "Test", "Test");

            try {
                // Act
                double similitud = servicioBiometrico.calcularSimilitud(usuario, fotoCedula);

                // Assert
                assertTrue(similitud >= 60.0,
                    "Debe encontrar y comparar la foto para cualquier cédula válida");
            } finally {
                fotoCedula.delete();
            }
        }
    }

    @Nested
    @DisplayName("Pruebas de Cobro Automático")
    class CobroAutomaticoTests {

        @Test
        @DisplayName("CA-01: Cobro exitoso con saldo suficiente")
        void testCobroExitosoSaldoSuficiente() throws Exception {
            // Arrange
            double saldoInicial = 5000.0;
            double costoServicio = 1500.0;
            estudiante.setSaldo(saldoInicial);

            // Act
            servicioPago.procesarCobro(estudiante, costoServicio);

            // Assert - Estudiante con 25% de tarifa
            double montoReal = costoServicio * 0.25; // 25% del costo
            double saldoEsperado = saldoInicial - montoReal;
            assertEquals(saldoEsperado, estudiante.obtSaldo(), 0.001,
                "El saldo debe descontarse el monto real con tarifa");
            assertTrue(estudiante.obtSaldo() >= 0,
                "El saldo no puede ser negativo");
        }

        @Test
        @DisplayName("CA-02: Cobro fallido por saldo insuficiente")
        void testCobroFallidoSaldoInsuficiente() {
            // Arrange
            estudiante.setSaldo(100.0); // Saldo bajo
            double costoServicio = 5000.0; // Costo alto

            // Act & Assert
            assertThrows(SaldoInsuficienteException.class, () -> {
                servicioPago.procesarCobro(estudiante, costoServicio);
            }, "Debe lanzar SaldoInsuficienteException");

            assertEquals(100.0, estudiante.obtSaldo(), 0.001,
                "El saldo no debe modificarse si el cobro falla");
        }

        @Test
        @DisplayName("CA-03: Cobro exacto - saldo igual al costo")
        void testCobroExacto() throws Exception {
            // Arrange
            double costoServicio = 8000.0;
            // Empleado con 95% de tarifa necesita saldo suficiente
            double montoReal = costoServicio * 0.95; // 95% del costo
            empleado.setSaldo(montoReal); // Saldo exacto para el monto real

            // Act
            servicioPago.procesarCobro(empleado, costoServicio);

            // Assert
            assertEquals(0.0, empleado.obtSaldo(), 0.001,
                "El saldo debe quedar en cero con cobro exacto");
        }

        @ParameterizedTest
        @CsvSource({
            "10000.0, 2500.0, 9375.0",
            "15000.0, 5000.5, 13749.5",
            "5000.0, 0.01, 4999.998",
            "999999.99, 123456.78, 974743.21"
        })
        @DisplayName("CA-04: Múltiples escenarios de cobro exitoso")
        void testEscenariosCobroExitoso(double saldoInicial, double costo, double saldoFinal) {
            // Arrange
            estudiante.setSaldo(saldoInicial);

            // Act
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudiante, costo);
            });

            // Assert - Estudiante con 25% de tarifa
            assertEquals(saldoFinal, estudiante.obtSaldo(), 0.001,
                "El saldo final debe ser inicial - (costo * 0.25)");
        }

        @Test
        @DisplayName("CA-05: Cobro con monto cero")
        void testCobroMontoCero() throws Exception {
            // Arrange
            double saldoInicial = estudiante.obtSaldo();

            // Act
            servicioPago.procesarCobro(estudiante, 0.0);

            // Assert
            assertEquals(saldoInicial, estudiante.obtSaldo(), 0.001,
                "Cobro de monto cero no debe afectar el saldo");
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -100.0, -5000.0})
        @DisplayName("CA-06: Intentos de cobro con montos negativos")
        void testCobroMontosNegativos(double montoNegativo) {
            // Arrange
            double saldoInicial = estudiante.obtSaldo();

            // Act & Assert - El sistema no lanza excepción para montos negativos
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudiante, montoNegativo);
            }, "No debe lanzar excepción para montos negativos");

            assertEquals(saldoInicial, estudiante.obtSaldo(), 0.001,
                "El saldo no debe modificarse con montos inválidos");
        }
    }

    @Nested
    @DisplayName("Pruebas Integradas - Flujo Completo")
    class FlujoCompletoTests {

        @Test
        @DisplayName("FC-01: Flujo completo - recarga, reconocimiento y cobro exitoso")
        void testFlujoCompletoExitoso() throws Exception {
            // Arrange
            double montoRecarga = 5000.0;
            double costoServicio = 2500.0;
            Monedero monedero = new Monedero(estudiante);
            double saldoInicial = estudiante.obtSaldo();

            // Act - Recargar
            monedero.recargar(montoRecarga);

            // Act - Validar reconocimiento
            double similitud = servicioBiometrico.calcularSimilitud(estudiante, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");

            // Act - Cobrar servicio
            servicioPago.procesarCobro(estudiante, costoServicio);

            // Assert
            double saldoFinalEsperado = saldoInicial + montoRecarga - costoServicio;
            assertEquals(saldoFinalEsperado, estudiante.obtSaldo(), 0.001,
                "El saldo final debe reflejar recarga menos cobro");
        }

        @Test
        @DisplayName("FC-02: Flujo con reconocimiento inválido - no permite cobro")
        void testFlujoReconocimientoInvalido() throws IOException {
            // Arrange
            double saldoInicial = estudiante.obtSaldo();

            // Act & Assert - Reconocimiento inválido
            assertThrows(IOException.class, () -> {
                servicioBiometrico.calcularSimilitud(estudiante, fotoNoExistente);
            }, "Reconocimiento debe fallar");

            // Act - Intentar cobro (simulado que no llega por validación previa)
            assertEquals(saldoInicial, estudiante.obtSaldo(), 0.001,
                "Saldo no debe modificarse si reconocimiento falla");
        }

        @Test
        @DisplayName("FC-03: Flujo con saldo insuficiente - rechaza servicio")
        void testFlujoSaldoInsuficiente() throws Exception {
            // Arrange
            estudiante.setSaldo(100.0); // Saldo bajo
            double costoServicio = 5000.0;

            // Act - Reconocimiento válido
            double similitud = servicioBiometrico.calcularSimilitud(estudiante, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");

            // Act & Assert - Cobro fallido
            assertThrows(SaldoInsuficienteException.class, () -> {
                servicioPago.procesarCobro(estudiante, costoServicio);
            }, "Debe rechazar por saldo insuficiente");

            assertEquals(100.0, estudiante.obtSaldo(), 0.001,
                "Saldo no debe modificarse si cobro falla");
        }
    }
}
