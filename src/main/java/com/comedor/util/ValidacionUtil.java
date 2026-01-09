package com.comedor.util;

public class ValidacionUtil {

    // Validar formato de correo institucional UCV
    public static boolean formatoCorreo(String email) {
        return email != null && email.endsWith("ucv.ve");
    }

    // Validar formato de cédula
    public static boolean formatoCedula(String cedula) {
        return cedula != null && cedula.matches("\\d{6,10}");
    }

    // Validar formato de contraseña
    public static boolean formatoContraseña(String contraseña) {
        return contraseña != null && contraseña.length() >= 6;
    }
}
