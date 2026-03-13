package com.comedor.test.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.comedor.controlador.ServicioCosto;
import com.comedor.controlador.ServicioMenu;
import com.comedor.modelo.entidades.*;

/**
 * Pruebas unitarias para el Administrador del Servicio de Comedor
 * Funcionalidades:
 * - Cálculo completo del CCB y su impacto en tarifas
 * - Configuración de menús (desayuno y almuerzo)
 */
public class AdministradorTest {

    private ServicioCosto servicioCosto;
    private ServicioMenu servicioMenu;
    private Administrador administrador;

    @BeforeEach
    void setUp() {
        servicioCosto = new ServicioCosto();
        servicioMenu = new ServicioMenu();
        administrador = new Administrador("V11223344", "admin123", "ADMIN001");
    }

    @Nested
    @DisplayName("Pruebas de Cálculo Completo del CCB")
    class CalculoCCBTests {

        @Test
        @DisplayName("CCB-01: Cálculo completo con valores estándar")
        void testCalculoCompletoEstandar() {
            // Arrange
            double costosFijos = 10000.0;
            double costosVariables = 8000.0;
            int produccionBandejas = 200;
            double porcentajeMerma = 0.15; // 15%

            // Act
            double ccbResultado = servicioCosto.calcularRegistrarCCBCompleto(
                costosFijos, costosVariables, produccionBandejas, porcentajeMerma
            );

            // Assert
            double totalCostos = costosFijos + costosVariables; // 18000
            double costoUnitarioBase = totalCostos / produccionBandejas; // 90.0
            double ccbEsperado = costoUnitarioBase * (1 + porcentajeMerma); // 103.5
            
            assertEquals(ccbEsperado, ccbResultado, 0.001,
                "CCB calculado debe coincidir con fórmula: (costos/producción) * (1 + merma)");
            
            // Verificar que se actualizó el CCB actual
            assertEquals(ccbEsperado, servicioCosto.obtenerCCBActual(), 0.001,
                "CCB actual debe actualizarse con el cálculo");
        }

        @ParameterizedTest
        @CsvSource({
            "5000.0, 3000.0, 100, 0.10, 88.0",
            "15000.0, 10000.0, 500, 0.20, 100.0",
            "8000.0, 4000.0, 150, 0.05, 82.67",
            "20000.0, 15000.0, 1000, 0.25, 87.5"
        })
        @DisplayName("CCB-02: Múltiples escenarios de cálculo CCB")
        void testEscenariosCalculoCCB(double fijos, double variables, 
                                    int produccion, double merma, double esperado) {
            // Act
            double resultado = servicioCosto.calcularRegistrarCCBCompleto(
                fijos, variables, produccion, merma
            );

            // Assert
            assertEquals(esperado, resultado, 0.01,
                "CCB debe calcularse correctamente para diferentes escenarios");
        }

        @Test
        @DisplayName("CCB-03: Cálculo con merma cero")
        void testCalculoMermaCero() {
            // Arrange
            double costosFijos = 12000.0;
            double costosVariables = 6000.0;
            int produccion = 300;
            double mermaCero = 0.0;

            // Act
            double resultado = servicioCosto.calcularRegistrarCCBCompleto(
                costosFijos, costosVariables, produccion, mermaCero
            );

            // Assert
            double costoUnitarioBase = (costosFijos + costosVariables) / produccion; // 60.0
            assertEquals(costoUnitarioBase, resultado, 0.001,
                "Con merma cero, CCB debe igualar al costo unitario base");
        }

        @Test
        @DisplayName("CCB-04: Cálculo con producción mínima")
        void testCalculoProduccionMinima() {
            // Arrange
            double costosFijos = 5000.0;
            double costosVariables = 2000.0;
            int produccionMinima = 10;
            double merma = 0.10;

            // Act
            double resultado = servicioCosto.calcularRegistrarCCBCompleto(
                costosFijos, costosVariables, produccionMinima, merma
            );

            // Assert
            double costoUnitarioBase = (costosFijos + costosVariables) / produccionMinima; // 700.0
            double ccbEsperado = costoUnitarioBase * (1 + merma); // 770.0
            assertEquals(ccbEsperado, resultado, 0.001,
                "Debe manejar producciones pequeñas correctamente");
        }

        @Test
        @DisplayName("CCB-05: Cálculo con costos variables cero")
        void testCalculoCostosVariablesCero() {
            // Arrange
            double costosFijos = 10000.0;
            double costosVariablesCero = 0.0;
            int produccion = 200;
            double merma = 0.12;

            // Act
            double resultado = servicioCosto.calcularRegistrarCCBCompleto(
                costosFijos, costosVariablesCero, produccion, merma
            );

            // Assert
            double costoUnitarioBase = costosFijos / produccion; // 50.0
            double ccbEsperado = costoUnitarioBase * (1 + merma); // 56.0
            assertEquals(ccbEsperado, resultado, 0.001,
                "Debe calcular correctamente con solo costos fijos");
        }
    }

