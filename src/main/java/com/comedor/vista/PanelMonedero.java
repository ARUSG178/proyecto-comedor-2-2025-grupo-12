package com.comedor.vista;

import javax.swing.*;
import java.awt.*;
import com.comedor.modelo.entidades.Monedero;
import com.comedor.modelo.entidades.Usuario;

public class PanelMonedero extends JPanel {
    private Monedero monedero;
    private JLabel lblSaldo;
    private JTextField txtRecarga;

    public PanelMonedero(Usuario usuario) {
        this.monedero = new Monedero(usuario);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Mi Monedero"));

        // Panel Superior: Saldo
        JPanel pnlInfo = new JPanel();
        lblSaldo = new JLabel("Saldo Disponible: $" + String.format("%.2f", monedero.obtSaldo()));
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(0, 100, 0));
        pnlInfo.add(lblSaldo);
        add(pnlInfo, BorderLayout.NORTH);

        // Panel Central: Recarga
        JPanel pnlRecarga = new JPanel(new FlowLayout());
        pnlRecarga.add(new JLabel("Monto a recargar: $"));
        txtRecarga = new JTextField(8);
        JButton btnRecargar = new JButton("Recargar");
        
        btnRecargar.addActionListener(e -> realizarRecarga());
        
        pnlRecarga.add(txtRecarga);
        pnlRecarga.add(btnRecargar);
        add(pnlRecarga, BorderLayout.CENTER);
    }

    private void realizarRecarga() {
        try {
            double monto = Double.parseDouble(txtRecarga.getText());
            
            // Validación de negocio: Solo incrementar
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto a recargar debe ser mayor a 0.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            monedero.recargar(monto);
            actualizarVisualizacion();
            txtRecarga.setText("");
            JOptionPane.showMessageDialog(this, "Recarga exitosa.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarVisualizacion() {
        lblSaldo.setText("Saldo Disponible: $" + String.format("%.2f", monedero.obtSaldo()));
    }
}