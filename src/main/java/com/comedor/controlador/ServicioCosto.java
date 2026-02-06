package com.comedor.controlador;// paquete del controlador

import com.comedor.modelo.RegistroCosto;// importa el modelo de costos
import java.util.ArrayList;// importa arraylist
import java.util.HashMap;// importa hashmap
import java.util.List;// importa lista
import java.util.Map;// importa mapa
import java.util.stream.Collectors;// importa collectors para streams

public class ServicioCosto {// clase para gestionar costos
    private List<RegistroCosto> costos = new ArrayList<>();// lista de costos registrados
    private Map<String, Integer> produccionBandejas = new HashMap<>();// mapa de produccion por periodo

    public void registrarProduccionBandejas(String periodo, int cantidad) {// registra bandejas producidas
        produccionBandejas.put(periodo, cantidad);// guarda la cantidad
        System.out.println("Producción estimada de bandejas para " + periodo + ": " + cantidad);// imprime confirmacion
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
}