    @Nested
    @DisplayName("Pruebas de Impacto en Tarifas por Tipo de Usuario")
    class ImpactoTarifasTests {

        @Test
        @DisplayName("TAR-01: Cálculo de tarifas diferenciadas por tipo de usuario")
        void testCalculoTarifasDiferenciadas() {
            // Arrange
            double ccbBase = 100.0; // CCB calculado
            
            // Tasas de markup por tipo de usuario (simuladas)
            double tasaEstudiante = 1.0; // Sin markup
            double tasaEmpleado = 1.2;    // 20% markup
            double tasaAdministrador = 0.8; // 20% descuento

            // Act
            double tarifaEstudiante = ccbBase * tasaEstudiante; // 100.0
            double tarifaEmpleado = ccbBase * tasaEmpleado;     // 120.0
            double tarifaAdministrador = ccbBase * tasaAdministrador; // 80.0

            // Assert
            assertEquals(100.0, tarifaEstudiante, 0.001,
                "Estudiante paga tarifa base");
            assertEquals(120.0, tarifaEmpleado, 0.001,
                "Empleado paga 20% adicional");
            assertEquals(80.0, tarifaAdministrador, 0.001,
                "Administrador tiene 20% descuento");
        }

        @ParameterizedTest
        @CsvSource({
            "50.0, 50.0, 60.0, 40.0",
            "75.5, 75.5, 90.6, 60.4",
            "150.0, 150.0, 180.0, 120.0",
            "200.25, 200.25, 240.3, 160.2"
        })
        @DisplayName("TAR-02: Impacto de CCB en diferentes tarifas")
        void testImpactoCCBTarifas(double ccb, double estudiante, double empleado, double admin) {
            // Act & Assert - Verificar cálculo de tarifas
            assertEquals(estudiante, ccb * 1.0, 0.01, "Tarifa estudiante = CCB");
            assertEquals(empleado, ccb * 1.2, 0.01, "Tarifa empleado = CCB * 1.2");
            assertEquals(admin, ccb * 0.8, 0.01, "Tarifa admin = CCB * 0.8");
        }

        @Test
        @DisplayName("TAR-03: Cálculo de ingresos proyectados por tipo de usuario")
        void testCalculoIngresosProyectados() {
            // Arrange
            double ccb = 100.0;
            int estudiantesEsperados = 150;
            int empleadosEsperados = 80;
            int administradoresEsperados = 5;

            // Act
            double ingresosEstudiantes = ccb * 1.0 * estudiantesEsperados; // 15000
            double ingresosEmpleados = ccb * 1.2 * empleadosEsperados;     // 9600
            double ingresosAdministradores = ccb * 0.8 * administradoresEsperados; // 400
            double ingresosTotales = ingresosEstudiantes + ingresosEmpleados + ingresosAdministradores;

            // Assert
            assertEquals(15000.0, ingresosEstudiantes, 0.001,
                "Ingresos estudiantes calculados correctamente");
            assertEquals(9600.0, ingresosEmpleados, 0.001,
                "Ingresos empleados calculados correctamente");
            assertEquals(400.0, ingresosAdministradores, 0.001,
                "Ingresos administradores calculados correctamente");
            assertEquals(25000.0, ingresosTotales, 0.001,
                "Ingresos totales calculados correctamente");
        }
    }

    @Nested
    @DisplayName("Pruebas de Configuración de Menús")
    class ConfiguracionMenusTests {

        @Test
        @DisplayName("MEN-01: Configuración exitosa de menú desayuno")
        void testConfigurarMenuDesayuno() {
            // Arrange
            Menu menuDesayuno = new Menu("Desayuno");
            menuDesayuno.setNombre("Desayuno Nutritivo");
            menuDesayuno.setMenuID("DES-001");
            
            // Agregar platillos
            menuDesayuno.agregarPlatillo(new Platillo("Huevos revueltos", 2500.0));
            menuDesayuno.agregarPlatillo(new Platillo("Jugo natural", 1500.0));
            menuDesayuno.agregarPlatillo(new Platillo("Arepa con queso", 1800.0));

            // Act
            servicioMenu.configurarMenu(administrador, menuDesayuno);
            Menu menuObtenido = servicioMenu.obtenerMenu("Desayuno");

            // Assert
            assertNotNull(menuObtenido, "Menú desayuno debe existir");
            assertEquals("Desayuno Nutritivo", menuObtenido.obtNombre(),
                "Nombre debe coincidir");
            assertEquals("DES-001", menuObtenido.obtMenuID(),
                "ID debe coincidir");
            assertEquals(3, menuObtenido.obtPlatillos().size(),
                "Debe tener 3 platillos");
        }

