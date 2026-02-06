package com.comedor.modelo;

import java.time.LocalDate;
import java.util.List;

public class MenuDiario {
    private LocalDate fecha;
    private List<String> platos; // Lista de platos
    private double precio;

    public MenuDiario(LocalDate fecha, List<String> platos, double precio) {
        this.fecha = fecha;
        this.platos = platos;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Menú del día " + fecha + ": " + platos + " - Precio: $" + precio;
    }
}