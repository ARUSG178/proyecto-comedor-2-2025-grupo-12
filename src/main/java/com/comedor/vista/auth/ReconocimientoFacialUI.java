package com.comedor.vista.auth;

import com.comedor.controlador.ServicioBiometrico;
import com.comedor.controlador.ServicioPago;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Reserva;
import com.comedor.modelo.persistencia.RepoReservas;
import com.comedor.modelo.excepciones.BiometriaFallidaException;
import com.comedor.vista.usuario.HistorialReservasUI;
import com.comedor.vista.usuario.PrincipalUserUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.awt.Window;

public class ReconocimientoFacialUI extends JFrame {

    private final Usuario usuario;
    private final double costoPlatillo;
    private final LocalDateTime fechaReserva;

    private File archivoSeleccionado;
    private JLabel lblPreview;
    private JLabel lblEstado;
    private JButton btnConfirmar;

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FORM_BG = new Color(255, 255, 255);
    private static final Color COLOR_TEXTO = new Color(0, 51, 102);

    private BufferedImage backgroundImage;

    public ReconocimientoFacialUI(Usuario usuario, double costoPlatillo, LocalDateTime fechaReserva) {
        this.usuario = usuario;
        this.costoPlatillo = costoPlatillo;
        this.fechaReserva = fechaReserva;

        try {
            URL imageUrl = getClass().getResource("/images/ui/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            // Imagen de fondo opcional
        }

        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Verificación Biométrica - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_AZUL_INST);

        if (backgroundImage != null) {
            mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.setColor(new Color(0, 51, 102, 140));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
        }

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel lblTitulo = new JLabel("Validación de Identidad", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        // Centro
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        ShadowRoundedPanel card = new ShadowRoundedPanel(new BorderLayout());
        card.setBackground(COLOR_FORM_BG);
        card.setBorder(new EmptyBorder(20, 25, 25, 25));
        card.setPreferredSize(new Dimension(500, 750));

        JPanel cardContent = new JPanel();
        cardContent.setOpaque(false);
        cardContent.setLayout(new BoxLayout(cardContent, BoxLayout.Y_AXIS));

        JLabel lblInstruccion = new JLabel("<html><center>Suba una foto de su rostro para autorizar<br>el cobro de <b>$ " + String.format("%.2f", costoPlatillo) + "</b></center></html>", SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInstruccion.setForeground(COLOR_TEXTO);
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblInstruccion.setHorizontalAlignment(SwingConstants.CENTER);
        cardContent.add(lblInstruccion);
        cardContent.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100, 150), 1));
        previewContainer.setBackground(new Color(0, 0, 0, 180));
        previewContainer.setAlignmentX(CENTER_ALIGNMENT);
        previewContainer.setMaximumSize(new Dimension(450, 400));

        lblPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPreview.setForeground(Color.GRAY);
        previewContainer.add(lblPreview, BorderLayout.CENTER);

        cardContent.add(previewContainer);
        cardContent.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnSubir = new JButton("Cargar Foto");
        btnSubir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubir.setBackground(COLOR_AZUL_INST);
        btnSubir.setForeground(Color.WHITE);
        btnSubir.setFocusPainted(false);
        btnSubir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubir.setAlignmentX(CENTER_ALIGNMENT);
        btnSubir.setMaximumSize(new Dimension(200, 40));
        btnSubir.addActionListener(e -> seleccionarFoto());
        cardContent.add(btnSubir);
        cardContent.add(Box.createRigidArea(new Dimension(0, 15)));

        lblEstado = new JLabel("Esperando imagen...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEstado.setForeground(COLOR_TEXTO);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);
        cardContent.add(lblEstado);
        cardContent.add(Box.createRigidArea(new Dimension(0, 30)));

        btnConfirmar = new JButton("Verificar y Pagar");
        styleButton(btnConfirmar);
        btnConfirmar.setAlignmentX(CENTER_ALIGNMENT);
        btnConfirmar.setMaximumSize(new Dimension(360, 40));
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> procesarVerificacion());
        cardContent.add(btnConfirmar);
        cardContent.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnVolver = new JButton("Volver al Menú Principal");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnVolver.setForeground(COLOR_TEXTO);
        btnVolver.setBackground(new Color(230, 235, 245));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(CENTER_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(360, 40));
        btnVolver.addActionListener(e -> {
            new PrincipalUserUI(usuario).setVisible(true);
            dispose();
        });
        cardContent.add(btnVolver);

