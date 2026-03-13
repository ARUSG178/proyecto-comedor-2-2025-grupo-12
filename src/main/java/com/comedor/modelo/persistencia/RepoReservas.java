package com.comedor.modelo.persistencia;

import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.util.ServicioUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepoReservas {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/reservas.txt";
    private static List<Reserva> reservas = new ArrayList<>();
    
    // Cargar reservas desde archivo al iniciar
    static {
        cargarReservasDesdeArchivo();
    }
    
    private static void cargarReservasDesdeArchivo() {
        try {
            List<String> lineas = ServicioUtil.leerLineas(RUTA_ARCHIVO);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Reserva reserva = deserializarReserva(linea);
                    if (reserva != null) {
                        reservas.add(reserva);
                    }
                }
            }
        } catch (Exception e) {
            // Error silenciado cargando reservas
        }
    }
    
    private static void guardarReservasEnArchivo() {
        try {
            // Limpiar archivo
            ServicioUtil.escribirLinea(RUTA_ARCHIVO, "", false);
            
            // Escribir todas las reservas
            boolean primeraLinea = true;
            for (Reserva reserva : reservas) {
                ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializarReserva(reserva), primeraLinea);
                primeraLinea = false;
            }
        } catch (Exception e) {
            // Error silenciado guardando reservas
        }
    }
    
    private static String serializarReserva(Reserva reserva) {
        return String.format("%s;%s;%s;%.2f", 
            reserva.obtPropietario().obtCedula(),
            reserva.obtHorarioReservado().toString(),
            reserva.obtEstado(),
            reserva.obtMontoPagado());
    }
    
    private static Reserva deserializarReserva(String linea) {
        try {
            String[] datos = linea.split(";");
            if (datos.length >= 3) {
                String cedula = datos[0].trim();
                LocalDateTime horario = LocalDateTime.parse(datos[1].trim());
                String estado = datos[2].trim();
                double montoPagado = 0.0;
                if (datos.length >= 4) {
                    try {
                        montoPagado = Double.parseDouble(datos[3].trim());
                    } catch (Exception e) {
                        // Error silenciado parseando monto
                    }
                }
                
                // Buscar usuario real desde el repositorio
                Usuario usuario = null;
                try {
                    RepoUsuarios repoUsuarios = new RepoUsuarios();
                    List<Usuario> usuarios = repoUsuarios.listarUsuarios();
                    for (Usuario u : usuarios) {
                        if (u.obtCedula().equals(cedula)) {
                            usuario = u;
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Error silenciado buscando usuario real
                }
                
                // Si no se encuentra el usuario, crear uno temporal
                if (usuario == null) {
                    usuario = new Usuario(cedula, "") {
                        @Override public double calcularTarifa(double precioBase) { return precioBase; }
                        @Override public String obtTipo() { return "Usuario"; }
                        @Override public boolean obtEstado() { return true; }
                        @Override public int obtIntentosFallidos() { return 0; }
                        @Override public double obtSaldo() { return 0.0; }
                        @Override public void setNombre(String nombre) {}
                        @Override public void setEstado(boolean estado) {}
                        @Override public void setSaldo(double saldo) {}
                        @Override public void setIntentosFallidos(int intentos) {}
                    };
                    usuario.setNombre("Usuario Reserva");
                }
                
                return new Reserva(usuario, horario, estado, montoPagado);
            }
        } catch (Exception e) {
            // Error silenciado deserializando reserva
        }
        return null;
    }
    
    public static void guardarReserva(Reserva reserva) {
        // Agregar a la lista en memoria
        reservas.add(reserva);
        // Agregar al archivo sin truncar (preservar reservas existentes)
        try {
            ServicioUtil.escribirLinea(RUTA_ARCHIVO, serializarReserva(reserva), true);
        } catch (Exception e) {
            System.err.println("Error guardando reserva: " + e.getMessage());
        }
    }
    
    public static List<Reserva> obtenerReservasPorUsuario(Usuario usuario) {
        return reservas.stream()
                .filter(r -> r.obtPropietario().obtCedula().equals(usuario.obtCedula()))
                .collect(Collectors.toList());
    }
    
    public static List<Reserva> obtenerTodasLasReservas() {
        return new ArrayList<>(reservas);
    }
    
    // Verifica si el usuario ya tiene una reserva para el mismo día y turno (desayuno/almuerzo)
    public static boolean existeReservaDelMismoTipo(Usuario usuario, LocalDateTime fechaHora) {
        LocalDate fecha = fechaHora.toLocalDate();
        int hora = fechaHora.getHour();
        
        return reservas.stream()
                .filter(r -> r.obtPropietario().obtCedula().equals(usuario.obtCedula()))
                .filter(r -> r.obtHorarioReservado().toLocalDate().equals(fecha))
                .anyMatch(r -> {
                    int horaExistente = r.obtHorarioReservado().getHour();
                    // Desayuno: 7-11, Almuerzo: 12-16 (4pm)
                    boolean esDesayunoNuevo = hora >= 7 && hora < 12;
                    boolean esAlmuerzoNuevo = hora >= 12 && hora < 17;
                    boolean esDesayunoExistente = horaExistente >= 7 && horaExistente < 12;
                    boolean esAlmuerzoExistente = horaExistente >= 12 && horaExistente < 17;
                    
                    return (esDesayunoNuevo && esDesayunoExistente) || (esAlmuerzoNuevo && esAlmuerzoExistente);
                });
    }
    
    // Elimina una reserva específica de un usuario
    public static boolean eliminarReserva(Usuario usuario, LocalDateTime horarioReservado) {
        boolean eliminada = reservas.removeIf(r -> 
            r.obtPropietario().obtCedula().equals(usuario.obtCedula()) &&
            r.obtHorarioReservado().equals(horarioReservado)
        );
        
        if (eliminada) {
            guardarReservasEnArchivo(); // Persistir cambios
        }
        
        return eliminada;
    }
    
    // Elimina una reserva por su clave de acceso
    public static boolean eliminarReservaPorClave(String claveAcceso) {
        boolean eliminada = reservas.removeIf(r -> r.obtClaveAcceso().equals(claveAcceso));
        
        if (eliminada) {
            guardarReservasEnArchivo(); // Persistir cambios
        }
        
        return eliminada;
    }
}
