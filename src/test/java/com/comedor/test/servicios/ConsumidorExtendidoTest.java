package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;

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
import com.comedor.controlador.ServicioCosto;
import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.excepciones.SaldoInsuficienteException;

/**
 * Pruebas unitarias extendidas para el Consumidor (Comensal)
 * Funcionalidades adicionales:
 * - Saldo Pana: recarga entre estudiantes
 * - Estudiantes Exonerados: acceso sin descuento
 * - Estudiantes Becarios: descuento del 5%
 */
public class ConsumidorExtendidoTest {

    private ServicioBiometrico servicioBiometrico;
    private ServicioCosto servicioCosto;
    private ServicioPago servicioPago;
    private Estudiante estudianteRegular;
    private Estudiante estudiantePana;
    private EstudianteExonerado estudianteExonerado;
    private EstudianteBecario estudianteBecario;
    private File fotoValida;
    private File fotoNoExistente;

    @BeforeEach
    void setUp() throws IOException {
        servicioBiometrico = new ServicioBiometrico();
        servicioCosto = new ServicioCosto();
        servicioPago = new ServicioPago();
        
        // Crear estudiantes de diferentes tipos
        estudianteRegular = new Estudiante("V12345678", "pass123", "Computación", "Ciencias");
        estudianteRegular.setSaldo(5000.0);
        
        estudiantePana = new Estudiante("V87654321", "pass456", "Ingeniería", "Tecnología");
        estudiantePana.setSaldo(8000.0);
        
        estudianteExonerado = new EstudianteExonerado("V11223344", "exonerado123", "Medicina", "Salud");
        estudianteExonerado.setSaldo(3000.0);
        
        estudianteBecario = new EstudianteBecario("V44556677", "becario123", "Derecho", "Ciencias Sociales", 5.0);
        estudianteBecario.setSaldo(2000.0);
        
        // Crear directorio de imágenes de prueba
        Path imagesDir = Paths.get("src/test/resources/images/secretaria");
        Files.createDirectories(imagesDir);
        
        // Crear archivos de imagen de prueba
        fotoValida = Files.createFile(imagesDir.resolve("V12345678.jpg")).toFile();
        fotoNoExistente = new File("no_existe.jpg");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpiar archivos de prueba
        if (fotoValida != null && fotoValida.exists()) fotoValida.delete();
        if (fotoNoExistente != null && fotoNoExistente.exists()) fotoNoExistente.delete();
    }

    @Nested
    @DisplayName("Pruebas de Saldo Pana - Recarga entre Estudiantes")
    class SaldoPanaTests {

        @Test
        @DisplayName("SP-01: Recarga exitosa de estudiante a estudiante")
        void testSaldoPanaRecargaExitosa() {
            // Arrange
            double saldoInicialPana = estudiantePana.obtSaldo();
            double saldoInicialReceptor = estudianteRegular.obtSaldo();
            double montoRecarga = 2000.0;
            
            // Act - Recargar saldo del receptor usando el monedero del pana
            try {
                servicioCosto.procesarRecarga(estudiantePana, montoRecarga, null, null, estudianteRegular.obtCedula());
            } catch (Exception e) {
                fail("No debería lanzar excepción: " + e.getMessage());
            }

            // Assert
            assertEquals(saldoInicialPana - montoRecarga, estudiantePana.obtSaldo(), 0.001,
                "El pana debe descontarse el monto recargado");
            assertEquals(saldoInicialReceptor + montoRecarga, estudianteRegular.obtSaldo(), 0.001,
                "El receptor debe recibir el monto recargado");
        }

        @Test
        @DisplayName("SP-02: Intento de recarga con saldo insuficiente del pana")
        void testSaldoPanaSaldoInsuficiente() {
            // Arrange
            estudiantePana.setSaldo(500.0); // Saldo bajo
            double montoRecarga = 1000.0; // Monto alto
            double saldoInicialPana = estudiantePana.obtSaldo();
            double saldoInicialReceptor = estudianteRegular.obtSaldo();

            // Act & Assert
            assertThrows(SaldoInsuficienteException.class, () -> {
                servicioCosto.procesarRecarga(estudiantePana, montoRecarga, null, null, estudianteRegular.obtCedula());
            }, "Debe lanzar SaldoInsuficienteException");

            // Verificar que no se modificaron los saldos
            assertEquals(saldoInicialPana, estudiantePana.obtSaldo(), 0.001,
                "Saldo del pana no debe modificarse");
            assertEquals(saldoInicialReceptor, estudianteRegular.obtSaldo(), 0.001,
                "Saldo del receptor no debe modificarse");
        }