        card.add(cardContent, BorderLayout.CENTER);

        gbc.gridy = 0;
        centerPanel.add(card, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(new Color(0, 123, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(15, 0, 15, 0));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(0, 86, 179));
                b.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(0, 123, 255));
                b.repaint();
            }
        });

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton button = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 30, 30);

                FontMetrics fm = g2.getFontMetrics();
                int textX = (button.getWidth() - fm.stringWidth(button.getText())) / 2;
                int textY = (button.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(button.getForeground());
                g2.setFont(button.getFont());
                g2.drawString(button.getText(), textX, textY);

                g2.dispose();
            }
        });
    }

    private void seleccionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(archivoSeleccionado);
                // Calcular dimensiones proporcionales manteniendo aspect ratio
                int maxSize = 350;
                int originalWidth = img.getWidth();
                int originalHeight = img.getHeight();
                double ratio = Math.min((double) maxSize / originalWidth, (double) maxSize / originalHeight);
                
                int newWidth = (int) (originalWidth * ratio);
                int newHeight = (int) (originalHeight * ratio);
                
                // Ajustar el contenedor a las dimensiones de la imagen
                Component parent = lblPreview.getParent();
                if (parent instanceof JPanel) {
                    parent.setPreferredSize(new Dimension(newWidth, newHeight));
                    parent.setMaximumSize(new Dimension(newWidth, newHeight));
                }
                
                ImageIcon icon = escalarImagenProporcional(img, newWidth, newHeight);
                lblPreview.setText("");
                lblPreview.setIcon(icon);
                btnConfirmar.setEnabled(true);
                lblEstado.setText("Imagen cargada. Listo para verificar.");
                lblEstado.setForeground(new Color(0, 100, 0));
                
                // Refrescar el layout
                revalidate();
                repaint();
            } catch (IOException e) {
                lblEstado.setText("Error al cargar imagen.");
                lblEstado.setForeground(Color.RED);
            }
        }
    }

    private ImageIcon escalarImagenProporcional(BufferedImage img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();
        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Image dimg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(dimg);
    }

    private void procesarVerificacion() {
        lblEstado.setText("Procesando...");
        lblEstado.setForeground(COLOR_TEXTO);
        try {
            ServicioBiometrico sBiometrico = new ServicioBiometrico();
            double similitud = sBiometrico.calcularSimilitud(usuario, archivoSeleccionado);

            if (similitud <= 60.0) {
                lblEstado.setText("Usuario inválido");
                lblEstado.setForeground(Color.RED);
                throw new BiometriaFallidaException("Usuario inválido");
            }

            lblEstado.setText("Usuario válido");
            lblEstado.setForeground(new Color(0, 100, 0));

            ServicioPago sPago = new ServicioPago();
            sPago.procesarCobro(usuario, costoPlatillo);

            Reserva nuevaReserva = new Reserva(usuario, fechaReserva, "Completado", costoPlatillo);
            RepoReservas.guardarReserva(nuevaReserva);

            try {
                SwingUtilities.invokeLater(() -> {
                    for (Window window : Window.getWindows()) {
                        if (window instanceof HistorialReservasUI) {
                            window.dispose();
                            break;
                        }
                    }
                    new HistorialReservasUI(usuario).setVisible(true);
                });
            } catch (Exception ex) {
                // Error silenciado al actualizar historial
            }

            String mensaje = String.format("¡Pago Exitoso!\nUsuario válido\nReserva confirmada para: %s\n\nEl historial ha sido actualizado.",
                    fechaReserva.toString().replace("T", " "));
            JOptionPane.showMessageDialog(this, mensaje, "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

            new PrincipalUserUI(usuario).setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Resultado de Verificación", JOptionPane.ERROR_MESSAGE);
            lblEstado.setText("Verificación fallida. Intente nuevamente.");
            lblEstado.setForeground(Color.RED);
        }
    }

    private class ShadowRoundedPanel extends JPanel {
        public ShadowRoundedPanel(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillRoundRect(10, 10, getWidth() - 10, getHeight() - 10, 30, 30);
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(7, 7, getWidth() - 7, getHeight() - 7, 30, 30);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 30, 30);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);
            g2.dispose();
        }
    }
}
