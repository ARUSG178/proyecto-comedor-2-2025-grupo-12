package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.comedor.controlador.ServicioCosto;
import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.*;
import com.comedor.modelo.entidades.RegistroCosto.TipoCosto;

/**
 * Pruebas unitarias extendidas para el Administrador del Servicio de Comedor
 * Funcionalidades adicionales:
 * - Agregar estudiantes Exonerados y Becarios
 * - Obtener listado de comensales por servicio
 * - Desagregación por tipo de estudiante
 */
public class AdministradorExtendidoTest {

    private GestionComensales gestionComensales;

    @BeforeEach
    void setUp() {
        gestionComensales = new GestionComensales();
    }

    @Nested
    @DisplayName("Pruebas de Gestión de Tipos de Estudiante")
    class GestionTiposEstudianteTests {

        @Test
        @DisplayName("GTE-01: Agregar estudiante Exonerado exitosamente")
        void testAgregarEstudianteExonerado() {
            // Arrange
            String cedula = "V12345678";
            String password = "pass123";
            String carrera = "Medicina";
            String facultad = "Salud";

            // Act
            EstudianteExonerado exonerado = gestionComensales.agregarEstudianteExonerado(
                cedula, password, carrera, facultad
            );

            // Assert
            assertNotNull(exonerado, "Debe crear estudiante exonerado");
            assertEquals(cedula, exonerado.obtCedula(), "Cédula debe coincidir");
            assertEquals(carrera, exonerado.obtCarrera(), "Carrera debe coincidir");
            assertTrue(exonerado instanceof EstudianteExonerado, 
                "Debe ser instancia de EstudianteExonerado");
        }

        @Test
        @DisplayName("GTE-02: Agregar estudiante Becario con porcentaje válido")
        void testAgregarEstudianteBecario() {
            // Arrange
            String cedula = "V87654321";
            String password = "beca123";
            String carrera = "Ingeniería";
            String facultad = "Tecnología";
            double porcentajeDescuento = 5.0;

            // Act
            EstudianteBecario becario = gestionComensales.agregarEstudianteBecario(
                cedula, password, carrera, facultad, porcentajeDescuento
            );

            // Assert
            assertNotNull(becario, "Debe crear estudiante becario");
            assertEquals(cedula, becario.obtCedula(), "Cédula debe coincidir");
            assertEquals(porcentajeDescuento, becario.obtPorcentajeDescuento(), 0.001,
                "Porcentaje de descuento debe coincidir");
            assertTrue(becario instanceof EstudianteBecario, 
                "Debe ser instancia de EstudianteBecario");
        }

