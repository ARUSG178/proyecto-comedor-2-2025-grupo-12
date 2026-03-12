package com.comedor.vista.auth;

import com.comedor.controlador.ServicioIS;
import com.comedor.controlador.ServicioFactory;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.InvalidCredentialsException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class LoginReconocimientoUI extends JFrame {

    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_FORM_BG = new Color(255, 255, 255);
    private static final Color COLOR_INPUT_BG = new Color(0, 85, 170);
    private static final Color COLOR_TEXTO = new Color(0, 51, 102);

    private BufferedImage backgroundImage;
    private ModernTextField txtCedula;
    private ModernPasswordField txtClave;

    public LoginReconocimientoUI() {
        try {
            URL imageUrl = getClass().getResource("/images/ui/com_is_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            // Imagen de fondo opcional
        }
        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Iniciar Sesión - Verificación Biométrica - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(COLOR_AZUL_INST);

        // POSICIONES INVERTIDAS: Panel izquierdo ahora tiene el formulario (como el panel derecho de InicioSesionUI)
        mainPanel.add(crearPanelFormulario());
        // Panel derecho ahora tiene la imagen (como el panel izquierdo de InicioSesionUI)
        mainPanel.add(crearPanelImagen());

        setContentPane(mainPanel);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_AZUL_INST);

        ShadowRoundedPanel card = new ShadowRoundedPanel(new BorderLayout());
        card.setBackground(COLOR_FORM_BG);
        card.setBorder(new EmptyBorder(18, 22, 22, 22));
        card.setPreferredSize(new Dimension(520, 360));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Verificación Biométrica");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        body.add(lblTitulo);
        body.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblSubtitulo = new JLabel("Inicie sesión para continuar");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_TEXTO);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);
        body.add(lblSubtitulo);
        body.add(Box.createRigidArea(new Dimension(0, 25)));

        txtCedula = new ModernTextField("Ej. 12345678");
        txtCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        body.add(crearCampo("Cédula:", txtCedula));
        body.add(Box.createRigidArea(new Dimension(0, 10)));

        txtClave = new ModernPasswordField("•••••••••");
        txtClave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        body.add(crearCampo("Contraseña:", txtClave));
        body.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnEntrar = new JButton("Continuar");
        styleButton(btnEntrar);
        btnEntrar.setAlignmentX(CENTER_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(360, 40));
        btnEntrar.addActionListener(e -> ejecutarLogin());
        body.add(btnEntrar);

        card.add(body, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, gbc);

        return panel;
    }

    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 51, 102, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(COLOR_AZUL_INST);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(new GridBagLayout());

        JLabel lbLogo = new JLabel("SAGC", SwingConstants.CENTER);
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 110));
        lbLogo.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Sistema de Asignación y Gestión del Comedor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 220));
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        lbLogo.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        logoPanel.add(lbLogo);
        logoPanel.add(subtitle);

        panel.add(logoPanel);
        return panel;
    }

    private JPanel crearCampo(String label, JComponent field) {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);
        container.setAlignmentX(CENTER_ALIGNMENT);
        container.setMaximumSize(new Dimension(360, 80));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(COLOR_TEXTO);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(l, BorderLayout.NORTH);
        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private void ejecutarLogin() {
        String cedula = txtCedula.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (cedula.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese cédula y contraseña",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServicioIS servicio = ServicioFactory.getInstance().crearServicioIS();

        try {
            Usuario usuarioIngresado = new Estudiante(cedula, clave, "", "");
            Usuario usuarioReal = servicio.IniciarSesion(usuarioIngresado);

            JOptionPane.showMessageDialog(this,
                "¡Bienvenido! Ahora proceda con la verificación biométrica.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            // Abrir reconocimiento facial
            SwingUtilities.invokeLater(() -> {
                // Cargar datos de la reserva pendiente si existen
                java.io.File requestFile = new java.io.File("src/main/resources/config/verification_request.properties");
                if (requestFile.exists()) {
                    try (java.io.FileInputStream in = new java.io.FileInputStream(requestFile)) {
                        java.util.Properties props = new java.util.Properties();
                        props.load(in);

                        double costo = Double.parseDouble(props.getProperty("costo", "0.0"));
                        java.time.LocalDateTime fecha = java.time.LocalDateTime.parse(props.getProperty("fechaReserva", java.time.LocalDateTime.now().toString()));

                        new ReconocimientoFacialUI(usuarioReal, costo, fecha).setVisible(true);
                        dispose();
                    } catch (Exception ex) {
                        // Si hay error, abrir con valores por defecto
                        new ReconocimientoFacialUI(usuarioReal, 0.0, java.time.LocalDateTime.now()).setVisible(true);
                        dispose();
                    }
                } else {
                    // Si no hay archivo de solicitud, mostrar mensaje
                    JOptionPane.showMessageDialog(this,
                        "No hay una compra pendiente para verificar.",
                        "Sin solicitud",
                        JOptionPane.WARNING_MESSAGE);
                }
            });

        } catch (InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error accediendo a la base de datos",
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        }
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

    private class ModernTextField extends JTextField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private final String hint;

        public ModernTextField(String h) {
            this.hint = h;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            if (getText().isEmpty()) {
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, 15, y);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    private class ModernPasswordField extends JPasswordField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private final String hint;

        public ModernPasswordField(String h) {
            this.hint = h;
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            if (getPassword().length == 0) {
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, 15, y);
            }

            super.paintComponent(g);
            g2.dispose();
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
