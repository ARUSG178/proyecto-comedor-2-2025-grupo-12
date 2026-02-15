package com.comedor.modelo.entidades;
// Clase que representa los costos del comedor en un periodo determinado
public class Costos {
    private String periodo;
    private double costosFijos;
    private double costosVariables;

    public Costos(String periodo, double costosFijos, double costosVariables) {//constructor de la clase Costos
        this.periodo = periodo;
        this.costosFijos = costosFijos;
        this.costosVariables = costosVariables;
    }

    public String obtPeriodo() { return periodo; }/// Getter para el periodo
    public double obtCostosFijos() { return costosFijos; }
    public double obtCostosVariables() { return costosVariables; }
    
    public double calcularTotal() {// metodo para calcular el costo total del periodo
        return costosFijos + costosVariables;
    }
}