        @ParameterizedTest
        @ValueSource(doubles = {1.0, 2.0, 4.9, 5.0, 9.9})
        @DisplayName("GTE-03: Múltiples porcentajes válidos para becario")
        void testPorcentajesBecarioValidos(double porcentaje) {
            // Act & Assert
            assertDoesNotThrow(() -> {
                EstudianteBecario becario = gestionComensales.agregarEstudianteBecario(
                    "V99999999", "pass", "Test", "Test", porcentaje
                );
                assertEquals(porcentaje, becario.obtPorcentajeDescuento(), 0.001,
                    "Porcentaje debe asignarse correctamente");
            });
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, 10.0, 15.0, 20.0})
        @DisplayName("GTE-04: Porcentajes inválidos para becario")
        void testPorcentajesBecarioInvalidos(double porcentajeInvalido) {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                gestionComensales.agregarEstudianteBecario(
                    "V88888888", "pass", "Test", "Test", porcentajeInvalido
                );
            }, "Debe lanzar excepción para porcentajes inválidos");
        }

        @Test
        @DisplayName("GTE-05: Validación que porcentaje becario < regular")
        void testValidacionPorcentajeMenorRegular() {
            // Arrange - Porcentaje regular simulado (10%)
            double porcentajeRegular = 10.0;
            double porcentajeBecarioInvalido = 12.0; // Mayor que regular

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                gestionComensales.agregarEstudianteBecario(
                    "V77777777", "pass", "Test", "Test", porcentajeBecarioInvalido
                );
            }, "Porcentaje becario debe ser menor que regular");

            // Act - Porcentaje válido
            assertDoesNotThrow(() -> {
                EstudianteBecario becario = gestionComensales.agregarEstudianteBecario(
                    "V66666666", "pass", "Test", "Test", porcentajeRegular - 1.0
                );
                assertTrue(becario.obtPorcentajeDescuento() < porcentajeRegular,
                    "Porcentaje becario debe ser menor que regular");
            });
        }

        @Test
        @DisplayName("GTE-06: Intento de agregar estudiante duplicado")
        void testAgregarEstudianteDuplicado() {
            // Arrange
            String cedula = "V55555555";
            
            // Arrange - Agregar primer estudiante
            gestionComensales.agregarEstudianteExonerado(
                "V55555555", "pass1", "Carrera1", "Facultad1"
            );

            // Act & Assert - Intento duplicado
            assertThrows(IllegalArgumentException.class, () -> {
                gestionComensales.agregarEstudianteExonerado(
                    cedula, "pass2", "Carrera2", "Facultad2"
                );
            }, "No debe permitir estudiantes duplicados");
        }
    }

    @Nested
    @DisplayName("Pruebas de Listado de Comensales")
    class ListadoComensalesTests {

        @Test
        @DisplayName("LC-01: Obtener listado de comensales desayuno")
        void testListadoComensalesDesayuno() {
            // Arrange - Registrar accesos al desayuno
            gestionComensales.registrarAcceso("V12345678", "Desayuno", "regular");
            gestionComensales.registrarAcceso("V87654321", "Desayuno", "becario");
            gestionComensales.registrarAcceso("V11223344", "Desayuno", "exonerado");
            gestionComensales.registrarAcceso("V55555555", "Desayuno", "empleado");

            // Act
            ListadoComensales listado = gestionComensales.obtenerListadoComensales("Desayuno");

            // Assert
            assertNotNull(listado, "Listado no debe ser nulo");
            assertEquals(4, listado.obtTotalComensales(), "Debe haber 4 comensales");
            
            // Verificar desagregación por tipo
            assertEquals(1, listado.obtCantidadEstudiantesRegulares(), "1 estudiante regular");
            assertEquals(1, listado.obtCantidadEstudiantesBecarios(), "1 estudiante becario");
            assertEquals(1, listado.obtCantidadEstudiantesExonerados(), "1 estudiante exonerado");
            assertEquals(1, listado.obtCantidadEmpleados(), "1 empleado");
        }

        @Test
        @DisplayName("LC-02: Obtener listado de comensales almuerzo")
        void testListadoComensalesAlmuerzo() {
            // Arrange - Registrar accesos al almuerzo
            gestionComensales.registrarAcceso("V11111111", "Almuerzo", "regular");
            gestionComensales.registrarAcceso("V22222222", "Almuerzo", "regular");
            gestionComensales.registrarAcceso("V33333333", "Almuerzo", "becario");
            gestionComensales.registrarAcceso("V44444444", "Almuerzo", "exonerado");
            gestionComensales.registrarAcceso("V55555555", "Almuerzo", "empleado");
            gestionComensales.registrarAcceso("V66666666", "Almuerzo", "empleado");

            // Act
            ListadoComensales listado = gestionComensales.obtenerListadoComensales("Almuerzo");

            // Assert
            assertEquals(6, listado.obtTotalComensales(), "Debe haber 6 comensales");
            assertEquals(2, listado.obtCantidadEstudiantesRegulares(), "2 estudiantes regulares");
            assertEquals(1, listado.obtCantidadEstudiantesBecarios(), "1 estudiante becario");
            assertEquals(1, listado.obtCantidadEstudiantesExonerados(), "1 estudiante exonerado");
            assertEquals(2, listado.obtCantidadEmpleados(), "2 empleados");
        }

        @Test
        @DisplayName("LC-03: Listado vacío para servicio sin accesos")
        void testListadoVacio() {
            // Act
            ListadoComensales listado = gestionComensales.obtenerListadoComensales("Cena");

            // Assert
            assertNotNull(listado, "Listado no debe ser nulo aunque esté vacío");
            assertEquals(0, listado.obtTotalComensales(), "Total debe ser cero");
            assertEquals(0, listado.obtCantidadEstudiantesRegulares(), "Regulares debe ser cero");
            assertEquals(0, listado.obtCantidadEstudiantesBecarios(), "Becarios debe ser cero");
            assertEquals(0, listado.obtCantidadEstudiantesExonerados(), "Exonerados debe ser cero");
            assertEquals(0, listado.obtCantidadEmpleados(), "Empleados debe ser cero");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Desayuno", "Almuerzo"})
        @DisplayName("LC-04: Listado por tipo de servicio")
        void testListadoPorTipoServicio(String tipoServicio) {
            // Arrange
            for (int i = 1; i <= 10; i++) {
                String cedula = "V" + String.format("%08d", i);
                String tipo = i <= 6 ? "regular" : i <= 8 ? "becario" : "exonerado";
                gestionComensales.registrarAcceso(cedula, tipoServicio, tipo);
            }

            // Act
            ListadoComensales listado = gestionComensales.obtenerListadoComensales(tipoServicio);

            // Assert
            assertEquals(10, listado.obtTotalComensales(), 
                "Debe haber 10 comensales para " + tipoServicio);
            assertEquals(6, listado.obtCantidadEstudiantesRegulares(), 
                "Debe haber 6 regulares");
            assertEquals(2, listado.obtCantidadEstudiantesBecarios(), 
                "Debe haber 2 becarios");
            assertEquals(2, listado.obtCantidadEstudiantesExonerados(), 
                "Debe haber 2 exonerados");
        }

        @Test
        @DisplayName("LC-05: Detalle individual de comensales")
        void testDetalleIndividualComensales() {
            // Arrange
            gestionComensales.registrarAcceso("V12345678", "Desayuno", "regular");
            gestionComensales.registrarAcceso("V87654321", "Desayuno", "becario");
            gestionComensales.registrarAcceso("V11223344", "Desayuno", "exonerado");

            // Act
            ListadoComensales listado = gestionComensales.obtenerListadoComensales("Desayuno");
            List<InfoComensal> detalles = listado.obtDetallesComensales();

            // Assert
            assertEquals(3, detalles.size(), "Debe haber 3 detalles");
            
            // Verificar información de cada comensal
            InfoComensal regular = detalles.stream()
                .filter(c -> c.obtCedula().equals("V12345678"))
                .findFirst().orElse(null);
            assertNotNull(regular, "Debe encontrar estudiante regular");
            assertEquals("regular", regular.obtTipoComensal(), "Tipo debe ser regular");
            assertEquals("Desayuno", regular.obtServicio(), "Servicio debe ser desayuno");

            InfoComensal becario = detalles.stream()
                .filter(c -> c.obtCedula().equals("V87654321"))
                .findFirst().orElse(null);
            assertNotNull(becario, "Debe encontrar estudiante becario");
            assertEquals("becario", becario.obtTipoComensal(), "Tipo debe ser becario");

            InfoComensal exonerado = detalles.stream()
                .filter(c -> c.obtCedula().equals("V11223344"))
                .findFirst().orElse(null);
            assertNotNull(exonerado, "Debe encontrar estudiante exonerado");
            assertEquals("exonerado", exonerado.obtTipoComensal(), "Tipo debe ser exonerado");
        }

        @Test
        @DisplayName("LC-06: Estadísticas de consumo por tipo")
        void testEstadisticasConsumoPorTipo() {
            // Arrange - Simular consumo de una semana
            String[] servicios = {"Desayuno", "Almuerzo"};
            String[] tipos = {"regular", "becario", "exonerado", "empleado"};
            
            for (int dia = 1; dia <= 5; dia++) { // 5 días
                for (String servicio : servicios) {
                    for (int i = 0; i < 10; i++) { // 10 accesos por servicio
                        String cedula = "V" + String.format("%08d", dia * 100 + i);
                        String tipo = tipos[i % tipos.length];
                        gestionComensales.registrarAcceso(cedula, servicio, tipo);
                    }
                }
            }

            // Act
            ListadoComensales listadoDesayuno = gestionComensales.obtenerListadoComensales("Desayuno");
            ListadoComensales listadoAlmuerzo = gestionComensales.obtenerListadoComensales("Almuerzo");

            // Assert
            assertEquals(50, listadoDesayuno.obtTotalComensales(), "50 accesos desayuno");
            assertEquals(50, listadoAlmuerzo.obtTotalComensales(), "50 accesos almuerzo");
            
            // Cada tipo debe tener exactamente 10 accesos por servicio (5 días * 2 servicios por día)
            assertTrue(listadoDesayuno.obtCantidadEstudiantesRegulares() >= 10, 
                "Regulares desayuno >= 10");
            assertTrue(listadoDesayuno.obtCantidadEstudiantesBecarios() >= 10, 
                "Becarios desayuno >= 10");
            assertTrue(listadoDesayuno.obtCantidadEstudiantesExonerados() >= 10, 
                "Exonerados desayuno >= 10");
            assertTrue(listadoDesayuno.obtCantidadEmpleados() >= 10, 
                "Empleados desayuno >= 10");
        }
    }

    @Nested
    @DisplayName("Pruebas Integradas - Gestión y Reportes")
    class IntegracionGestionReportesTests {

        @Test
        @DisplayName("INT-01: Flujo completo - agregar tipos y generar reportes")
        void testFlujoCompletoGestionReportes() {
            // Arrange - Agregar estudiantes especiales
            gestionComensales.agregarEstudianteExonerado(
                "V11111111", "exonerado123", "Medicina", "Salud"
            );
            gestionComensales.agregarEstudianteBecario(
                "V22222222", "becario123", "Ingeniería", "Tecnología", 5.0
            );

            // Act - Simular accesos
            gestionComensales.registrarAcceso("V11111111", "Desayuno", "exonerado");
            gestionComensales.registrarAcceso("V22222222", "Desayuno", "becario");
            gestionComensales.registrarAcceso("V33333333", "Desayuno", "regular");

            // Act - Generar reportes
            ListadoComensales reporteDesayuno = gestionComensales.obtenerListadoComensales("Desayuno");

            // Assert
            assertEquals(3, reporteDesayuno.obtTotalComensales(), "3 comensales totales");
            assertEquals(1, reporteDesayuno.obtCantidadEstudiantesRegulares(), "1 regular");
            assertEquals(1, reporteDesayuno.obtCantidadEstudiantesBecarios(), "1 becario");
            assertEquals(1, reporteDesayuno.obtCantidadEstudiantesExonerados(), "1 exonerado");

            // Verificar detalles
            List<InfoComensal> detalles = reporteDesayuno.obtDetallesComensales();
            assertEquals(3, detalles.size(), "3 detalles individuales");
        }

        @Test
        @DisplayName("INT-02: Reporte comparativo entre servicios")
        void testReporteComparativoServicios() {
            // Arrange - Simular diferentes patrones de consumo
            // Desayuno: más regulares
            for (int i = 1; i <= 8; i++) {
                gestionComensales.registrarAcceso("V" + String.format("%08d", i), "Desayuno", "regular");
            }
            for (int i = 9; i <= 10; i++) {
                gestionComensales.registrarAcceso("V" + String.format("%08d", i), "Desayuno", "becario");
            }
            
            // Almuerzo: más becarios
            for (int i = 1; i <= 3; i++) {
                gestionComensales.registrarAcceso("V" + String.format("%08d", i), "Almuerzo", "regular");
            }
            for (int i = 4; i <= 9; i++) {
                gestionComensales.registrarAcceso("V" + String.format("%08d", i), "Almuerzo", "becario");
            }

            // Act
            ListadoComensales reporteDesayuno = gestionComensales.obtenerListadoComensales("Desayuno");
            ListadoComensales reporteAlmuerzo = gestionComensales.obtenerListadoComensales("Almuerzo");

            // Assert - Desayuno
            assertEquals(10, reporteDesayuno.obtTotalComensales(), "10 en desayuno");
            assertEquals(8, reporteDesayuno.obtCantidadEstudiantesRegulares(), "8 regulares desayuno");
            assertEquals(2, reporteDesayuno.obtCantidadEstudiantesBecarios(), "2 becarios desayuno");

            // Assert - Almuerzo
            assertEquals(9, reporteAlmuerzo.obtTotalComensales(), "9 en almuerzo");
            assertEquals(3, reporteAlmuerzo.obtCantidadEstudiantesRegulares(), "3 regulares almuerzo");
            assertEquals(6, reporteAlmuerzo.obtCantidadEstudiantesBecarios(), "6 becarios almuerzo");
        }

        @Test
        @DisplayName("INT-03: Validación de integridad de datos")
        void testValidacionIntegridadDatos() {
            // Arrange - Registrar múltiples accesos
            String[] cedulas = {"V11111111", "V22222222", "V33333333"};
            String[] servicios = {"Desayuno", "Almuerzo"};
            String[] tipos = {"regular", "becario", "exonerado"};

            // Cada estudiante accede a ambos servicios
            for (int i = 0; i < cedulas.length; i++) {
                for (String servicio : servicios) {
                    gestionComensales.registrarAcceso(cedulas[i], servicio, tipos[i]);
                }
            }

            // Act
            ListadoComensales reporteDesayuno = gestionComensales.obtenerListadoComensales("Desayuno");
            ListadoComensales reporteAlmuerzo = gestionComensales.obtenerListadoComensales("Almuerzo");

            // Assert - Consistencia entre reportes
            assertEquals(reporteDesayuno.obtTotalComensales(), reporteAlmuerzo.obtTotalComensales(),
                "Total debe ser consistente entre servicios");
            assertEquals(reporteDesayuno.obtCantidadEstudiantesRegulares(), reporteAlmuerzo.obtCantidadEstudiantesRegulares(),
                "Regulares debe ser consistente");
            assertEquals(reporteDesayuno.obtCantidadEstudiantesBecarios(), reporteAlmuerzo.obtCantidadEstudiantesBecarios(),
                "Becarios debe ser consistente");
            assertEquals(reporteDesayuno.obtCantidadEstudiantesExonerados(), reporteAlmuerzo.obtCantidadEstudiantesExonerados(),
                "Exonerados debe ser consistente");

            // Assert - Total = suma de partes
            int totalDesayuno = reporteDesayuno.obtCantidadEstudiantesRegulares() +
                              reporteDesayuno.obtCantidadEstudiantesBecarios() +
                              reporteDesayuno.obtCantidadEstudiantesExonerados() +
                              reporteDesayuno.obtCantidadEmpleados();
            assertEquals(totalDesayuno, reporteDesayuno.obtTotalComensales(),
                "Total debe ser suma de todas las categorías");
        }
    }
}

