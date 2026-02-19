package com.comedor.controlador;// paquete del controlador

import java.util.ArrayList;// importa arraylist
import java.util.HashMap;// importa hashmap
import java.util.List;// importa lista
import java.util.Map;// importa mapa
import java.util.stream.Collectors;// importa collectors para streams
import java.time.LocalDate; // importa LocalDate

import com.comedor.modelo.entidades.RegistroCosto;
import com.comedor.modelo.entidades.Costos;

public class ServicioCosto {// clase para gestionar costos
    private static List<RegistroCosto> costos = new ArrayList<>();// STATIC: lista de costos registrados
    private static Map<String, Integer> produccionBandejas = new HashMap<>();// STATIC: mapa de produccion por periodo
    private static Map<String, Costos> costosMensuales = new HashMap<>(); // STATIC: almacena los costos agregados por el admin

    public void registrarProduccionBandejas(String periodo, int cantidad) {// registra bandejas producidas
        produccionBandejas.put(periodo, cantidad);// guarda la cantidad
        System.out.println("Producción estimada de bandejas para " + periodo + ": " + cantidad);// imprime confirmacion
    }

    // Método para que el Admin suba los valores de la fórmula directamente
    public void registrarValoresCCB(String periodo, double fijos, double variables, int produccion) {
        Costos c = new Costos(periodo, fijos, variables);
        costosMensuales.put(periodo, c);
        registrarProduccionBandejas(periodo, produccion);
        System.out.println("Valores CCB registrados para " + periodo + ": CF=" + fijos + ", CV=" + variables + ", Prod=" + produccion);
    }

    public void agregarCosto(String periodo, RegistroCosto.TipoCosto tipo, String descripcion, double monto) {// agrega un nuevo costo
        costos.add(new RegistroCosto(periodo, tipo, descripcion, monto));// crea y anade el costo
        System.out.println("Costo agregado: " + descripcion + " (" + tipo + ")");// imprime confirmacion
    }

    public List<RegistroCosto> obtenerCostosPorPeriodo(String periodo) {// obtiene costos de un periodo
        return costos.stream()// inicia stream de costos
                .filter(c -> c.getPeriodo().equals(periodo))// filtra por periodo
                .collect(Collectors.toList());// devuelve como lista
    }
    
    public double obtenerTotalCostosPorPeriodo(String periodo) {// calcula total de costos
        // Si el admin subió los valores agregados (Costos.java), usamos esos
        if (costosMensuales.containsKey(periodo)) {
            return costosMensuales.get(periodo).calcularTotal();
        }
        // Si no, sumamos los costos individuales (RegistroCosto)
        return obtenerCostosPorPeriodo(periodo).stream()// stream de costos filtrados
                .mapToDouble(RegistroCosto::getMonto)// obtiene los montos
                .sum();// suma los montos
    }

    public double calcularCostoUnitarioBandeja(String periodo) {// calcula costo por bandeja
        double totalCostos = obtenerTotalCostosPorPeriodo(periodo);// obtiene costo total
        int cantidad = produccionBandejas.getOrDefault(periodo, 0);// obtiene cantidad producida
        if (cantidad <= 0) {// verifica si es cero o menos
            return 0.0;// retorna cero para evitar error
        }
        return totalCostos / cantidad;// calcula costo unitario
    }

    // Obtiene el periodo actual (Ej: "2025-05")
    public String obtenerPeriodoActual() {
        return LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());
    }

    // Obtiene el CCB del mes en curso
    public double obtenerCCBActual() {
        return calcularCostoUnitarioBandeja(obtenerPeriodoActual());
    }

    // Registra un cambio de precio de un platillo como un costo variable.
    // Se guarda el nuevo precio y se incluye una descripción con el platillo, actor y cambio.
    public void registrarCambioPrecio(String nombrePlatillo, double precioAnterior, double precioNuevo, String actor) {
        String periodo = obtenerPeriodoActual();
        double diferencia = precioNuevo - precioAnterior;
        String descripcion = String.format("Cambio precio %s por %s: %.2f -> %.2f (diff %.2f)", nombrePlatillo, actor, precioAnterior, precioNuevo, diferencia);
        agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, descripcion, precioNuevo);
        System.out.println("Registro de cambio de precio creado: " + descripcion);
    }
}