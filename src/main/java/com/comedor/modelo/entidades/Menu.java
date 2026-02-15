package com.comedor.modelo.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//clase que representa el menú diario del comedor
public class Menu {
    private static Menu instance;
    private String nombre;
    private String menuID;
    private boolean estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Platillo> platillos;

    // Constructor privado para implementar Singleton
    private Menu() {
        this.platillos = new ArrayList<>();
    }

    // Método estático para obtener la instancia única de Menu
    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void agregarPlatillo(Platillo p) {// Metodo para agregar un platillo al menu
        platillos.add(p);// Agrega el platillo a la lista de platillos
    }

    public LocalDate obtFechaInicio() { return fechaInicio; }// Getter para la fecha del menu
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate obtFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public List<Platillo> obtPlatillos() { return platillos; }// Getter para la lista de platillos del menu

    public String obtMenuID() { return menuID; }
    public void setMenuID(String menuID) { this.menuID = menuID; }

    public String obtNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean obtEstado() {return estado;}
    public void setEstado(boolean estado) { this.estado = estado; }
}
