package com.comedor.modelos;

import java.util.UUID;

public class Reserva {
    private Usuario propietario;
    private Fecha horarioReservado;
    private String estado;
    private String clave_acceso;

    public Reserva(Usuario propietario, Fecha horarioReservado, String estado) {
        this.propietario = propietario;
        this.horarioReservado = horarioReservado;
        this.estado = estado;
        this.clave_acceso = generarClaveAcceso();
    }

    String generarClaveAcceso() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public Usuario getPropietario() { return propietario; }
    public Fecha getHorarioReservado() { return horarioReservado; }
    public String getEstado() { return estado; }
    public String getClaveAcceso() { return clave_acceso; }

    @Override
    public String toString() {
        return "Reserva de " + propietario.getNombre() + " en " + horarioReservado +  " [estado ->" + estado + ", clave ->" + clave_acceso + "]";
    }
}