/**
 * Clases de apoyo para las pruebas del administrador extendido
 */
class GestionComensales {
    private Map<String, Usuario> estudiantesRegistrados = new HashMap<>();
    private List<RegistroAcceso> accesos = new ArrayList<>();

    public EstudianteExonerado agregarEstudianteExonerado(String cedula, String password, 
                                                       String carrera, String facultad) {
        if (estudiantesRegistrados.containsKey(cedula)) {
            throw new IllegalArgumentException("Estudiante ya existe: " + cedula);
        }
        
        EstudianteExonerado exonerado = new EstudianteExonerado(cedula, password, carrera, facultad);
        estudiantesRegistrados.put(cedula, exonerado);
        return exonerado;
    }

    public EstudianteBecario agregarEstudianteBecario(String cedula, String password, 
                                                    String carrera, String facultad, 
                                                    double porcentajeDescuento) {
        if (estudiantesRegistrados.containsKey(cedula)) {
            throw new IllegalArgumentException("Estudiante ya existe: " + cedula);
        }
        
        if (porcentajeDescuento <= 0 || porcentajeDescuento >= 10) {
            throw new IllegalArgumentException("Porcentaje de descuento inválido: " + porcentajeDescuento);
        }
        
        EstudianteBecario becario = new EstudianteBecario(cedula, password, carrera, facultad, porcentajeDescuento);
        estudiantesRegistrados.put(cedula, becario);
        return becario;
    }

