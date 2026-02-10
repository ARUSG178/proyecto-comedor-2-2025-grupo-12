package com.comedor.modelo.persistencia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Usuario;

public class RepositorioUsuarios {
    private static final String RUTA_ARCHIVO = "src/main/java/com/comedor/data/usuarios.txt";

    // Método para guardar un usuario al final del archivo (Append)
    public void guardarUsuario(Usuario usuario) throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            escritor.write(serializar(usuario));
            escritor.newLine();
        }
    }

    //metodo para guardar toda la lista de usuarios (Sobrescribir archivo) para actualizaciones de saldo
    public void guardarTodos(List<Usuario> usuarios) throws IOException {
        Path path = Paths.get(RUTA_ARCHIVO);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Usuario usuario : usuarios) {
                escritor.write(serializar(usuario));
                escritor.newLine();
            }
        }
    }

    //metodo para leer todos los usuarios del archivo
    public List<Usuario> listarUsuarios() throws IOException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        Path path = Paths.get(RUTA_ARCHIVO);

        //si el archivo no existe, retornamos lista vacía
        if (!Files.exists(path)) {
            return listaUsuarios;
        }

        try (BufferedReader lector = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                Usuario u = deserializar(linea);
                if (u != null) listaUsuarios.add(u);
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
            buffer.append(estudiante.getFacultad());
        } else if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;
            buffer.append(emp.getCargo()).append(";");
            buffer.append(emp.getDepartamento()).append(";");
            buffer.append(emp.getCodigoEmpleado());
        } else if (usuario instanceof Administrador) {
            // Si Administrador tiene un campo adicional, agregar aquí
            Administrador adm = (Administrador) usuario;
            // Intentar agregar un campo identificador común; si no existe, dejar vacío
            try {
                // asume método getCodigoAdministrador o similar; si no existe, se ignora
                String codigo = "";
                try {
                    codigo = (String) adm.getClass().getMethod("getCodigoAdministrador").invoke(adm);
                } catch (NoSuchMethodException ex) {
                    try {
                        codigo = (String) adm.getClass().getMethod("getCodigo").invoke(adm);
                    } catch (NoSuchMethodException ex2) {
                        // no method found -> leave codigo empty
                    }
                }
                buffer.append(codigo);
            } catch (Exception ignored) {
            }
        }
        return buffer.toString();
    }

    //convierte un String (CSV) a objeto Usuario
    private Usuario deserializar(String linea) {
        String[] datos = linea.split(";");
        if (datos.length < 9) return null; // Validación básica de estructura

        try {
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
                usuario = new Estudiante(cedula, nombre, apellido, email, contraseña, datos[9], datos[10]);
            } else if (tipo.equals("Empleado") && datos.length >= 12) {
                usuario = new Empleado(cedula, nombre, apellido, email, contraseña, datos[9], datos[10], datos[11]);
            } else if (tipo.equals("Administrador") && datos.length >= 10) {
                // Asume Administrador tiene constructor con un campo extra en datos[9]
                usuario = new Administrador(cedula, nombre, apellido, email, contraseña, datos[9]);
            }

            if (usuario != null) {
                usuario.setEstado(estado);
                usuario.setIntentosFallidos(intentos);
                usuario.setSaldo(saldo);
            }
            return usuario;
        } catch (RuntimeException ex) {
            // Línea malformada o parseo falló -> ignorar y devolver null
            return null;
        }
    }
}