        @ParameterizedTest
        @CsvSource({
            "1000.0, 4000.0, 6000.0",
            "2500.5, 5500.5, 10500.5",
            "0.01, 4999.99, 8000.0",
            "7999.99, 0.01, 8000.0"
        })
        @DisplayName("SP-03: Múltiples montos de recarga pana válidos")
        void testSaldoPanaMontosValidos(double montoRecarga, double saldoFinalPana, double saldoFinalReceptor) {
            // Arrange
            estudiantePana.setSaldo(8000.0);
            estudianteRegular.setSaldo(4000.0);

            // Act
            try {
                servicioCosto.procesarRecarga(estudiantePana, montoRecarga, null, null, estudianteRegular.obtCedula());
            } catch (Exception e) {
                fail("No debería lanzar excepción: " + e.getMessage());
            }

            // Assert
            assertEquals(saldoFinalPana, estudiantePana.obtSaldo(), 0.001,
                "Saldo final del pana debe ser correcto");
            assertEquals(saldoFinalReceptor, estudianteRegular.obtSaldo(), 0.001,
                "Saldo final del receptor debe ser correcto");
        }

        @Test
        @DisplayName("SP-04: Recarga pana con monto cero - no debe modificar saldos")
        void testSaldoPanaMontoCero() {
            // Arrange
            double saldoInicialPana = estudiantePana.obtSaldo();
            double saldoInicialReceptor = estudianteRegular.obtSaldo();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                servicioCosto.procesarRecarga(estudiantePana, 0.0, null, null, estudianteRegular.obtCedula());
            }, "Debe lanzar excepción para monto cero");

