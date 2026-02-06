package com.comedor.vista;//paquete que contiene la clase principal de la vista

import com.comedor.modelo.RegistroCosto;//importa la clase modelo para el registro de costos
import com.comedor.controlador.AuthService;//importa el servicio de autenticacion
import com.comedor.controlador.ServicioCosto;//importa el servicio de gestion de costos
import com.comedor.controlador.ServicioMenu;//importa el servicio de gestion de menus
import java.time.LocalDate;//importa la clase para manejar fechas
import java.util.Arrays;//importa la clase utilitaria para manejar arreglos y listas

public class Main {//clase principal que ejecuta la simulacion del sistema
    public static void main(String[] args) {//metodo main punto de entrada de la aplicacion
        AuthService servicioAutenticacion = new AuthService();//instancia el servicio de autenticacion
        ServicioCosto servicioCosto = new ServicioCosto();//instancia el servicio de costos
        ServicioMenu servicioMenu = new ServicioMenu();//instancia el servicio de menus

        System.out.println("=== SISTEMA DE COMEDOR - MÓDULO ADMINISTRADOR ===\n");//imprime el titulo del modulo en consola

        servicioAutenticacion.register("admin_comedor", "securePass123", "ADMIN");//registra un usuario administrador por defecto
        
        if (servicioAutenticacion.login("admin_comedor", "securePass123")) {//intenta iniciar sesion con las credenciales registradas
            
            System.out.println("\n--- Gestión de Costos ---");//imprime separador para la gestion de costos
            String periodo = "2025-01";//define la variable para el periodo de facturacion
            servicioCosto.agregarCosto(periodo, RegistroCosto.TipoCosto.FIJO, "Alquiler de Instalaciones", 1200.00);//agrega un costo fijo de alquiler
            servicioCosto.agregarCosto(periodo, RegistroCosto.TipoCosto.FIJO, "Sueldos Personal Cocina", 3500.00);//agrega un costo fijo de sueldos
            servicioCosto.agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, "Compra de Verduras Semanal", 450.50);//agrega un costo variable de verduras
            servicioCosto.agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, "Compra de Carnes", 800.00);//agrega un costo variable de carnes

            servicioCosto.registrarProduccionBandejas(periodo, 2000);//registra la produccion estimada de bandejas para el calculo

            System.out.println("Total de Costos Operativos (" + periodo + "): $" + servicioCosto.obtenerTotalCostosPorPeriodo(periodo));//muestra el total de costos del periodo
            System.out.println("Costo Unitario por Bandeja (Calculado): $" + String.format("%.2f", servicioCosto.calcularCostoUnitarioBandeja(periodo)));//muestra el costo unitario calculado

            System.out.println("\n--- Gestión de Menú ---");//imprime separador para la gestion de menu
            LocalDate hoy = LocalDate.now();//obtiene la fecha actual
            servicioMenu.configurarMenu(hoy, Arrays.asList("Sopa de Lentejas", "Milanesa con Puré", "Manzana"), 6.50);//configura el menu del dia con platos y precio
            
            System.out.println("Visualizando menú del día:");//imprime mensaje informativo
            servicioMenu.visualizarMenu(hoy);//muestra el menu configurado para la fecha actual
        }
    }
}
