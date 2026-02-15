package com.comedor.modelo.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reserva {
    private Usuario propietario;
    private LocalDateTime horarioReservado;
    private String estado;
    private String clave_acceso;

    public Reserva(Usuario propietario, LocalDateTime horarioReservado, String estado) {
        this.propietario = propietario;
        this.horarioReservado = horarioReservado;
        this.estado = estado;
        this.clave_acceso = genClaveAcceso();
    }

    // Se crea de forma aleatoria a través del import UUID.
    String genClaveAcceso() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public Usuario obtPropietario() { return propietario; }
    public LocalDateTime obtHorarioReservado() { return horarioReservado; }
    public String obtEstado() { return estado; }
    public String obtClaveAcceso() { return clave_acceso; }

    @Override
    public String toString() {
        return "Reserva de " + propietario.obtCedula() + " en " + horarioReservado +  " [estado ->" + estado + ", clave ->" + clave_acceso + "]";
    }
}