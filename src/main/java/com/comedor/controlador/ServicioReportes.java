package com.comedor.controlador;

import com.comedor.modelo.entidades.*;
import com.comedor.modelo.persistencia.RepoReservas;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.util.Logger;
import java.time.LocalDate;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Servicio para generar reportes de comensales y asistencia basado en datos reales
public class ServicioReportes {
    
    // Obtiene el listado de comensales que acudieron a un servicio específico
    public ReporteComensales obtenerComensalesPorServicio(String tipoServicio, LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        // Obtener datos reales de reservas del día
        List<AsistenciaComedor> asistencias = obtenerAsistenciasReales(tipoServicio, fecha);
        
        return new ReporteComensales(asistencias, tipoServicio, fecha);
    }
    
    // Obtiene asistencias reales basadas en reservas completadas/pagadas del día
    private List<AsistenciaComedor> obtenerAsistenciasReales(String tipoServicio, LocalDate fecha) {
        List<AsistenciaComedor> asistencias = new ArrayList<>();
        
        try {
            // Obtener todas las reservas
            List<Reserva> todasReservas = RepoReservas.obtenerTodasLasReservas();
            
            // Filtrar reservas del día especificado y tipo de servicio
            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.atTime(23, 59, 59);
            
            List<Reserva> reservasDelDia = todasReservas.stream()
                .filter(r -> {
                    LocalDateTime horario = r.obtHorarioReservado();
                    // Verificar si está dentro del rango de fecha
                    boolean enRango = !horario.isBefore(inicioDia) && !horario.isAfter(finDia);
                    
                    // Determinar si es desayuno o almuerzo según la hora
                    int hora = horario.getHour();
                    boolean esDesayuno = hora >= 6 && hora < 11;
                    boolean esAlmuerzo = hora >= 11 && hora < 15;
                    
                    boolean tipoCoincide = (tipoServicio.equalsIgnoreCase("Desayuno") && esDesayuno) ||
                                          (tipoServicio.equalsIgnoreCase("Almuerzo") && esAlmuerzo);
                    
                    return enRango && tipoCoincide && r.obtEstado().equalsIgnoreCase("Completado");
                })
                .collect(Collectors.toList());
            
            // Convertir reservas a asistencias con monto real pagado (desde la reserva)
            for (Reserva reserva : reservasDelDia) {
                Usuario usuario = reserva.obtPropietario();
                LocalDateTime fechaHora = reserva.obtHorarioReservado();
                String cedula = usuario.obtCedula();
                
                // Leer monto directamente desde la reserva
                double montoReal = reserva.obtMontoPagado();
                
                // Si no hay monto en la reserva, calcular basado en CCB (fallback)
                if (montoReal == 0.0) {
                    double ccb = obtenerCCBActual();
                    montoReal = calcularTarifaPorUsuario(usuario, ccb);
                    Logger.info("No hay monto en reserva para " + cedula + ", usando cálculo: $" + montoReal);
                } else {
                    Logger.info("Monto desde reserva para " + cedula + ": $" + montoReal);
                }
                
                asistencias.add(new AsistenciaComedor(usuario, tipoServicio, fechaHora, montoReal));
            }
            
            Logger.info(String.format("Reporte generado: %d comensales para %s del %s", 
                asistencias.size(), tipoServicio, fecha));
                
        } catch (Exception e) {
            Logger.error("Error obteniendo asistencias reales: " + e.getMessage());
        }
        
        return asistencias;
    }
    
