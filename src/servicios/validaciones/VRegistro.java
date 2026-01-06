package servicios.validaciones;

import modelos.Usuario;
import servicios.excepciones.*;
import modelos.Estudiante;
import modelos.Empleado;
import util.ValidacionUtil;
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

    // Validar todo el registro
    public void validar() throws InvalidEmailFormatException, InvalidCredentialsException, DuplicateUserException {
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
        if (!validarEstudiante()) {
            throw new InvalidCredentialsException("Datos de estudiante incompletos o inválidos");
        }
        if (!validarEmpleado()) {
            throw new InvalidCredentialsException("Datos de empleado incompletos o inválidos");
        }
    }
}