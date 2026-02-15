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
        buffer.append(usuario.obtTipo()).append(";");
        buffer.append(usuario.obtCedula()).append(";");
        buffer.append(usuario.obtContraseña()).append(";");
        buffer.append(usuario.obtEstado()).append(";");
        buffer.append(usuario.obtIntentosFallidos()).append(";");
        buffer.append(usuario.obtSaldo()).append(";");

        //datos específicos según el tipo
        if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            buffer.append(estudiante.obtCarrera()).append(";");
            buffer.append(estudiante.obtFacultad());
        } else if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;
            buffer.append(emp.obtCargo()).append(";");
            buffer.append(emp.obtDepartamento()).append(";");
            buffer.append(emp.obtCodigoEmpleado());
        } else if (usuario instanceof Administrador) {
            // Si Administrador tiene un campo adicional, agregar aquí
            Administrador adm = (Administrador) usuario;
            buffer.append(adm.obtCodigoAdministrador());
        }
        return buffer.toString();
    }

    //convierte un String (CSV) a objeto Usuario
    private Usuario deserializar(String linea) {
        String[] datos = linea.split(";");
        if (datos.length < 6) return null; // Validación básica ajustada a menos campos

        try {
            String tipo = datos[0];
            String cedula = datos[1];
            String contraseña = datos[2];
            boolean estado = Boolean.parseBoolean(datos[3]);
            int intentos = Integer.parseInt(datos[4]);
            double saldo = Double.parseDouble(datos[5]);

            Usuario usuario = null;

            //instanciación según el tipo
            if (tipo.equals("Estudiante") && datos.length >= 8) {
                usuario = new Estudiante(cedula, contraseña, datos[6], datos[7]);
            } else if (tipo.equals("Empleado") && datos.length >= 9) {
                // Ajustado asumiendo constructor (cedula, pass, cargo, depto, codigo)
                usuario = new Empleado(cedula, contraseña, datos[6], datos[7], datos[8]);
            } else if (tipo.equals("Administrador") && datos.length >= 7) {
                usuario = new Administrador(cedula, contraseña, datos[6]);
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