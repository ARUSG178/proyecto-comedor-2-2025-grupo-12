package com.comedor.modelo.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private static Menu instance;
    private String nombre;
    private String menuID;
    private boolean estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Platillo> platillos;

    // Constructor privado para inicializar la lista de platillos (Singleton)
    private Menu() {
        this.platillos = new ArrayList<>();
    }

    // Retorna la instancia única de la clase Menu
    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    // Agrega un nuevo platillo a la lista del menú
    public void agregarPlatillo(Platillo p) {
        platillos.add(p);
    }

    // Retorna la fecha de inicio de vigencia del menú
    public LocalDate obtFechaInicio() { return fechaInicio; }
    
    // Establece la fecha de inicio de vigencia del menú
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    // Retorna la fecha de fin de vigencia del menú
    public LocalDate obtFechaFin() { return fechaFin; }
    
    // Establece la fecha de fin de vigencia del menú
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    // Retorna la lista de platillos configurados en el menú
    public List<Platillo> obtPlatillos() { return platillos; }

    // Retorna el identificador único del menú
    public String obtMenuID() { return menuID; }
    
    // Establece el identificador único del menú
    public void setMenuID(String menuID) { this.menuID = menuID; }

    // Retorna el nombre descriptivo del menú
    public String obtNombre() { return nombre; }
    
    // Establece el nombre descriptivo del menú
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Indica si el menú está activo
    public boolean obtEstado() {return estado;}
    
    // Establece el estado de actividad del menú
    public void setEstado(boolean estado) { this.estado = estado; }
}
