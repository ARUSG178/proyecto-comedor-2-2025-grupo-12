package com.comedor.vista;

import javax.swing.*;
import java.awt.*;
import com.comedor.controlador.ServicioCosto;

public class DialogoCCB extends JDialog {
    private JTextField txtFijos;
    private JTextField txtVariables;
    private JTextField txtProduccion;
    private JTextField txtMerma;
    private JLabel lblResultado;
    private ServicioCosto servicioCosto;

    public DialogoCCB(Frame parent) {
        super(parent, "Cálculo de CCB (Costo Cubierto por Bandeja)", true);
        this.servicioCosto = new ServicioCosto();
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel(" Costos Fijos ($):"));
        txtFijos = new JTextField();
        add(txtFijos);

        add(new JLabel(" Costos Variables ($):"));
        txtVariables = new JTextField();
        add(txtVariables);

        add(new JLabel(" Producción Total (Bandejas):"));
        txtProduccion = new JTextField();
        add(txtProduccion);

        add(new JLabel(" Merma (Bandejas desechadas):"));
        txtMerma = new JTextField();
        add(txtMerma);

        JButton btnCalcular = new JButton("Calcular y Guardar");
        btnCalcular.addActionListener(e -> calcular());
        add(btnCalcular);

        lblResultado = new JLabel("CCB Actual: -");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblResultado);
    }

    private void calcular() {
        try {
            double fijos = Double.parseDouble(txtFijos.getText());
            double variables = Double.parseDouble(txtVariables.getText());
            int produccion = Integer.parseInt(txtProduccion.getText());
            int merma = Integer.parseInt(txtMerma.getText());

            if (produccion <= merma) {
                JOptionPane.showMessageDialog(this, "La merma no puede ser mayor o igual a la producción.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double ccb = servicioCosto.calcularRegistrarCCBCompleto(fijos, variables, produccion, merma);
            lblResultado.setText(String.format("CCB: $%.2f", ccb));
            JOptionPane.showMessageDialog(this, "Cálculo realizado y guardado exitosamente.\nNuevo Costo por Bandeja: $" + String.format("%.2f", ccb));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}