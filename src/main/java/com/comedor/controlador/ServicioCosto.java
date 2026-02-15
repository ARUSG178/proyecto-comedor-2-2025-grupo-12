package com.comedor.controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.comedor.modelo.entidades.RegistroCosto;
import com.comedor.util.ServicioUtil;

public class ServicioCosto {
    private List<RegistroCosto> costos = new ArrayList<>();
    private Map<String, Integer> produccionBandejas = new HashMap<>();

    // Registra la cantidad estimada de bandejas a producir en un periodo específico
    public void registrarProduccionBandejas(String periodo, int cantidad) {
        produccionBandejas.put(periodo, cantidad);
        System.out.println("Producción estimada de bandejas para " + periodo + ": " + cantidad);
    }

    // Agrega un nuevo registro de costo al sistema con su tipo y descripción
    public void agregarCosto(String periodo, RegistroCosto.TipoCosto tipo, String descripcion, double monto) {
        costos.add(new RegistroCosto(periodo, tipo, descripcion, monto));
        System.out.println("Costo agregado: " + descripcion + " (" + tipo + ")");
    }

    // Obtiene una lista de costos filtrados por el periodo especificado
    public List<RegistroCosto> obtenerCostosPorPeriodo(String periodo) {
        return costos.stream().filter(c -> c.obtPeriodo().equals(periodo)).collect(Collectors.toList());
    }
    
    // Calcula la suma total de los montos de costos para un periodo dado
    public double obtenerTotalCostosPorPeriodo(String periodo) {
        return obtenerCostosPorPeriodo(periodo).stream().mapToDouble(RegistroCosto::obtMonto).sum();
    }

    // Calcula el costo unitario por bandeja dividiendo el total de costos entre la producción
    public double calcularCostoUnitarioBandeja(String periodo) {
        double totalCostos = obtenerTotalCostosPorPeriodo(periodo);
        int cantidad = produccionBandejas.getOrDefault(periodo, 0);
        if (cantidad <= 0) {
            return 0.0;
        }
        return totalCostos / cantidad;
    }

    // Registra un cambio de precio de platillo como costo variable en el periodo actual
    public void registrarCambioPrecio(String nombrePlatillo, double precioAnterior, double precioNuevo, String actor) {
        String periodo = ServicioUtil.obtenerPeriodoActual();
        double diferencia = precioNuevo - precioAnterior;
        String descripcion = String.format("Cambio precio %s por %s: %.2f -> %.2f (diff %.2f)", nombrePlatillo, actor, precioAnterior, precioNuevo, diferencia);
        agregarCosto(periodo, RegistroCosto.TipoCosto.VARIABLE, descripcion, precioNuevo);
        System.out.println("Registro de cambio de precio creado: " + descripcion);
    }
}