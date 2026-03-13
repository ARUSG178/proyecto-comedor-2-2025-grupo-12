package com.comedor.controlador;

import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.SaldoInsuficienteException;
import com.comedor.modelo.persistencia.RepoUsuarios;
import com.comedor.controlador.ServicioMenu;
import java.util.List;

public class ServicioPago {

    // Verifica el saldo disponible y realiza el descuento si es suficiente, persistiendo el cambio
    public void procesarCobro(Usuario usuario, double monto) throws Exception {
        Monedero monedero = new Monedero(usuario);
        double saldoActual = monedero.obtSaldo();
        
        // USAR EL SISTEMA QUE SÍ LEE DEL ARCHIVO CONFIGURABLE
        ServicioMenu servicioMenu = new ServicioMenu();
        double factor = servicioMenu.factorParaUsuario(usuario);
        double tarifaFinal = monto * factor;

        if (saldoActual < tarifaFinal) {
            throw new SaldoInsuficienteException(String.format("Saldo insuficiente. Costo: $%.2f, Disponible: $%.2f", tarifaFinal, saldoActual));
        }

        monedero.descontar(tarifaFinal);

        // Persistir el nuevo saldo (descuento) en la base de datos
        RepoUsuarios repo = new RepoUsuarios();
        List<Usuario> usuarios = repo.listarUsuarios();
        for (Usuario u : usuarios) {
            if (u.obtCedula().equals(usuario.obtCedula())) {
                u.setSaldo(monedero.obtSaldo());
                break;
            }
        }
        repo.guardarTodos(usuarios);
        
        // Actualizar referencia local
        usuario.setSaldo(monedero.obtSaldo());
        
        // El monto se guardará en la reserva directamente (ReconocimientoFacialUI)
        // Ya no se necesita guardar en ingresos.txt
    }
}