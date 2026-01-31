package com.comedor.modelos;

import java.util.ArrayList;
import java.util.List;
//clase que representa el menú diario del comedor
public class Menu {
    private Fecha fecha;
    private List<Platillo> platillos;

    public Menu(Fecha fecha) {// Constructor de la clase Menu
        this.fecha = fecha;
        this.platillos = new ArrayList<>();
    }

    public void agregarPlatillo(Platillo p) {// Metodo para agregar un platillo al menu
        platillos.add(p);// Agrega el platillo a la lista de platillos
    }

    public Fecha getFecha() { return fecha; }// Getter para la fecha del menu
    public List<Platillo> getPlatillos() { return platillos; }// Getter para la lista de platillos del menu
}
