package com.comedor.vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import com.comedor.controlador.ServicioIS;
import com.comedor.modelo.entidades.Estudiante; 
import com.comedor.modelo.excepciones.InvalidCredentialsException;
import com.comedor.modelo.excepciones.InvalidEmailFormatException;

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

    private BufferedImage backgroundImage;
    private ModernTextField txtEmail;
    private ModernPasswordField txtClave;

    /**
     * Constructor de la interfaz. Carga los recursos gráficos iniciales
     * y dispara la construcción de los componentes de la ventana.
     */
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

    /**
     * Establece las propiedades de la ventana JFrame, como el título, 
     * dimensiones predeterminadas y la ubicación central en pantalla.
     */
    private void configurarVentana() {
        setTitle("Iniciar Sesión - SAGC UCV");
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Inicializa y organiza los componentes del contenedor principal.
     * Divide la pantalla en un área visual decorativa (izquierda) y 
     * un área funcional de formulario (derecha).
     */
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
        card.setPreferredSize(new Dimension(450, 550));

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

        JButton btnEntrar = new JButton("Entrar");
        styleButton(btnEntrar);
        btnEntrar.addActionListener(e -> ejecutarLogin());
        
        gbc.gridy = 3; gbc.insets = new Insets(30, 0, 10, 0);
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
        gbc.gridy = 4;
        card.add(btnIrRegistro, gbc);

        rightPanel.add(card);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);
    }

    /**
     * Extrae la información de los campos de texto, realiza una validación 
     * de presencia de datos y envía la solicitud al servicio de autenticación.
     */
    private void ejecutarLogin() {
        String email = txtEmail.getText().trim();
        String clave = new String(txtClave.getPassword());
        
        if (email.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese correo y contraseña", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Estudiante userIngresado = new Estudiante("", "", "", email, clave, "", "");
        ServicioIS servicio = new ServicioIS();
        
        try {
            servicio.IniciarSesion(userIngresado);
            JOptionPane.showMessageDialog(this, "¡Bienvenido al sistema!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidCredentialsException | InvalidEmailFormatException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ioEx) {
            JOptionPane.showMessageDialog(this, "Error accediendo a la base de datos de usuarios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea una agrupación vertical compuesta por una etiqueta descriptiva 
     * y su respectivo campo de entrada, insertándola en el panel destino.
     * * @param p Panel contenedor.
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
     * * @param b Botón a estilizar.
     */
    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(COLOR_BTN_VERDE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 0, 12, 0));
    }

    /**
     * Componente personalizado de entrada de texto que implementa 
     * bordes redondeados y una estética coherente con el manual de marca.
     */
    private class ModernTextField extends JTextField {
        public ModernTextField(String h) { 
            setOpaque(false); setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); setBorder(new EmptyBorder(10, 15, 10, 15));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * Variante de ModernTextField diseñada específicamente para contraseñas,
     * manteniendo el renderizado redondeado pero ocultando el contenido.
     */
    private class ModernPasswordField extends JPasswordField {
        public ModernPasswordField(String h) { 
            setOpaque(false); setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); setBorder(new EmptyBorder(10, 15, 10, 15));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * Panel contenedor especializado que dibuja una elevación (sombra) 
     * y un fondo redondeado para simular una tarjeta de diseño moderno.
     */
    private class ShadowRoundedPanel extends JPanel {
        public ShadowRoundedPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
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