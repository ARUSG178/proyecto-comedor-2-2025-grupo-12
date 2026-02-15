package com.comedor.modelo.entidades;

public class RegistroCosto {
    public enum TipoCosto { FIJO, VARIABLE }

    private String periodo; // Formato: YYYY-MM
    private TipoCosto tipo;
    private String descripcion;
    private double monto;

    public RegistroCosto(String periodo, TipoCosto tipo, String descripcion, double monto) {
        this.periodo = periodo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public String obtPeriodo() { return periodo; }
    public TipoCosto obtTipo() { return tipo; }
    public double obtMonto() { return monto; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: $%.2f", periodo, tipo, descripcion, monto);
    }
}