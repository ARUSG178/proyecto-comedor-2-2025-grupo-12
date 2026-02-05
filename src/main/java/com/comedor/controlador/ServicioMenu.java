package com.comedor.controlador;

import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;

import java.time.LocalDate;
import java.util.List;

/**
 * Esta clase de servicio se encarga de toda la lógica de negocio
 * relacionada con la gestión del menú.
 */
public class ServicioMenu {

    /**
     * Actualiza los datos del menú semanal.
     * Este método será llamado desde la interfaz de usuario del administrador.
     * @param nombre El nuevo nombre para el menú (ej. "Menú Semana 3")
     * @param fechaInicio La fecha en que el menú se activa.
     * @param fechaFin La fecha en que el menú termina.
     * @param platillos La lista de platillos para ese menú.
     */
    public void actualizarMenuSemanal(String nombre, LocalDate fechaInicio, LocalDate fechaFin, List<Platillo> platillos) {
        // 1. Obtener la instancia única del menú.
        Menu menu = Menu.getInstance();

        // 2. Usar los setters para actualizar toda su información.
        menu.setNombre(nombre);
        menu.setFechaInicio(fechaInicio);
        menu.setFechaFin(fechaFin);
        menu.setEstado(true); // Se asume que al actualizarlo, se quiere activar.

        // 3. Limpiar la lista de platillos anterior y agregar los nuevos.
        menu.getPlatillos().clear();
        for (Platillo platillo : platillos) {
            menu.agregarPlatillo(platillo);
        }

        System.out.println("Menú actualizado correctamente: " + menu.getNombre());
        // Aquí podrías agregar lógica para guardar los cambios en un archivo si fuera necesario.
    }

    /**
     * Obtiene el menú actual para que otras partes de la aplicación lo muestren.
     * @return La instancia única y ya configurada del menú.
     */
    public Menu getMenuActual() {
        return Menu.getInstance();
    }
}