        @Test
        @DisplayName("MEN-02: Configuración exitosa de menú almuerzo")
        void testConfigurarMenuAlmuerzo() {
            // Arrange
            Menu menuAlmuerzo = new Menu("Almuerzo");
            menuAlmuerzo.setNombre("Almuerzo Ejecutivo");
            menuAlmuerzo.setMenuID("ALM-001");
            
            // Agregar platillos
            menuAlmuerzo.agregarPlatillo(new Platillo("Pollo a la plancha", 4500.0));
            menuAlmuerzo.agregarPlatillo(new Platillo("Ensalada mixta", 1200.0));
            menuAlmuerzo.agregarPlatillo(new Platillo("Arroz con coco", 800.0));
            menuAlmuerzo.agregarPlatillo(new Platillo("Jugo de frutas", 1500.0));

            // Act
            servicioMenu.configurarMenu(administrador, menuAlmuerzo);
            Menu menuObtenido = servicioMenu.obtenerMenu("Almuerzo");

            // Assert
            assertNotNull(menuObtenido, "Menú almuerzo debe existir");
            assertEquals("Almuerzo Ejecutivo", menuObtenido.obtNombre(),
                "Nombre debe coincidir");
            assertEquals(4, menuObtenido.obtPlatillos().size(),
                "Debe tener 4 platillos");
        }

        @Test
        @DisplayName("MEN-03: Actualización de menú existente")
        void testActualizarMenuExistente() {
            // Arrange - Configurar menú inicial
            Menu menuInicial = new Menu("Desayuno");
            menuInicial.setNombre("Desayuno Básico");
            menuInicial.agregarPlatillo(new Platillo("Cereal", 1000.0));
            
            servicioMenu.configurarMenu(administrador, menuInicial);

            // Act - Actualizar menú
            Menu menuActualizado = new Menu("Desayuno");
            menuActualizado.setNombre("Desayuno Premium");
            menuActualizado.agregarPlatillo(new Platillo("Huevos benedictine", 3500.0));
            menuActualizado.agregarPlatillo(new Platillo("Café especial", 2000.0));
            
            servicioMenu.configurarMenu(administrador, menuActualizado);
            Menu menuFinal = servicioMenu.obtenerMenu("Desayuno");

            // Assert
            assertEquals("Desayuno Premium", menuFinal.obtNombre(),
                "Nombre debe actualizarse");
            assertEquals(2, menuFinal.obtPlatillos().size(),
                "Platillos deben actualizarse");
            assertEquals("Huevos benedictine", menuFinal.obtPlatillos().get(0).obtNombre(),
                "Primer platillo debe ser el nuevo");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Desayuno", "Almuerzo"})
        @DisplayName("MEN-04: Configuración de menús por tipo")
        void testConfiguracionMenusPorTipo(String tipoMenu) {
            // Arrange
            Menu menu = new Menu(tipoMenu);
            menu.setNombre("Menú " + tipoMenu + " Especial");
            menu.setMenuID(tipoMenu.substring(0, 3).toUpperCase() + "-001");
            
            // Agregar platillos según tipo
            if (tipoMenu.equals("Desayuno")) {
                menu.agregarPlatillo(new Platillo("Pancakes", 3000.0));
                menu.agregarPlatillo(new Platillo("Café", 1500.0));
            } else {
                menu.agregarPlatillo(new Platillo("Sándwich", 4000.0));
                menu.agregarPlatillo(new Platillo("Bebida", 1000.0));
            }

            // Act
            servicioMenu.configurarMenu(administrador, menu);
            Menu menuObtenido = servicioMenu.obtenerMenu(tipoMenu);

            // Assert
            assertNotNull(menuObtenido, "Menú " + tipoMenu + " debe existir");
            assertEquals("Menú " + tipoMenu + " Especial", menuObtenido.obtNombre(),
                "Nombre debe configurarse correctamente");
            assertTrue(menuObtenido.obtPlatillos().size() >= 2,
                "Debe tener al menos 2 platillos");
        }

        @Test
        @DisplayName("MEN-05: Validación de platillos en menú")
        void testValidacionPlatillosMenu() {
            // Arrange
            Menu menu = new Menu("Desayuno");
            
            // Agregar platillos con precios variados
            menu.agregarPlatillo(new Platillo("Económico", 500.0));
            menu.agregarPlatillo(new Platillo("Estándar", 2500.0));
            menu.agregarPlatillo(new Platillo("Premium", 5000.0));

            // Act
            servicioMenu.configurarMenu(administrador, menu);
            Menu menuObtenido = servicioMenu.obtenerMenu("Desayuno");

            // Assert
            List<Platillo> platillos = menuObtenido.obtPlatillos();
            assertEquals(3, platillos.size(), "Debe tener 3 platillos");
            
            // Verificar precios
            assertEquals(500.0, platillos.get(0).obtPrecio(), 0.001, "Precio económico");
            assertEquals(2500.0, platillos.get(1).obtPrecio(), 0.001, "Precio estándar");
            assertEquals(5000.0, platillos.get(2).obtPrecio(), 0.001, "Precio premium");
        }

        @Test
        @DisplayName("MEN-06: Menú sin platillos - configuración válida")
        void testMenuSinPlatillos() {
            // Arrange
            Menu menuVacio = new Menu("Desayuno");
            menuVacio.setNombre("Menú Vacío");
            menuVacio.setMenuID("VAC-001");
            // No se agregan platillos

            // Act
            servicioMenu.configurarMenu(administrador, menuVacio);
            Menu menuObtenido = servicioMenu.obtenerMenu("Desayuno");

            // Assert
            assertNotNull(menuObtenido, "Menú debe existir aunque esté vacío");
            assertEquals("Menú Vacío", menuObtenido.obtNombre(),
                "Nombre debe configurarse");
            assertTrue(menuObtenido.obtPlatillos().isEmpty(),
                "Lista de platillos debe estar vacía");
        }
    }

