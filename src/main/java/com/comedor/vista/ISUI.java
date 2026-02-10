package com.comedor.vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.comedor.controlador.ServicioIS;
import com.comedor.modelo.entidades.Administrador;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.modelo.excepciones.InvalidEmailFormatException;
import com.comedor.vista.admin.MainAdminUI;
import com.comedor.vista.usuario.MainUserUI;

/**
 * Interfaz gráfica principal para el inicio de sesión del sistema SAGC UCV.
 * Gestiona la presentación visual de doble panel (imagen/formulario) y coordina
 * la captura de credenciales con la lógica de autenticación del controlador.
 */
public class ISUI extends JFrame {

    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);
    private static final Color COLOR_FORM_BG = new Color(248, 245, 235);
    private static final Color COLOR_INPUT_BG = new Color(175, 125, 95);
    private static final Color COLOR_BTN_VERDE = new Color(75, 105, 50);
    private static final Color COLOR_TEXTO = new Color(60, 40, 30);
    private static final Color COLOR_RADIO_GOLD = new Color(210, 160, 30); // Color dorado para radios

    private BufferedImage backgroundImage;
    private ModernTextField txtEmail;
    private ModernPasswordField txtClave;
    private JRadioButton usuarioRadio;
    private JRadioButton adminRadio;

    public ISUI() {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_is_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }

        configurarVentana();
        initUI();
    }


    private void configurarVentana() {
        setTitle("Iniciar Sesión - SAGC UCV"); 
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);    
    }


    private void initUI() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(COLOR_TERRACOTA);

        // --- LADO IZQUIERDO: SECCIÓN DE IDENTIDAD VISUAL ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(160, 70, 40, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel lbLogo = new JLabel("SAGC", SwingConstants.CENTER);
        lbLogo.setFont(new Font("Segoe UI", Font.BOLD, 100));
        lbLogo.setForeground(Color.WHITE);
        lbLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbLogo.setFocusable(true);
        lbLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new RegistroUI().setVisible(true);
                ISUI.this.dispose();
            }
        });

        JLabel subtitle = new JLabel("Sistema de Asignación y Gestión del Comedor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(lbLogo, BorderLayout.CENTER);
        logoPanel.add(subtitle, BorderLayout.SOUTH);
        leftPanel.add(logoPanel);

        // --- LADO DERECHO: SECCIÓN DE CREDENCIALES ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(COLOR_TERRACOTA);

        ShadowRoundedPanel card = new ShadowRoundedPanel(new GridBagLayout());
        card.setBackground(COLOR_FORM_BG);
        card.setBorder(new EmptyBorder(50, 50, 50, 50));
        card.setPreferredSize(new Dimension(450, 600)); // Aumentado para incluir radios

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Iniciar Sesión");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(COLOR_TERRACOTA);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 30, 0);
        card.add(title, gbc);

        txtEmail = new ModernTextField("Ej. ucvista@ciens.ucv.ve");
        addLabelAndField(card, "Correo Institucional:", txtEmail, gbc, 1);

        txtClave = new ModernPasswordField("••••••••");
        addLabelAndField(card, "Contraseña:", txtClave, gbc, 2);

        // --- SECCIÓN DE RADIO BUTTONS PERSONALIZADOS ---
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        radioPanel.setOpaque(false);
        
        usuarioRadio = createCustomRadio("Usuario");
        adminRadio = createCustomRadio("Administrador");
        
        ButtonGroup group = new ButtonGroup();
        group.add(usuarioRadio);
        group.add(adminRadio);
        usuarioRadio.setSelected(true);
        
        radioPanel.add(usuarioRadio);
        radioPanel.add(adminRadio);
        
        gbc.gridy = 3; gbc.insets = new Insets(15, 0, 15, 0);
        card.add(radioPanel, gbc);

        JButton btnEntrar = new JButton("Entrar");
        styleButton(btnEntrar);
        btnEntrar.addActionListener(e -> ejecutarLogin());
        
        gbc.gridy = 4; gbc.insets = new Insets(30, 0, 10, 0);
        card.add(btnEntrar, gbc);

        JButton btnIrRegistro = new JButton("¿No tienes cuenta? Regístrate");
        btnIrRegistro.setContentAreaFilled(false);
        btnIrRegistro.setBorderPainted(false);
        btnIrRegistro.setForeground(COLOR_TERRACOTA);
        btnIrRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIrRegistro.addActionListener(e -> {
            new RegistroUI().setVisible(true);
            this.dispose();
        });
        gbc.gridy = 5;
        card.add(btnIrRegistro, gbc);

        rightPanel.add(card);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);
    }

    private void ejecutarLogin() {
        String email = txtEmail.getText().trim();
        String clave = new String(txtClave.getPassword());
        
        if (email.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese correo y contraseña", 
                "Datos incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServicioIS servicio = new ServicioIS();
        
        try {
            // Crear instancia Usuario según selección
            Usuario usuarioIngresado;
            if (adminRadio.isSelected()) {
                usuarioIngresado = new Administrador("", "", "", email, clave, "");
            } else {
                usuarioIngresado = new Estudiante("", "", "", email, clave, "", "");
            }
            
            Usuario usuarioReal = servicio.IniciarSesion(usuarioIngresado);
            
            if (usuarioReal instanceof Administrador) {
                JOptionPane.showMessageDialog(this, 
                    "¡Bienvenido Administrador!", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    new MainAdminUI().setVisible(true);
                });
                
            } else if (usuarioReal instanceof Estudiante) {
                JOptionPane.showMessageDialog(this, 
                    "¡Bienvenido Estudiante!", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Redirigir a MainUserUI
                SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    new MainUserUI().setVisible(true);
                });
            }
            
        } catch (InvalidCredentialsException ex) {
            // Este error ahora incluirá mensajes específicos sobre tipo de usuario
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidEmailFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Formato de correo inválido.\nUse el formato: usuario@dominio.ucv.ve", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ioEx) {
            JOptionPane.showMessageDialog(this, 
                "Error accediendo a la base de datos", 
                "Error del Sistema", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea una agrupación vertical compuesta por una etiqueta descriptiva 
     * y su respectivo campo de entrada, insertándola en el panel destino.
     * @param p Panel contenedor.
     * @param t Texto de la etiqueta.
     * @param f Componente de entrada de texto.
     * @param g Restricciones de GridBagLayout.
     * @param y Fila en la que se posicionará el grupo.
     */
    private void addLabelAndField(JPanel p, String t, JComponent f, GridBagConstraints g, int y) {
        g.gridy = y;
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(COLOR_TEXTO);
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);
        container.add(l, BorderLayout.NORTH);
        container.add(f, BorderLayout.CENTER);
        p.add(container, g);
    }

    /**
     * Define la apariencia visual de los botones de acción, configurando
     * tipografía, colores de fondo y bordes de relleno (padding).
     * @param b Botón a estilizar.
     */
    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(COLOR_BTN_VERDE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 0, 12, 0));
    }

    private JRadioButton createCustomRadio(String texto) {
        JRadioButton radio = new JRadioButton(texto);
        radio.setOpaque(false);
        radio.setFocusPainted(false); // ESTO ELIMINA EL RECUADRO AZUL
        radio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radio.setForeground(new Color(60, 40, 30));
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Aplicamos los círculos dibujados manualmente (mismo que RegistroUI)
        radio.setIcon(new CustomRadioIcon(false));
        radio.setSelectedIcon(new CustomRadioIcon(true));
        return radio;
    }

    private static class CustomRadioIcon implements Icon {
        private boolean sel;
        public CustomRadioIcon(boolean sel) { this.sel = sel; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_RADIO_GOLD);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(x, y + 2, 16, 16); // Círculo exterior
            if (sel) g2.fillOval(x + 4, y + 6, 9, 9); // Punto interior
            g2.dispose();
        }
        @Override public int getIconWidth() { return 25; }
        @Override public int getIconHeight() { return 20; }
    }

    private class ModernTextField extends JTextField {
        private final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160);
        private String hint;
        
        public ModernTextField(String h) { 
            this.hint = h; 
            setOpaque(false); 
            setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); 
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            // Texto placeholder si está vacío
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
        private String hint;
        
        public ModernPasswordField(String h) { 
            this.hint = h;
            setOpaque(false); 
            setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); 
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            
            // Texto placeholder si está vacío
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
        public ShadowRoundedPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 30, 30);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
            g2.dispose();
        }
    }

    /**
     * Punto de entrada de la interfaz de inicio de sesión.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ISUI().setVisible(true));
    }
}