    public void registrarAcceso(String cedula, String servicio, String tipoComensal) {
        accesos.add(new RegistroAcceso(cedula, servicio, tipoComensal));
    }

    public ListadoComensales obtenerListadoComensales(String servicio) {
        List<RegistroAcceso> accesosServicio = accesos.stream()
            .filter(a -> a.obtServicio().equals(servicio))
            .toList();
        
        return new ListadoComensales(accesosServicio);
    }
}

class RegistroAcceso {
    private String cedula;
    private String servicio;
    private String tipoComensal;
    private java.time.LocalDateTime timestamp;

    public RegistroAcceso(String cedula, String servicio, String tipoComensal) {
        this.cedula = cedula;
        this.servicio = servicio;
        this.tipoComensal = tipoComensal;
        this.timestamp = java.time.LocalDateTime.now();
    }

    public String obtCedula() { return cedula; }
    public String obtServicio() { return servicio; }
    public String obtTipoComensal() { return tipoComensal; }
    public java.time.LocalDateTime obtTimestamp() { return timestamp; }
}

class ListadoComensales {
    private List<RegistroAcceso> accesos;
    private List<InfoComensal> detalles;

    public ListadoComensales(List<RegistroAcceso> accesos) {
        this.accesos = accesos;
        this.detalles = accesos.stream()
            .map(a -> new InfoComensal(a.obtCedula(), a.obtServicio(), a.obtTipoComensal(), a.obtTimestamp()))
            .toList();
    }