    @Nested
    @DisplayName("Pruebas Integradas - CCB y Menús")
    class IntegracionCCBMenusTests {

        @Test
        @DisplayName("INT-01: Cálculo CCB y configuración de tarifas en menús")
        void testIntegracionCCBMenus() {
            // Arrange - Calcular CCB
            double ccbCalculado = servicioCosto.calcularRegistrarCCBCompleto(
                12000.0, 8000.0, 200, 0.15
            ); // Resultado: 115.0

            // Arrange - Configurar menús con tarifas basadas en CCB
            Menu menuDesayuno = new Menu("Desayuno");
            menuDesayuno.setNombre("Desayuno con CCB");
            menuDesayuno.agregarPlatillo(new Platillo("Omelette", ccbCalculado * 1.0)); // Estudiante
            menuDesayuno.agregarPlatillo(new Platillo("Omelette Premium", ccbCalculado * 1.2)); // Empleado

            Menu menuAlmuerzo = new Menu("Almuerzo");
            menuAlmuerzo.setNombre("Almuerzo con CCB");
            menuAlmuerzo.agregarPlatillo(new Platillo("Menú estándar", ccbCalculado * 1.0));
            menuAlmuerzo.agregarPlatillo(new Platillo("Menú ejecutivo", ccbCalculado * 1.2));

            // Act
            servicioMenu.configurarMenu(administrador, menuDesayuno);
            servicioMenu.configurarMenu(administrador, menuAlmuerzo);

            // Assert
            Menu desayunoObtenido = servicioMenu.obtenerMenu("Desayuno");
            Menu almuerzoObtenido = servicioMenu.obtenerMenu("Almuerzo");

            // Verificar precios basados en CCB
            assertEquals(115.0, desayunoObtenido.obtPlatillos().get(0).obtPrecio(), 0.001,
                "Precio estudiante debe basarse en CCB");
            assertEquals(138.0, desayunoObtenido.obtPlatillos().get(1).obtPrecio(), 0.001,
                "Precio empleado debe ser CCB * 1.2");
            assertEquals(115.0, almuerzoObtenido.obtPlatillos().get(0).obtPrecio(), 0.001,
                "Precio almuerzo estándar debe basarse en CCB");
        }

        @Test
        @DisplayName("INT-02: Impacto de cambios en CCB sobre tarifas existentes")
        void testImpactoCambiosCCBTarifas() {
            // Arrange - CCB inicial
            servicioCosto.calcularRegistrarCCBCompleto(10000.0, 5000.0, 150, 0.10); // CCB: 110.0
            double ccbInicial = servicioCosto.obtenerCCBActual();

            // Configurar menú con tarifas iniciales
            Menu menu = new Menu("Desayuno");
            menu.setNombre("Desayuno Dinámico");
            menu.agregarPlatillo(new Platillo("Básico", ccbInicial));
            servicioMenu.configurarMenu(administrador, menu);

            // Act - Cambiar CCB (nuevo cálculo)
            servicioCosto.calcularRegistrarCCBCompleto(15000.0, 7000.0, 180, 0.12); // CCB: ~133.33
            double ccbNuevo = servicioCosto.obtenerCCBActual();

            // Assert - Verificar impacto
            assertTrue(ccbNuevo > ccbInicial, "Nuevo CCB debe ser mayor");
            assertEquals(133.33, ccbNuevo, 0.01, "CCB nuevo debe calcularse correctamente");
            
            // Nota: En una implementación real, las tarifas de menús existentes 
            // deberían actualizarse automáticamente cuando cambia el CCB
        }
    }
}
