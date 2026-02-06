package com.comedor.controlador;// paquete del controlador

import com.comedor.modelo.MenuDiario;// importa la clase menu diario
import java.time.LocalDate;// importa clase para fechas
import java.util.HashMap;// importa hashmap
import java.util.List;// importa lista
import java.util.Map;// importa mapa

public class ServicioMenu {// clase para gestionar los menus
    private Map<LocalDate, MenuDiario> menus = new HashMap<>();// mapa para guardar menus por fecha

    public void configurarMenu(LocalDate fecha, List<String> platos, double precio) {// metodo para crear un menu
        MenuDiario menu = new MenuDiario(fecha, platos, precio);// crea el objeto menu
        menus.put(fecha, menu);// lo guarda en el mapa
        System.out.println("Menú configurado para la fecha: " + fecha);// confirma la accion
    }

    public void visualizarMenu(LocalDate fecha) {// metodo para ver un menu
        MenuDiario menu = menus.get(fecha);// busca el menu por fecha
        if (menu != null) {// si existe el menu
            System.out.println(menu);// lo imprime
        } else {// si no existe
            System.out.println("No hay menú configurado para el " + fecha);// avisa que no hay menu
        }
    }
}