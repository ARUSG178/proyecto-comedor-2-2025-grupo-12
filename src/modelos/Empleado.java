package modelos;

public class Empleado extends Usuario {
    private String cargo;         
    private String departamento;  
    private String codigoEmpleado; 

    public Empleado(String cedula, String nombre, String apellido, String email, String contraseña,String cargo, String departamento, String codigoEmpleado) {
        super(cedula, nombre, apellido, email, contraseña);
        setCargo(cargo);
        setDepartamento(departamento);
        setCodigoEmpleado(codigoEmpleado);
    }

    @Override
    public String getTipo() { return "Empleado"; }

    // Getters
    public String getCargo() { return cargo; }
    public String getDepartamento() { return departamento; }
    public String getCodigoEmpleado() { return codigoEmpleado; }

    // Setters
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }
}