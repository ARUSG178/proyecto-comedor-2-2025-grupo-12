package com.comedor.modelo.validaciones; 

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.modelo.util.ValidacionUtil;
import com.comedor.modelo.entidades.Administrador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VRegistro {
    private final Usuario uIngresado;
    private final List<Usuario> usBaseDatos;

    public VRegistro(Usuario uIngresado, List<Usuario> usBaseDatos) {
        if (uIngresado == null || usBaseDatos == null) {
            throw new IllegalArgumentException("El usuario y la lista no pueden ser nulos");
        }
        this.uIngresado = uIngresado;
        this.usBaseDatos = usBaseDatos;
    }

    // Validar que no exista duplicado en BD
    boolean validarDuplicado() {
        return usBaseDatos.stream().noneMatch(u -> u.getEmail().equals(uIngresado.getEmail()));
    }

    // Validar datos exclusivos si es Estudiante
    boolean validarEstudiante() {
        if (uIngresado instanceof Estudiante) {
            Estudiante est = (Estudiante) uIngresado;
            return est.getCarrera() != null && !est.getCarrera().isEmpty();
        }
        return true;
    }

    // Validar datos exclusivos si es Empleado
    boolean validarEmpleado() {
        if (uIngresado instanceof Empleado) {
            Empleado emp = (Empleado) uIngresado;
            return emp.getCargo() != null && !emp.getCargo().isEmpty()
                    && emp.getDepartamento() != null && !emp.getDepartamento().isEmpty();
        }
        return true;
    }

    // Comprueba si el código aparece en el archivo de códigos de administradores
    private boolean existeCodigoEnArchivo(String codigo) throws IOException {
        Path p = Paths.get("src/main/java/com/comedor/data/codigos_admin.txt");
        if (!Files.exists(p)) return false;
        String contenido = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        String[] cods = contenido.split(";");
        for (String c : cods) {
            if (c != null && codigo.equalsIgnoreCase(c.trim())) return true;
        }
        return false;
    }

    // Elimina (consume) el código de administrador del archivo tras registro exitoso
    public void consumirCodigoAdministrador() throws IOException {
        if (!(uIngresado instanceof Administrador)) return;
        String codigo = ((Administrador) uIngresado).getCodigoAdministrador();
        if (codigo == null) return;
        Path p = Paths.get("src/main/java/com/comedor/data/codigos_admin.txt");
        if (!Files.exists(p)) return;
        String contenido = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        String[] cods = contenido.split(";");
        StringBuilder sb = new StringBuilder();
        for (String c : cods) {
            if (c == null) continue;
            String t = c.trim();
            if (t.isEmpty()) continue;
            if (t.equalsIgnoreCase(codigo.trim())) continue; // omitir el código usado
            if (sb.length() > 0) sb.append("; ");
            sb.append(t);
        }
        if (sb.length() > 0) sb.append(";");
        Files.write(p, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Validar todo el registro
    public void validar() throws InvalidEmailFormatException, InvalidCredentialsException, DuplicateUserException, IOException {
        if (!ValidacionUtil.formatoCorreo(uIngresado.getEmail())) {
            throw new InvalidEmailFormatException("El correo no es institucional");
        }
        if (!ValidacionUtil.formatoCedula(uIngresado.getCedula())) {
            throw new InvalidCredentialsException("La cédula no es válida");
        }
        if (!ValidacionUtil.formatoContraseña(uIngresado.getContraseña())) {
            throw new InvalidCredentialsException("La contraseña no cumple requisitos mínimos");
        }
        if (!validarDuplicado()) {
            throw new DuplicateUserException("El usuario ya existe en la base de datos");
        }
        // Si es administrador, validar código contra archivo de códigos válidos
        if (uIngresado instanceof Administrador) {
            String codigo = ((Administrador) uIngresado).getCodigoAdministrador();
            if (codigo == null || !codigo.trim().matches("[A-Za-z0-9]{8}")) {
                throw new InvalidCredentialsException("Código de administrador inválido");
            }
            if (!existeCodigoEnArchivo(codigo.trim())) {
                throw new InvalidCredentialsException("Código de administrador no encontrado");
            }
        }
        if (!validarEstudiante()) {
            throw new InvalidCredentialsException("Datos de estudiante incompletos o inválidos");
        }
        if (!validarEmpleado()) {
            throw new InvalidCredentialsException("Datos de empleado incompletos o inválidos");
        }
    }


}