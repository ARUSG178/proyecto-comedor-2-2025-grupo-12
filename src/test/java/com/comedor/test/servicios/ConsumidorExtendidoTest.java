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

import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.EstudianteExonerado;
import com.comedor.modelo.entidades.EstudianteBecario;
import com.comedor.modelo.excepciones.SaldoInsuficienteException;

/**
 * Pruebas unitarias extendidas para el Consumidor (Comensal)
 * Funcionalidades adicionales:
 * - Estudiantes Exonerados: acceso sin descuento
 * - Estudiantes Becarios: descuento del 5%
 */
public class ConsumidorExtendidoTest {

    private ServicioPago servicioPago;
    private Estudiante estudianteRegular;
    private EstudianteExonerado estudianteExonerado;
    private EstudianteBecario estudianteBecario;

    @BeforeEach
    void setUp() throws IOException {
        servicioPago = new ServicioPago();
        
        // Crear estudiantes de diferentes tipos
        estudianteRegular = new Estudiante("V12345678", "pass123", "Computación", "Ciencias");
        estudianteRegular.setSaldo(5000.0);
        
        estudianteExonerado = new EstudianteExonerado("V11223344", "exonerado123", "Medicina", "Salud");
        estudianteExonerado.setSaldo(3000.0);
        
        estudianteBecario = new EstudianteBecario("V44556677", "becario123", "Derecho", "Ciencias Sociales", 5.0);
        estudianteBecario.setSaldo(2000.0);
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

            // Act - Cobro del servicio (sin reconocimiento facial)
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
            "9999.99, 3000.0"
        })
        @DisplayName("EX-03: Estudiante exonerado mantiene saldo sin importar el costo")
        void testExoneradoMantieneSaldo(double costoServicio, double saldoEsperado) {
            // Arrange
            estudianteExonerado.setSaldo(3000.0);

            // Act
            try {
                servicioPago.procesarCobro(estudianteExonerado, costoServicio);
            } catch (Exception e) {
                fail("No debería lanzar excepción: " + e.getMessage());
            }

            // Assert
            assertEquals(saldoEsperado, estudianteExonerado.obtSaldo(), 0.001,
                "Estudiante exonerado no debe pagar nada");
        }

        @Test
        @DisplayName("EX-04: Estudiante exonerado con saldo cero - puede acceder")
        void testExoneradoSaldoCero() {
            // Arrange
            estudianteExonerado.setSaldo(0.0);
            double costoServicio = 5000.0;

            // Act & Assert - No debe lanzar excepción
            assertDoesNotThrow(() -> {
                servicioPago.procesarCobro(estudianteExonerado, costoServicio);
            }, "Estudiante exonerado con saldo cero debe poder acceder");

            assertEquals(0.0, estudianteExonerado.obtSaldo(), 0.001,
                "Saldo debe permanecer en cero");
        }
    }

    @Nested
    @DisplayName("Pruebas de Estudiante Becario")
    class EstudianteBecarioTests {

        @Test
        @DisplayName("BC-01: Acceso exitoso de estudiante becario con descuento")
        void testEstudianteBecarioAccesoConDescuento() throws Exception {
            // Arrange
            double costoServicio = 2000.0;
            double saldoInicial = estudianteBecario.obtSaldo();
            // El becario con 5% de descuento paga 95% del costo
            double montoCobrar = costoServicio * 0.95;

            // Act - Cobro del servicio (sin reconocimiento facial)
            servicioPago.procesarCobro(estudianteBecario, costoServicio);

            // Assert
            assertEquals(saldoInicial - montoCobrar, estudianteBecario.obtSaldo(), 0.001,
                "Debe aplicar descuento del 5%");
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
            "1000.0, 4050.0",
            "2000.0, 3100.0",
            "3000.0, 2150.0",
            "4000.0, 1200.0"
        })
        @DisplayName("BC-03: Estudiante becario paga 95% del costo (5% descuento)")
        void testBecarioPorcentajeDescuento(double costoServicio, double saldoEsperado) {
            // Arrange
            estudianteBecario.setSaldo(5000.0);

            // Act
            try {
                servicioPago.procesarCobro(estudianteBecario, costoServicio);
            } catch (Exception e) {
                fail("No debería lanzar excepción: " + e.getMessage());
            }

            // Assert
            assertEquals(saldoEsperado, estudianteBecario.obtSaldo(), 0.001,
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
    }

    @Nested
    @DisplayName("Pruebas de Comparación entre Tipos de Estudiante")
    class ComparacionTiposEstudianteTests {

        @Test
        @DisplayName("TC-01: Comparación de tarifas - Exonerado vs Regular vs Becario")
        void testComparacionTodosTipos() {
            // Arrange
            double costoServicio = 6000.0;
            estudianteRegular.setSaldo(10000.0);
            estudianteExonerado.setSaldo(10000.0);
            estudianteBecario.setSaldo(10000.0);

            // Act
            try {
                servicioPago.procesarCobro(estudianteRegular, costoServicio);
                servicioPago.procesarCobro(estudianteExonerado, costoServicio);
                servicioPago.procesarCobro(estudianteBecario, costoServicio);
            } catch (Exception e) {
                fail("No debería lanzar excepción: " + e.getMessage());
            }

            // Assert
            assertTrue(estudianteBecario.obtSaldo() < estudianteRegular.obtSaldo(),
                "Becario debe pagar menos que regular");
            assertTrue(estudianteExonerado.obtSaldo() > estudianteBecario.obtSaldo(),
                "Exonerado debe pagar menos que becario");
            assertEquals(10000.0, estudianteExonerado.obtSaldo(), 0.001,
                "Exonerado no paga nada");
        }

        @Test
        @DisplayName("TC-02: Validación de métodos de tipo")
        void testValidacionMetodosTipo() {
            // Assert - Estudiante exonerado
            assertTrue(estudianteExonerado.esExonerado(), "Exonerado debe ser exonerado");
            assertFalse(estudianteExonerado.esBecario(), "Exonerado no es becario");

            // Assert - Estudiante becario
            assertFalse(estudianteBecario.esExonerado(), "Becario no es exonerado");
            assertTrue(estudianteBecario.esBecario(), "Becario debe ser becario");
        }
    }

    @Nested
    @DisplayName("Pruebas de Flujo Completo - Tipos Especiales")
    class FlujoCompletoTiposEspecialesTests {

        @Test
        @DisplayName("INT-01: Flujo completo - Exonerado sin saldo")
        void testFlujoExoneradoSinSaldo() throws Exception {
            // Arrange
            estudianteExonerado.setSaldo(0.0);
            double costoServicio = 5000.0;

            // Act - Cobro
            servicioPago.procesarCobro(estudianteExonerado, costoServicio);

            // Assert
            assertEquals(0.0, estudianteExonerado.obtSaldo(), 0.001,
                "Exonerado sin saldo debe poder acceder sin pagar");
        }
    }
}
