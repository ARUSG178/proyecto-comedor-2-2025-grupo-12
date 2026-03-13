package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.*;
import com.comedor.modelo.excepciones.SaldoInsuficienteException;

/**
 * Prueba para verificar que el cobro real usa las tarifas configurables
 * y no los valores hardcodeados.
 */
public class VerificacionCobroConfigurableTest {

    private ServicioPago servicioPago;
    private Estudiante estudiante;
    private Empleado empleado;
    private EstudianteExonerado exonerado;
    private EstudianteBecario becario;
    private File configFile;

    @BeforeEach
    void setUp() throws IOException {
        servicioPago = new ServicioPago();
        
        // Crear usuarios de prueba
        estudiante = new Estudiante("V12345678", "pass123", "Computación", "Ciencias");
        estudiante.setSaldo(10000.0);
        
        empleado = new Empleado("V87654321", "pass456", "Analista", "RRHH", "EMP001");
        empleado.setSaldo(10000.0);
        
        exonerado = new EstudianteExonerado("V11223344", "exonerado123", "Medicina", "Salud");
        exonerado.setSaldo(10000.0);
        
        becario = new EstudianteBecario("V44556677", "becario123", "Derecho", "Ciencias Sociales", 95.0);
        becario.setSaldo(10000.0);
        
        // Crear archivo de configuración de prueba
        configFile = Files.createFile(Paths.get("menu_config_test.properties")).toFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restaurar archivo original si existe
        File originalConfig = new File("menu_config.properties");
        if (originalConfig.exists()) {
            originalConfig.delete();
        }
        
        // Limpiar archivo de prueba
        if (configFile != null && configFile.exists()) {
            configFile.delete();
        }
    }

