package com.comedor.modelo.entidades;

public class Monedero {// clase publica Monedero
    private Usuario propietario;// atributo privado de tipo Usuario para el propietario

    public Monedero(Usuario propietario) {//constructor de la clase Monedero que recibe un objeto Usuario como parametro
        this.propietario = propietario;
    }

    // Getters
    public Usuario getPropietario() { return propietario; }// metodo publico para obtener el propietario del monedero
    public double getSaldo() { return propietario.getSaldo(); }// obtiene el saldo directamente del usuario

    // Permite aumentar el saldo
    public void recargar(double monto) {
        if (monto > 0) {
            propietario.setSaldo(propietario.getSaldo() + monto);
        }
    }
    // Permite descontar del saldo si hay fondos suficientes
    public boolean descontar(double monto) {// clase publica tipo boolean o buleana para devolver verdadero o falso
        if (monto > 0 && getSaldo() >= monto) {
            propietario.setSaldo(getSaldo() - monto);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {// metodo para devolver una representacion en cadena del objeto Monedero
        return "Monedero de " + propietario.getNombre() + " [Saldo: $" + getSaldo() + "]";
    }
}
