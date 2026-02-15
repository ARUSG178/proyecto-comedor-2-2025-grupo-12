package com.comedor.modelo.entidades;

public class Empleado extends Usuario {
    private String cargo;         
    private String departamento;  
    private String codigoEmpleado; 

    public Empleado(String cedula, String contraseña, String cargo, String departamento, String codigoEmpleado) {
        super(cedula, contraseña);
        setCargo(cargo);
        setDepartamento(departamento);
        setCodigoEmpleado(codigoEmpleado);
    }

    @Override
    public String obtTipo() { return "Empleado"; }

    // Getters
    public String obtCargo() { return cargo; }
    public String obtDepartamento() { return departamento; }
    public String obtCodigoEmpleado() { return codigoEmpleado; }

    // Setters
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }
}