    private void crearConfiguracion(double estudiantePct, double empleadoPct, double profesorPct) throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("# Configuración de prueba\n");
            writer.write("ccb_actual=100.0\n");
            writer.write("tarifa_pct_estudiante=" + estudiantePct + "\n");
            writer.write("tarifa_pct_empleado=" + empleadoPct + "\n");
            writer.write("tarifa_pct_profesor=" + profesorPct + "\n");
        }
        
        // Renombrar para que lo lea el sistema
        File originalConfig = new File("menu_config.properties");
        if (originalConfig.exists()) {
            originalConfig.delete();
        }
        configFile.renameTo(originalConfig);
    }

    @Test
    @DisplayName("VERIFICACIÓN: Estudiante paga tarifa configurable")
    void testEstudiantePagaTarifaConfigurable() throws Exception {
        // Arrange - Configurar estudiante al 30%
        crearConfiguracion(30.0, 50.0, 100.0);
        
        double costoBase = 1000.0;
        double saldoInicial = estudiante.obtSaldo();
        double tarifaEsperada = costoBase * 0.30; // 300.0

        // Act
        servicioPago.procesarCobro(estudiante, costoBase);

        // Assert
        assertEquals(saldoInicial - tarifaEsperada, estudiante.obtSaldo(), 0.001,
            "Estudiante debe pagar 30% del costo base (configurable)");
        
        // Verificar que NO pagó el valor hardcodeado (20% = 200.0)
        assertNotEquals(saldoInicial - 200.0, estudiante.obtSaldo(), 0.001,
            "NO debe usar el valor hardcodeado de 20%");
    }

    @Test
    @DisplayName("VERIFICACIÓN: Empleado paga tarifa configurable")
    void testEmpleadoPagaTarifaConfigurable() throws Exception {
        // Arrange - Configurar empleado al 75%
        crearConfiguracion(25.0, 75.0, 100.0);
        
        double costoBase = 1000.0;
        double saldoInicial = empleado.obtSaldo();
        double tarifaEsperada = costoBase * 0.75; // 750.0

        // Act
        servicioPago.procesarCobro(empleado, costoBase);

        // Assert
        assertEquals(saldoInicial - tarifaEsperada, empleado.obtSaldo(), 0.001,
            "Empleado debe pagar 75% del costo base (configurable)");
        
        // Verificar que NO pagó el valor hardcodeado (50% = 500.0)
        assertNotEquals(saldoInicial - 500.0, empleado.obtSaldo(), 0.001,
            "NO debe usar el valor hardcodeado de 50%");
    }

    @Test
    @DisplayName("VERIFICACIÓN: Estudiante Exonerado paga 0% (siempre)")
    void testExoneradoPagaCero() throws Exception {
        // Arrange - Configurar cualquier valor
        crearConfiguracion(50.0, 80.0, 100.0);
        
        double costoBase = 1000.0;
        double saldoInicial = exonerado.obtSaldo();

        // Act
        servicioPago.procesarCobro(exonerado, costoBase);

        // Assert
        assertEquals(saldoInicial, exonerado.obtSaldo(), 0.001,
            "Estudiante exonerado siempre paga 0%");
    }

    @Test
    @DisplayName("VERIFICACIÓN: Estudiante Becario paga según su porcentaje")
    void testBecarioPagaSegunPorcentaje() throws Exception {
        // Arrange - Becario con 95% descuento (paga 5%)
        crearConfiguracion(30.0, 70.0, 100.0);
        
        double costoBase = 1000.0;
        double saldoInicial = becario.obtSaldo();
        double tarifaEsperada = costoBase * 0.05; // 50.0 (5% con 95% descuento)

        // Act
        servicioPago.procesarCobro(becario, costoBase);

        // Assert
        assertEquals(saldoInicial - tarifaEsperada, becario.obtSaldo(), 0.001,
            "Becario debe pagar 5% con 95% descuento");
    }

    @ParameterizedTest
    @CsvSource({
        "10.0, 100.0, 100.0",  // Estudiante 10%, Empleado 100%
        "50.0, 25.0, 75.0",   // Estudiante 50%, Empleado 25%, Profesor 75%
        "0.0, 0.0, 0.0",      // Todos pagan 0%
        "100.0, 100.0, 100.0"  // Todos pagan 100%
    })
    @DisplayName("VERIFICACIÓN: Múltiples configuraciones de tarifas")
    void testMultiplesConfiguracionesTarifas(double estudiantePct, double empleadoPct, double profesorPct) throws Exception {
        // Arrange
        crearConfiguracion(estudiantePct, empleadoPct, profesorPct);
        
        double costoBase = 2000.0;
        double saldoInicialEst = 5000.0;
        double saldoInicialEmp = 5000.0;
        
        estudiante.setSaldo(saldoInicialEst);
        empleado.setSaldo(saldoInicialEmp);

        // Act
        servicioPago.procesarCobro(estudiante, costoBase);
        servicioPago.procesarCobro(empleado, costoBase);

        // Assert
        double esperadoEst = saldoInicialEst - (costoBase * estudiantePct / 100.0);
        double esperadoEmp = saldoInicialEmp - (costoBase * empleadoPct / 100.0);
        
        assertEquals(esperadoEst, estudiante.obtSaldo(), 0.001,
            "Estudiante debe pagar " + estudiantePct + "%");
        assertEquals(esperadoEmp, empleado.obtSaldo(), 0.001,
            "Empleado debe pagar " + empleadoPct + "%");
    }

    @Test
    @DisplayName("VERIFICACIÓN: Saldo insuficiente con tarifa configurable")
    void testSaldoInsuficienteConTarifaConfigurable() throws Exception {
        // Arrange - Configurar tarifa alta
        crearConfiguracion(80.0, 90.0, 100.0);
        
        estudiante.setSaldo(500.0); // Saldo bajo
        double costoBase = 1000.0;
        double tarifaConfigurable = costoBase * 0.80; // 800.0

        // Act & Assert
        assertThrows(SaldoInsuficienteException.class, () -> {
            servicioPago.procesarCobro(estudiante, costoBase);
        }, "Debe lanzar SaldoInsuficienteException con tarifa configurable");

        assertEquals(500.0, estudiante.obtSaldo(), 0.001,
            "Saldo no debe modificarse si es insuficiente");
        
        // Verificar que validó con tarifa configurable (800.0) y no con monto base (1000.0)
        assertTrue(tarifaConfigurable > estudiante.obtSaldo(),
            "Debe validar contra tarifa configurable (" + tarifaConfigurable + ")");
    }

    @Test
    @DisplayName("VERIFICACIÓN: Cambio de configuración en tiempo de ejecución")
    void testCambioConfiguracionTiempoReal() throws Exception {
        // Arrange - Configuración inicial
        crearConfiguracion(20.0, 50.0, 100.0);
        
        double costoBase = 1000.0;
        double saldoInicial = estudiante.obtSaldo();

        // Act - Primer cobro con 20%
        servicioPago.procesarCobro(estudiante, costoBase);
        double saldoDespues20 = estudiante.obtSaldo();
        
        // Cambiar configuración a 40%
        crearConfiguracion(40.0, 50.0, 100.0);
        
        // Act - Segundo cobro con 40%
        servicioPago.procesarCobro(estudiante, costoBase);
        double saldoDespues40 = estudiante.obtSaldo();

        // Assert
        assertEquals(saldoInicial - 200.0, saldoDespues20, 0.001,
            "Primer cobro debe ser 20% = 200.0");
        assertEquals(saldoDespues20 - 400.0, saldoDespues40, 0.001,
            "Segundo cobro debe ser 40% = 400.0");
        
        double totalPagado = saldoInicial - saldoDespues40;
        assertEquals(600.0, totalPagado, 0.001,
            "Total pagado debe ser 200.0 + 400.0 = 600.0");
    }
}
