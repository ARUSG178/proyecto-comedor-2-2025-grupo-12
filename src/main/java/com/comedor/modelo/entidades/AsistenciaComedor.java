package com.comedor.modelo.entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa el registro de asistencia de un comensal a un servicio específico
 */
public class AsistenciaComedor {
    private Usuario comensal;
    private String tipoServicio; // "Desayuno" o "Almuerzo"
    private LocalDateTime fechaHora;
    private double montoPagado;
    
    public AsistenciaComedor(Usuario comensal, String tipoServicio, LocalDateTime fechaHora, double montoPagado) {
        this.comensal = comensal;
        this.tipoServicio = tipoServicio;
        this.fechaHora = fechaHora;
        this.montoPagado = montoPagado;
    }
    
    // Getters
    public Usuario obtComensal() { return comensal; }
    public String obtTipoServicio() { return tipoServicio; }
    public LocalDateTime obtFechaHora() { return fechaHora; }
    public double obtMontoPagado() { return montoPagado; }
    
    public String obtTipoComensal() {
        return comensal.obtTipo();
    }
    
    // Para reportes
    public String obtenerResumen() {
        return String.format("%s | %s | %s | $%.2f | %s", 
            comensal.obtNombre(),
            comensal.obtCedula(),
            tipoServicio,
            montoPagado,
            fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }
}
