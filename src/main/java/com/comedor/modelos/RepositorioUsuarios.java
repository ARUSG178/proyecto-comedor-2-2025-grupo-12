package com.comedor.modelos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuarios {
    private static final String RUTA_ARCHIVO = "usuarios.txt";

    // Método para guardar un usuario al final del archivo (Append)
    public void guardarUsuario(Usuario usuario) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            escritor.write(serializar(usuario));
            escritor.newLine();
        }
    }

    //metodo para guardar toda la lista de usuarios (Sobrescribir archivo) para actualizaciones de saldo
    public void guardarTodos(List<Usuario> usuarios) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Usuario usuario : usuarios) {
                escritor.write(serializar(usuario));
                escritor.newLine();
            }
        }
    }

    //metodo para leer todos los usuarios del archivo
    public List<Usuario> listarUsuarios() throws IOException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);
        
        //si el archivo no existe, retornamos lista vacía
        if (!archivo.exists()) {
            return listaUsuarios;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    Usuario usuario = deserializar(linea);
                    if (usuario != null) {
                        listaUsuarios.add(usuario);
                    }
                }
            }
        }
        return listaUsuarios;
    }

    //convierte un objeto Usuario a String (Formato CSV)
    private String serializar(Usuario usuario) {
        StringBuilder buffer = new StringBuilder();
        //datos comunes
        buffer.append(usuario.getTipo()).append(";");
        buffer.append(usuario.getCedula()).append(";");
        buffer.append(usuario.getNombre()).append(";");
        buffer.append(usuario.getApellido()).append(";");
        buffer.append(usuario.getEmail()).append(";");
        buffer.append(usuario.getContraseña()).append(";");
        buffer.append(usuario.isEstado()).append(";");
        buffer.append(usuario.getIntentosFallidos()).append(";");
        buffer.append(usuario.getSaldo()).append(";");

        //datos específicos según el tipo
        if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            buffer.append(estudiante.getCarrera()).append(";");
            buffer.append(estudiante.getCodigoEstudiante());
        } else if (usuario instanceof Empleado) {
            Empleado empleado = (Empleado) usuario;
            buffer.append(empleado.getCargo()).append(";");
            buffer.append(empleado.getDepartamento()).append(";");
            buffer.append(empleado.getCodigoEmpleado());
        } else if (usuario instanceof Administrador) {
            Administrador administrador = (Administrador) usuario;
            buffer.append(administrador.getResponsabilidad());
        }
        return buffer.toString();
    }

    //convierte un String (CSV) a objeto Usuario
    private Usuario deserializar(String linea) {
        String[] datos = linea.split(";");
        if (datos.length < 9) return null; // Validación básica de estructura

        //variables para guardar los datos del usuario
        String tipo = datos[0];
        String cedula = datos[1];
        String nombre = datos[2];
        String apellido = datos[3];
        String email = datos[4];
        String contraseña = datos[5];
        boolean estado = Boolean.parseBoolean(datos[6]);
        int intentos = Integer.parseInt(datos[7]);
        double saldo = Double.parseDouble(datos[8]);

        Usuario usuario = null;

        //instanciación según el tipo
        if (tipo.equals("Estudiante") && datos.length >= 11) {
            // Nota: El semestre se hardcodea a 1 ya que no parece guardarse en el CSV actual
            usuario = new Estudiante(cedula, nombre, apellido, email, contraseña, datos[9], 1, datos[10]);
        } else if (tipo.equals("Empleado") && datos.length >= 12) {
            usuario = new Empleado(cedula, nombre, apellido, email, contraseña, datos[9], datos[10], datos[11]);
        } else if (tipo.equals("Administrador") && datos.length >= 10) {
            usuario = new Administrador(cedula, nombre, apellido, email, contraseña, datos[9]);
        }

        if (usuario != null) {
            usuario.setEstado(estado);
            usuario.setIntentosFallidos(intentos);
            usuario.setSaldo(saldo);
        }
        return usuario;
    }
}