package com.comedor.modelo.persistencia;

import com.comedor.util.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Repositorio para persistir ingresos del sistema (ganancia concesionario e ingresos propios)
public class RepoIngresos {
    
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/ingresos.txt";
    
    // Registra un ingreso del sistema con distribución
    public static void registrarIngreso(double montoTotal, double gananciaConcesionario, 
                                       double ingresosPropios, String tipoUsuario, 
                                       String tipoServicio, String cedula) {
        try {
            // Corregir formato: usar LocalDate.now() en lugar de ServicioUtil.obtenerPeriodoActual()
            String fechaHora = java.time.LocalDate.now() + " " + 
                              java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            String linea = String.format("%s;%.2f;%.2f;%.2f;%s;%s;%s",
                fechaHora,
                montoTotal,
                gananciaConcesionario,
                ingresosPropios,
                tipoUsuario,
                tipoServicio,
                cedula
            );
            
            // Crear archivo si no existe
            if (!Files.exists(Paths.get(RUTA_ARCHIVO))) {
                Files.createDirectories(Paths.get(RUTA_ARCHIVO).getParent());
                Files.write(Paths.get(RUTA_ARCHIVO), 
                    "fecha_hora;monto_total;ganancia_concesionario;ingresos_propios;tipo_usuario;tipo_servicio;cedula\n".getBytes());
            }
            
            // Agregar línea al archivo
            List<String> lineas = new ArrayList<>();
            lineas.add(linea);
            Files.write(Paths.get(RUTA_ARCHIVO), lineas, 
                java.nio.file.StandardOpenOption.APPEND, 
                java.nio.file.StandardOpenOption.CREATE);
            
            Logger.info("Ingreso registrado: Total=$" + montoTotal + 
                       " | Concesionario=$" + gananciaConcesionario + 
                       " | Propios=$" + ingresosPropios);
                       
        } catch (IOException e) {
            Logger.error("Error registrando ingreso: " + e.getMessage());
        }
    }
    
    // Obtiene todos los ingresos registrados
    public static List<RegistroIngreso> obtenerTodosLosIngresos() {
        List<RegistroIngreso> ingresos = new ArrayList<>();
        
        try {
            if (!Files.exists(Paths.get(RUTA_ARCHIVO))) {
                return ingresos;
            }
            
            List<String> lineas = Files.readAllLines(Paths.get(RUTA_ARCHIVO));
            // Saltar header
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (!linea.isEmpty()) {
                    RegistroIngreso ingreso = deserializarIngreso(linea);
                    if (ingreso != null) {
                        ingresos.add(ingreso);
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Error leyendo ingresos: " + e.getMessage());
        }
        
        return ingresos;
    }
    
    // Obtiene ingresos de una fecha específica
    public static List<RegistroIngreso> obtenerIngresosPorFecha(LocalDate fecha) {
        List<RegistroIngreso> todos = obtenerTodosLosIngresos();
        List<RegistroIngreso> filtrados = new ArrayList<>();
        
        for (RegistroIngreso ingreso : todos) {
            if (ingreso.getFecha().equals(fecha)) {
                filtrados.add(ingreso);
            }
        }
        
        return filtrados;
    }
    
    // Calcula totales por período
    public static ResumenFinanciero calcularResumenPeriodo(String periodo) {
        List<RegistroIngreso> ingresos = obtenerTodosLosIngresos();
        
        double totalMonto = 0;
        double totalConcesionario = 0;
        double totalPropios = 0;
        
        for (RegistroIngreso ingreso : ingresos) {
            if (ingreso.getPeriodo().equals(periodo)) {
                totalMonto += ingreso.getMontoTotal();
                totalConcesionario += ingreso.getGananciaConcesionario();
                totalPropios += ingreso.getIngresosPropios();
            }
        }
        
        return new ResumenFinanciero(totalMonto, totalConcesionario, totalPropios);
    }
    
    private static RegistroIngreso deserializarIngreso(String linea) {
        try {
            String[] partes = linea.split(";");
            if (partes.length >= 7) {
                String fechaHora = partes[0];
                double montoTotal = Double.parseDouble(partes[1]);
                double gananciaConcesionario = Double.parseDouble(partes[2]);
                double ingresosPropios = Double.parseDouble(partes[3]);
                String tipoUsuario = partes[4];
                String tipoServicio = partes[5];
                String cedula = partes[6];
                
                return new RegistroIngreso(fechaHora, montoTotal, gananciaConcesionario, 
                                          ingresosPropios, tipoUsuario, tipoServicio, cedula);
            }
        } catch (Exception e) {
            Logger.error("Error deserializando ingreso: " + linea);
        }
        return null;
    }
    
    // Clase interna para representar un registro de ingreso
    public static class RegistroIngreso {
        private final String fechaHora;
        private final double montoTotal;
        private final double gananciaConcesionario;
        private final double ingresosPropios;
        private final String tipoUsuario;
        private final String tipoServicio;
        private final String cedula;
        
        public RegistroIngreso(String fechaHora, double montoTotal, double gananciaConcesionario,
                              double ingresosPropios, String tipoUsuario, String tipoServicio, String cedula) {
            this.fechaHora = fechaHora;
            this.montoTotal = montoTotal;
            this.gananciaConcesionario = gananciaConcesionario;
            this.ingresosPropios = ingresosPropios;
            this.tipoUsuario = tipoUsuario;
            this.tipoServicio = tipoServicio;
            this.cedula = cedula;
        }
        
        public String getFechaHora() { return fechaHora; }
        public double getMontoTotal() { return montoTotal; }
        public double getGananciaConcesionario() { return gananciaConcesionario; }
        public double getIngresosPropios() { return ingresosPropios; }
        public String getTipoUsuario() { return tipoUsuario; }
        public String getTipoServicio() { return tipoServicio; }
        public String getCedula() { return cedula; }
        
        public LocalDate getFecha() {
            return LocalDate.parse(fechaHora.substring(0, 10));
        }
        
        public LocalDateTime getFechaHoraCompleta() {
            try {
                return LocalDateTime.parse(fechaHora.replace(" ", "T"));
            } catch (Exception e) {
                return null;
            }
        }
        
        public String getPeriodo() {
            return fechaHora.substring(0, 7); // yyyy-MM
        }
    }
    
    // Clase para resumen financiero
    public static class ResumenFinanciero {
        private final double totalMonto;
        private final double totalConcesionario;
        private final double totalPropios;
        
        public ResumenFinanciero(double totalMonto, double totalConcesionario, double totalPropios) {
            this.totalMonto = totalMonto;
            this.totalConcesionario = totalConcesionario;
            this.totalPropios = totalPropios;
        }
        
        public double getTotalMonto() { return totalMonto; }
        public double getTotalConcesionario() { return totalConcesionario; }
        public double getTotalPropios() { return totalPropios; }
    }
}
