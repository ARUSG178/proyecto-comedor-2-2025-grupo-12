package com.comedor;

import javax.swing.SwingUtilities;
import com.comedor.vista.InicioSesionUI;

/**
 * Punto de entrada principal de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InicioSesionUI().setVisible(true));
    }
}