            assertEquals(saldoInicialPana, estudiantePana.obtSaldo(), 0.001,
                "Saldo del pana no debe modificarse");
            assertEquals(saldoInicialReceptor, estudianteRegular.obtSaldo(), 0.001,
                "Saldo del receptor no debe modificarse");
        }

        @Test
        @DisplayName("SP-05: Recarga pana a sí mismo - no permitido")
        void testSaldoPanaRecargaASiMismo() {
            // Arrange
            double saldoInicial = estudianteRegular.obtSaldo();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                servicioCosto.procesarRecarga(estudianteRegular, 1000.0, null, null, estudianteRegular.obtCedula());
            }, "No debe permitirse recargar a sí mismo");

            assertEquals(saldoInicial, estudianteRegular.obtSaldo(), 0.001,
                "Saldo no debe modificarse al intentar recargarse a sí mismo");
        }

        @Test
        @DisplayName("SP-06: Recarga pana a estudiante nulo - excepción")
        void testSaldoPanaEstudianteNulo() {
            // Act & Assert - Usar ServicioCosto para Saldo Pana
            assertThrows(IllegalArgumentException.class, () -> {
                servicioCosto.procesarRecarga(estudiantePana, 1000.0, null, null, null);
            }, "Debe lanzar excepción para estudiante nulo");
        }
    }

    @Nested
    @DisplayName("Pruebas de Estudiante Exonerado")
    class EstudianteExoneradoTests {

        @Test
        @DisplayName("EX-01: Acceso exitoso de estudiante exonerado sin descuento")
        void testEstudianteExoneradoAccesoSinDescuento() throws Exception {
            // Arrange
            double costoServicio = 2500.0;
            double saldoInicial = estudianteExonerado.obtSaldo();

            // Act - Acceso al servicio (reconocimiento válido)
            double similitud = servicioBiometrico.calcularSimilitud(estudianteExonerado, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");

            // Act - Cobro del servicio
            servicioPago.procesarCobro(estudianteExonerado, costoServicio);

            // Assert
            assertEquals(saldoInicial, estudianteExonerado.obtSaldo(), 0.001,
                "Estudiante exonerado no debe tener descuento en el cobro");
        }

        @Test
        @DisplayName("EX-02: Validación de tipo estudiante exonerado")
        void testValidacionTipoExonerado() {
            // Assert
            assertTrue(estudianteExonerado.esExonerado(),
                "Debe identificarse como estudiante exonerado");
            assertFalse(estudianteExonerado.esBecario(),
                "No debe identificarse como becario");
            assertEquals("V11223344", estudianteExonerado.obtCedula(),
                "Cédula debe ser correcta");
        }

        @ParameterizedTest
        @CsvSource({
            "1000.0, 3000.0",
            "5000.0, 3000.0",
            "0.01, 3000.0",
            "999999.99, 3000.0"
        })
        @DisplayName("EX-03: Múltiples costos de servicio - sin descuento siempre")
        void testExoneradoMultiplesCostos(double costoServicio, double saldoFinalEsperado) {
            // Arrange
            estudianteExonerado.setSaldo(3000.0);

            // Act
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudianteExonerado, costoServicio);
            });

            // Assert
            assertEquals(saldoFinalEsperado, estudianteExonerado.obtSaldo(), 0.001,
                "Saldo no debe modificarse para estudiante exonerado");
        }

        @Test
        @DisplayName("EX-04: Estudiante exonerado con saldo cero - acceso permitido")
        void testExoneradoSaldoCero() throws Exception {
            // Arrange
            estudianteExonerado.setSaldo(0.0);
            double costoServicio = 5000.0;

            // Act - No debe lanzar SaldoInsuficienteException
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudianteExonerado, costoServicio);
            });

            // Assert
            assertEquals(0.0, estudianteExonerado.obtSaldo(), 0.001,
                "Saldo debe permanecer en cero");
        }

        @Test
        @DisplayName("EX-05: Comparación con estudiante regular - mismo costo")
        void testComparacionExoneradoVsRegular() throws Exception {
            // Arrange
            double costoServicio = 3000.0;
            double saldoInicialRegular = estudianteRegular.obtSaldo();
            double saldoInicialExonerado = estudianteExonerado.obtSaldo();

            // Act
            servicioPago.procesarCobro(estudianteRegular, costoServicio);
            servicioPago.procesarCobro(estudianteExonerado, costoServicio);

            // Assert
            assertEquals(saldoInicialRegular - costoServicio, estudianteRegular.obtSaldo(), 0.001,
                "Regular debe pagar costo completo");
            assertEquals(saldoInicialExonerado, estudianteExonerado.obtSaldo(), 0.001,
                "Exonerado no debe pagar nada");
        }
    }

    @Nested
    @DisplayName("Pruebas de Estudiante Becario")
    class EstudianteBecarioTests {

        @Test
        @DisplayName("BC-01: Acceso exitoso de estudiante becario con descuento 5%")
        void testEstudianteBecarioAccesoConDescuento() throws Exception {
            // Arrange
            double costoServicio = 2500.0;
            double saldoInicial = estudianteBecario.obtSaldo();
            double descuentoEsperado = costoServicio * 0.05; // 125.0
            double montoCobrar = costoServicio - descuentoEsperado; // 2375.0

            // Act - Acceso al servicio
            double similitud = servicioBiometrico.calcularSimilitud(estudianteBecario, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");

            // Act - Cobro del servicio
            servicioPago.procesarCobro(estudianteBecario, costoServicio);

            // Assert
            assertEquals(saldoInicial - montoCobrar, estudianteBecario.obtSaldo(), 0.001,
                "Becario debe pagar con 5% de descuento");
        }

        @Test
        @DisplayName("BC-02: Validación de tipo estudiante becario")
        void testValidacionTipoBecario() {
            // Assert
            assertTrue(estudianteBecario.esBecario(),
                "Debe identificarse como estudiante becario");
            assertFalse(estudianteBecario.esExonerado(),
                "No debe identificarse como exonerado");
            assertEquals(5.0, estudianteBecario.obtPorcentajeDescuento(), 0.001,
                "Porcentaje de descuento debe ser 5%");
        }

        @ParameterizedTest
        @CsvSource({
            "1000.0, 950.0",
            "5000.0, 4750.0",
            "2000.0, 1900.0",
            "3333.33, 3166.66"
        })
        @DisplayName("BC-03: Múltiples costos con descuento 5%")
        void testBecarioMultiplesCostos(double costoServicio, double montoEsperado) {
            // Arrange
            estudianteBecario.setSaldo(10000.0);
            double saldoInicial = estudianteBecario.obtSaldo();
            double descuento = costoServicio * 0.05;
            double montoCobrar = costoServicio - descuento;

            // Act
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudianteBecario, costoServicio);
            });

            // Assert
            assertEquals(saldoInicial - montoCobrar, estudianteBecario.obtSaldo(), 0.001,
                "Debe aplicar descuento del 5%");
        }

        @Test
        @DisplayName("BC-04: Estudiante becario con saldo insuficiente - usa descuento")
        void testBecarioSaldoInsuficiente() {
            // Arrange
            estudianteBecario.setSaldo(100.0); // Saldo bajo
            double costoServicio = 5000.0;

            // Act & Assert
            assertThrows(SaldoInsuficienteException.class, () -> {
                servicioPago.procesarCobro(estudianteBecario, costoServicio);
            }, "Debe lanzar SaldoInsuficienteException incluso con descuento");

            assertEquals(100.0, estudianteBecario.obtSaldo(), 0.001,
                "Saldo no debe modificarse");
        }

        @Test
        @DisplayName("BC-05: Comparación entre tipos de estudiantes")
        void testComparacionTiposEstudiantes() throws Exception {
            // Arrange
            double costoServicio = 2000.0;
            double saldoInicialRegular = 5000.0;
            double saldoInicialBecario = 5000.0;
            double saldoInicialExonerado = 5000.0;
            
            estudianteRegular.setSaldo(saldoInicialRegular);
            estudianteBecario.setSaldo(saldoInicialBecario);
            estudianteExonerado.setSaldo(saldoInicialExonerado);

            // Act
            servicioPago.procesarCobro(estudianteRegular, costoServicio); // Paga 2000
            servicioPago.procesarCobro(estudianteBecario, costoServicio);  // Paga 1900
            servicioPago.procesarCobro(estudianteExonerado, costoServicio); // Paga 0

            // Assert
            assertEquals(3000.0, estudianteRegular.obtSaldo(), 0.001, "Regular paga 100%");
            assertEquals(3100.0, estudianteBecario.obtSaldo(), 0.001, "Becario paga 95%");
            assertEquals(5000.0, estudianteExonerado.obtSaldo(), 0.001, "Exonerado paga 0%");
        }

        @Test
        @DisplayName("BC-06: Validación porcentaje descuento menor que regular")
        void testPorcentajeDescuentoValido() {
            // Arrange - Crear becario con porcentaje alto (inválido)
            assertThrows(IllegalArgumentException.class, () -> {
                new EstudianteBecario("V99999999", "test", "Test", "Test", 15.0);
            }, "Porcentaje de descuento no puede ser 15% (debe ser < regular)");

            // Arrange - Porcentaje válido
            EstudianteBecario becarioValido = new EstudianteBecario("V88888888", "test", "Test", "Test", 3.0);
            
            // Assert
            assertEquals(3.0, becarioValido.obtPorcentajeDescuento(), 0.001,
                "Porcentaje 3% debe ser válido");
        }
    }

    @Nested
    @DisplayName("Pruebas Integradas - Flujo Completo con Tipos Especiales")
    class FlujoCompletoTiposEspecialesTests {

        @Test
        @DisplayName("INT-01: Flujo completo - Saldo Pana + Becario")
        void testFlujoSaldoPanaBecario() throws Exception {
            // Arrange
            double montoRecarga = 3000.0;
            double costoServicio = 2000.0;
            double saldoInicialPana = estudiantePana.obtSaldo();
            double saldoInicialBecario = estudianteBecario.obtSaldo();

            // Act - Recarga pana
            try {
                servicioCosto.procesarRecarga(estudiantePana, montoRecarga, null, null, estudianteBecario.obtCedula());
            } catch (Exception e) {
                fail("No debería lanzar excepción en recarga: " + e.getMessage());
            }

            // Act - Acceso y cobro con descuento
            double similitud = servicioBiometrico.calcularSimilitud(estudianteBecario, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");
            
            servicioPago.procesarCobro(estudianteBecario, costoServicio);

            // Assert
            double descuento = costoServicio * 0.05; // 100.0
            double montoCobrar = costoServicio - descuento; // 1900.0
            
            assertEquals(saldoInicialPana - montoRecarga, estudiantePana.obtSaldo(), 0.001,
                "Pana debe descontarse recarga");
            assertEquals(saldoInicialBecario + montoRecarga - montoCobrar, estudianteBecario.obtSaldo(), 0.001,
                "Becario debe recibir recarga y pagar con descuento");
        }

        @Test
        @DisplayName("INT-02: Flujo completo - Exonerado sin saldo")
        void testFlujoExoneradoSinSaldo() throws Exception {
            // Arrange
            estudianteExonerado.setSaldo(0.0);
            double costoServicio = 5000.0;

            // Act - Acceso y cobro
            double similitud = servicioBiometrico.calcularSimilitud(estudianteExonerado, fotoValida);
            assertTrue(similitud >= 60.0, "Reconocimiento debe ser válido");
            
            servicioPago.procesarCobro(estudianteExonerado, costoServicio);

            // Assert
            assertEquals(0.0, estudianteExonerado.obtSaldo(), 0.001,
                "Exonerado con saldo cero debe acceder sin problemas");
        }

        @Test
        @DisplayName("INT-03: Comparación final de todos los tipos")
        void testComparacionTodosTipos() throws Exception {
            // Arrange
            double costoServicio = 4000.0;
            double saldoBase = 10000.0;
            
            estudianteRegular.setSaldo(saldoBase);
            estudianteBecario.setSaldo(saldoBase);
            estudianteExonerado.setSaldo(saldoBase);

            // Act - Todos acceden al mismo servicio
            servicioPago.procesarCobro(estudianteRegular, costoServicio);   // -4000
            servicioPago.procesarCobro(estudianteBecario, costoServicio);    // -3800 (5% desc)
            servicioPago.procesarCobro(estudianteExonerado, costoServicio); // -0

            // Assert
            assertEquals(6000.0, estudianteRegular.obtSaldo(), 0.001, "Regular: 100% pago");
            assertEquals(6200.0, estudianteBecario.obtSaldo(), 0.001, "Becario: 95% pago");
            assertEquals(10000.0, estudianteExonerado.obtSaldo(), 0.001, "Exonerado: 0% pago");
        }
    }
}