    // Obtiene el CCB actual del sistema
    private double obtenerCCBActual() {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("menu_config.properties")) {
                props.load(in);
            }
            return Double.parseDouble(props.getProperty("ccb_actual", "0.0"));
        } catch (Exception e) {
            return 0.0; // Valor por defecto si no hay CCB calculado
        }
    }
    
    // Calcula la tarifa según tipo de usuario
    private double calcularTarifaPorUsuario(Usuario usuario, double ccb) {
        String tipoUsuario = usuario.obtTipo();
        
        switch (tipoUsuario) {
            case "EstudianteExonerado":
                return 0.0;
            case "EstudianteBecario":
                // Para becarios, necesitamos obtener el porcentaje de descuento
                double porcentajeDescuento = 90.0; // Valor por defecto
                try {
                    // Buscar el porcentaje real desde usuarios.txt
                    RepoUsuarios repoUsuarios = new RepoUsuarios();
                    List<Usuario> usuarios = repoUsuarios.listarUsuarios();
                    for (Usuario u : usuarios) {
                        if (u.obtCedula().equals(usuario.obtCedula())) {
                            if (u instanceof EstudianteBecario) {
                                porcentajeDescuento = ((EstudianteBecario) u).obtPorcentajeDescuento();
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Error silenciado, usar valor por defecto
                }
                return ccb * ((100.0 - porcentajeDescuento) / 100.0);
            case "Estudiante":
                return ccb * 0.20;
            case "Profesor":
                return ccb * 0.80;
            case "Empleado":
                return ccb * 0.50;
            case "Administrador":
                return ccb * 0.50; // Se ajusta para igualar la tarifa de Empleado
            default:
                return ccb; // Valor por defecto
        }
    }
    
    // Clase interna para representar el reporte de comensales
    public static class ReporteComensales {
        private final List<AsistenciaComedor> asistencias;
        private final String tipoServicio;
        private final LocalDate fecha;
        
        public ReporteComensales(List<AsistenciaComedor> asistencias, String tipoServicio, LocalDate fecha) {
            this.asistencias = asistencias;
            this.tipoServicio = tipoServicio;
            this.fecha = fecha;
        }
        
        public int getTotalComensales() {
            return asistencias.size();
        }
        
        public double getTotalRecaudado() {
            return asistencias.stream().mapToDouble(AsistenciaComedor::obtMontoPagado).sum();
        }
        
        // Desglose por tipo de comensal
        public long getEstudiantesRegulares() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("Estudiante"))
                .count();
        }
        
        public long getEstudiantesBecarios() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("EstudianteBecario"))
                .count();
        }
        
        public long getEstudiantesExonerados() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("EstudianteExonerado"))
                .count();
        }
        
        public long getProfesores() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("Profesor"))
                .count();
        }
        
        public long getEmpleados() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("Empleado"))
                .count();
        }
        
        public long getAdministradores() {
            return asistencias.stream()
                .filter(a -> a.obtTipoComensal().equals("Administrador"))
                .count();
        }
        
        // Genera reporte detallado en formato texto
        public String generarReporteDetallado() {
            StringBuilder reporte = new StringBuilder();
            reporte.append("=".repeat(80)).append("\n");
            reporte.append("REPORTE DE COMENSALES - ").append(tipoServicio.toUpperCase()).append("\n");
            reporte.append("Fecha: ").append(fecha).append("\n");
            reporte.append("=".repeat(80)).append("\n\n");
            
            // Resumen general
            reporte.append("RESUMEN GENERAL:\n");
            reporte.append(String.format("Total Comensales: %d\n", getTotalComensales()));
            reporte.append(String.format("Total Recaudado: $%.2f\n\n", getTotalRecaudado()));
            
            // Desglose por tipo
            reporte.append("DESGLOSE POR TIPO DE COMENSAL:\n");
            reporte.append(String.format("Estudiantes Regulares: %d\n", getEstudiantesRegulares()));
            reporte.append(String.format("Estudiantes Becarios: %d\n", getEstudiantesBecarios()));
            reporte.append(String.format("Estudiantes Exonerados: %d\n", getEstudiantesExonerados()));
            reporte.append(String.format("Profesores: %d\n", getProfesores()));
            reporte.append(String.format("Empleados: %d\n", getEmpleados()));
            reporte.append(String.format("Administradores: %d\n\n", getAdministradores()));
            
            // Detalle individual
            reporte.append("DETALLE INDIVIDUAL:\n");
            reporte.append("-".repeat(80)).append("\n");
            reporte.append(String.format("%-20s %-12s %-12s %-10s %-15s\n", 
                "Nombre", "Cédula", "Tipo", "Monto", "Hora"));
            reporte.append("-".repeat(80)).append("\n");
            
            for (AsistenciaComedor asistencia : asistencias) {
                reporte.append(String.format("%-20s %-12s %-12s $%-9.2f %-15s\n",
                    asistencia.obtComensal().obtNombre(),
                    asistencia.obtComensal().obtCedula(),
                    asistencia.obtTipoComensal(),
                    asistencia.obtMontoPagado(),
                    asistencia.obtFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                ));
            }
            
            // --- TOTALES DEL DÍA ---
            reporte.append("\n");
            reporte.append("TOTALES DEL DÍA:\n");
            reporte.append("-".repeat(80)).append("\n");
            
            // Calcular totales del día (desayuno + almuerzo)
            double porcentajeConcesionario = 0.275; // 27.5%
            double gananciaConcesionario = getTotalRecaudado() * porcentajeConcesionario;
            double ingresosPropios = getTotalRecaudado() * (1 - porcentajeConcesionario);
            
            reporte.append(String.format("Total General del Día: %d comensales | $%.2f\n", 
                getTotalComensales(), getTotalRecaudado()));
            reporte.append(String.format("Ganancia Concesionario (27.5%%): $%.2f\n", gananciaConcesionario));
            reporte.append(String.format("Ingresos Propios (72.5%%): $%.2f\n", ingresosPropios));
            
            reporte.append("\n").append("=".repeat(80));
            
            // Log del reporte
            Logger.info("Reporte de comensales generado: " + tipoServicio + " - " + fecha);
            Logger.info(String.format("Total: %d comensales, Recaudado: $%.2f", 
                getTotalComensales(), getTotalRecaudado()));
            
            return reporte.toString();
        }
    }
}
