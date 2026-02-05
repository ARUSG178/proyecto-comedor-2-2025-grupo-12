package com.comedor.modelo.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//clase que representa el menú diario del comedor
public class Menu {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Platillo> platillos;

    public Menu(LocalDate fechaInicio, LocalDate fechaFin) {// Constructor de la clase Menu
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.platillos = new ArrayList<>();
    }

    public void agregarPlatillo(Platillo p) {// Metodo para agregar un platillo al menu
        platillos.add(p);// Agrega el platillo a la lista de platillos
    }

    public LocalDate getFechaInicio() { return fechaInicio; }// Getter para la fecha del menu
    public LocalDate getFechaFin() { return fechaFin; }
    public List<Platillo> getPlatillos() { return platillos; }// Getter para la lista de platillos del menu
}
