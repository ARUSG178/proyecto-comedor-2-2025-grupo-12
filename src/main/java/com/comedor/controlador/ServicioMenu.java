package com.comedor.controlador;

import java.util.List;

import com.comedor.modelo.entidades.Menu;
import com.comedor.modelo.entidades.Platillo;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Administrador;

public class ServicioMenu {
    private final Menu menu = Menu.getInstance();
    private final ServicioCosto servicioCosto = new ServicioCosto();

    // Permite a un administrador subir/actualizar el menú semanal.
    // Solo los usuarios de tipo Administrador pueden ejecutar esta acción.
    public void configurarMenu(Usuario actor, Menu nuevoMenu) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden modificar el menú.");
            return;
        }

        // Copia los atributos del objeto recibido al singleton vigente
        menu.setNombre(nuevoMenu.obtNombre());
        menu.setMenuID(nuevoMenu.obtMenuID());
        menu.setFechaInicio(nuevoMenu.obtFechaInicio());
        menu.setFechaFin(nuevoMenu.obtFechaFin());
        menu.setEstado(nuevoMenu.obtEstado());

        // Reemplaza la lista de platillos
        List<Platillo> platillosNuevos = nuevoMenu.obtPlatillos();
        menu.obtPlatillos().clear();
        if (platillosNuevos != null) {
            for (Platillo p : platillosNuevos) {
                menu.agregarPlatillo(p);
            }
        }

        System.out.println("Menú actualizado por administrador (Cédula): " + actor.obtCedula());
    }

    // Añadir un platillo al menú (solo Administrador)
    public void agregarPlatillo(Usuario actor, Platillo p) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden agregar platillos.");
            return;
        }
        if (p == null) {
            System.out.println("Platillo nulo.");
            return;
        }
        menu.agregarPlatillo(p);
        System.out.println("Platillo agregado: " + p.obtNombre());
    }

    // Quitar un platillo por nombre (solo Administrador)
    public void quitarPlatillo(Usuario actor, String nombrePlatillo) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden quitar platillos.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            System.out.println("Nombre de platillo inválido.");
            return;
        }
        boolean removed = menu.obtPlatillos().removeIf(p -> nombrePlatillo.equalsIgnoreCase(p.obtNombre()));
        if (removed) {
            System.out.println("Platillo eliminado: " + nombrePlatillo);
        } else {
            System.out.println("No se encontró el platillo: " + nombrePlatillo);
        }
    }

    // Actualizar precio de un platillo (solo Administrador)
    // Nota: el cálculo de costos corresponde a `ServicioCosto`; aquí solo actualizamos el precio mostrado.
    public void actualizarPrecioPlatillo(Usuario actor, String nombrePlatillo, double nuevoPrecio) {
        if (!(actor instanceof Administrador)) {
            System.out.println("Acceso denegado: solo administradores pueden actualizar precios.");
            return;
        }
        if (nombrePlatillo == null || nombrePlatillo.isEmpty()) {
            System.out.println("Nombre de platillo inválido.");
            return;
        }
        for (Platillo p : menu.obtPlatillos()) {
            if (nombrePlatillo.equalsIgnoreCase(p.obtNombre())) {
                double anterior = p.obtPrecio();
                // Registrar el cambio de precio en el servicio de costos
                servicioCosto.registrarCambioPrecio(p.obtNombre(), anterior, nuevoPrecio, actor.obtCedula());
                // Actualizar el precio mostrado en el platillo
                p.setPrecio(nuevoPrecio);
                System.out.println("Precio actualizado para " + p.obtNombre() + ": " + nuevoPrecio);
                return;
            }
        }
        System.out.println("No se encontró el platillo: " + nombrePlatillo);
    }

    // Visualización del menú: disponible tanto para usuarios como administradores
    public void visualizarMenu(Usuario actor) {
        if (menu.obtPlatillos() == null || menu.obtPlatillos().isEmpty()) {
            System.out.println("No hay menú configurado.");
            return;
        }

        System.out.println("Menú: " + (menu.obtNombre() != null ? menu.obtNombre() : "(sin nombre)"));
        System.out.println("Periodo: " + menu.obtFechaInicio() + " - " + menu.obtFechaFin());
        System.out.println("Platillos:");
        for (Platillo p : menu.obtPlatillos()) {
            System.out.println(" - " + p);
        }
    }

    // Devuelve el menú actual (útil para interfaces o pruebas)
    public Menu obtenerMenu() { return menu; }
}