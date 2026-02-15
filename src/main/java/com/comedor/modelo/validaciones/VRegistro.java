package com.comedor.modelo.validaciones; 

import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.*;
import com.comedor.util.ValidacionUtil;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.persistencia.RepoAdminCdg;

import java.io.IOException;
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
        return usBaseDatos.stream().noneMatch(u -> u.obtCedula().equals(uIngresado.obtCedula()));
    }

    // Validar datos exclusivos si es Estudiante
    boolean validarEstudiante() {
        if (uIngresado instanceof Estudiante) {
            Estudiante est = (Estudiante) uIngresado;
            return est.obtCarrera() != null && !est.obtCarrera().isEmpty();
        }
        return true;
    }

    // Validar datos exclusivos si es Empleado
    boolean validarEmpleado() {
        if (uIngresado instanceof Empleado) {
            Empleado emp = (Empleado) uIngresado;
            return emp.obtCargo() != null && !emp.obtCargo().isEmpty()
                    && emp.obtDepartamento() != null && !emp.obtDepartamento().isEmpty();
        }
        return true;
    }

    // Validar todo el registro
    public void validar() throws InvalidCredentialsException, DuplicateUserException, IOException {
        if (!ValidacionUtil.formatoCedula(uIngresado.obtCedula())) {
            throw new InvalidCredentialsException("La cédula no es válida");
        }
        if (!ValidacionUtil.formatoContraseña(uIngresado.obtContraseña())) {
            throw new InvalidCredentialsException("La contraseña no cumple requisitos mínimos");
        }
        if (!validarDuplicado()) {
            throw new DuplicateUserException("El usuario ya existe en la base de datos");
        }
        // Si es administrador, validar código contra archivo de códigos válidos
        if (uIngresado instanceof Administrador) {
            String codigo = ((Administrador) uIngresado).obtCodigoAdministrador();
            if (codigo == null || !codigo.trim().matches("[A-Za-z0-9]{8}")) {
                throw new InvalidCredentialsException("Código de administrador inválido");
            }
            RepoAdminCdg repoCdg = new RepoAdminCdg();
            if (!repoCdg.existeCodigo(codigo.trim())) {
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