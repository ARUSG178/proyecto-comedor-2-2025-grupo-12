package com.comedor.vista.usuario;

import com.comedor.controlador.ServicioBiometrico;
import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class ReconocimientoFacialUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private final LocalDateTime fechaReserva;
    
    private File archivoSeleccionado;
    private JLabel lblPreview;
    private JLabel lblEstado;
    private JButton btnConfirmar;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FONDO = new Color(245, 245, 250);

    // Constructor principal que recibe los datos de la transacción
    public ReconocimientoFacialUI(Usuario usuario, double costoPlatillo, LocalDateTime fechaReserva) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        this.fechaReserva = fechaReserva;
        configurarVentana();
        initUI();
    }

    // Configura las propiedades de la ventana
    private void configurarVentana() {
        setTitle("Verificación Biométrica - SAGC");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
    }

    // Inicializa los componentes de la interfaz de reconocimiento
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_FONDO);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL_INST);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        JLabel lblTitulo = new JLabel("Validación de Identidad", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JLabel btnBack = new JLabel("  < Volver");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Aumentado de 14 a 20
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Regresar a selección de fecha
                new ReservaFechaUI(usuario, costoPlatillo).setVisible(true);
                dispose();
            }
        });
        headerPanel.add(btnBack, BorderLayout.WEST);

        // Centro
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblInstruccion = new JLabel("<html><center>Suba una foto de su rostro para autorizar<br>el cobro de <b>$ " + String.format("%.2f", costoPlatillo) + "</b></center></html>");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 0;
        centerPanel.add(lblInstruccion, gbc);

        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setPreferredSize(new Dimension(300, 300));
        previewContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        previewContainer.setBackground(Color.WHITE);

        lblPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        previewContainer.add(lblPreview, BorderLayout.CENTER);

        gbc.gridy = 1;
        centerPanel.add(previewContainer, gbc);

        JButton btnSubir = new JButton("Cargar Foto");
        btnSubir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSubir.addActionListener(e -> seleccionarFoto());
        gbc.gridy = 2;
        centerPanel.add(btnSubir, gbc);

        lblEstado = new JLabel("Esperando imagen...");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(Color.GRAY);
        gbc.gridy = 3;
        centerPanel.add(lblEstado, gbc);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footerPanel.setOpaque(false);

        btnConfirmar = new JButton("Verificar y Pagar");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(COLOR_AZUL_INST);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(200, 45));
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> procesarVerificacion());

        footerPanel.add(btnConfirmar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Abre un selector de archivos para cargar la imagen del rostro
    private void seleccionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(archivoSeleccionado);
                
                // CORRECCIÓN: Escalar proporcionalmente para evitar que se vea estirada
                ImageIcon icon = escalarImagenProporcional(img, 300, 300);
                
                lblPreview.setText("");
                lblPreview.setIcon(icon);
                btnConfirmar.setEnabled(true);
                lblEstado.setText("Imagen cargada.");
            } catch (IOException e) {
                lblEstado.setText("Error al cargar imagen.");
            }
        }
    }

    // Escala la imagen manteniendo la proporción para la previsualización
    private ImageIcon escalarImagenProporcional(BufferedImage img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();
        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        Image dimg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(dimg);
    }

    // Ejecuta la lógica de validación biométrica y cobro
    private void procesarVerificacion() {
        lblEstado.setText("Procesando...");
        try {
            // 1. Delegar validación biométrica
            ServicioBiometrico sBiometrico = new ServicioBiometrico();
            double similitud = sBiometrico.calcularSimilitud(usuario, archivoSeleccionado);
            
            if (similitud <= 60.0) {
                throw new Exception(String.format("Biometría fallida.\nSimilitud: %.2f%%\nSe requiere > 60%%", similitud));
            }

            // 2. Delegar cobro
            ServicioPago sPago = new ServicioPago();
            sPago.procesarCobro(usuario, costoPlatillo);

            // Si no hubo excepciones, todo fue exitoso
                String mensaje = String.format("¡Pago Exitoso!\nSimilitud Biométrica: %.2f%%\nReserva confirmada para: %s", 
                                             similitud, fechaReserva.toString().replace("T", " "));
                JOptionPane.showMessageDialog(this, mensaje, "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
                new MainUserUI(usuario).setVisible(true);
                dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Fallo", JOptionPane.ERROR_MESSAGE);
            lblEstado.setText("Intente nuevamente.");
        }
    }
}