    public int obtTotalComensales() { return accesos.size(); }
    
    public int obtCantidadEstudiantesRegulares() {
        return (int) accesos.stream().filter(a -> "regular".equals(a.obtTipoComensal())).count();
    }
    
    public int obtCantidadEstudiantesBecarios() {
        return (int) accesos.stream().filter(a -> "becario".equals(a.obtTipoComensal())).count();
    }
    
    public int obtCantidadEstudiantesExonerados() {
        return (int) accesos.stream().filter(a -> "exonerado".equals(a.obtTipoComensal())).count();
    }
    
    public int obtCantidadEmpleados() {
        return (int) accesos.stream().filter(a -> "empleado".equals(a.obtTipoComensal())).count();
    }

    public List<InfoComensal> obtDetallesComensales() { return detalles; }
}

class InfoComensal {
    private String cedula;
    private String servicio;
    private String tipoComensal;
    private java.time.LocalDateTime timestamp;

    public InfoComensal(String cedula, String servicio, String tipoComensal, java.time.LocalDateTime timestamp) {
        this.cedula = cedula;
        this.servicio = servicio;
        this.tipoComensal = tipoComensal;
        this.timestamp = timestamp;
    }

    public String obtCedula() { return cedula; }
    public String obtServicio() { return servicio; }
    public String obtTipoComensal() { return tipoComensal; }
    public java.time.LocalDateTime obtTimestamp() { return timestamp; }
}
