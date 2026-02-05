package com.comedor.vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import com.comedor.controlador.ServicioRegistro;
import com.comedor.modelo.entidades.Usuario;
import com.comedor.modelo.entidades.Estudiante;
import com.comedor.modelo.entidades.Empleado;
import com.comedor.modelo.excepciones.DuplicateUserException;
import com.comedor.modelo.excepciones.InvalidEmailFormatException;
import com.comedor.modelo.excepciones.InvalidCredentialsException;

/**
 * Interfaz de Registro de Usuario - SAGC UCV.
 * Este código replica el prototipo visual centrado y estilizado.
 */
public class RegistroFrameUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(160, 70, 40, 140);      // Filtro sobre imagen
    private static final Color COLOR_FORM_BG = new Color(248, 245, 235);            // Fondo crema de la tarjeta
    private static final Color COLOR_INPUT_BG = new Color(175, 125, 95);            // Fondo café de los inputs
    private static final Color COLOR_RADIO_GOLD = new Color(210, 160, 30);          // Dorado para los Radios
    private static final Color COLOR_BTN_VERDE = new Color(75, 105, 50);            // Botón Registrar
    private static final Color COLOR_PLACEHOLDER = new Color(255, 255, 255, 160); // Texto fantasma

    private BufferedImage backgroundImage;
    private CardLayout cardLayout;
    private JPanel specificFieldsPanel;

    // { changed code } -- campos accesibles por el listener
    private ModernTextField txtNombre;
    private ModernTextField txtCedula;
    private ModernTextField txtEmail;
    private ModernTextField txtContrasena;

    private ModernTextField txtFacultad;
    private ModernTextField txtCarrera;

    private ModernTextField txtCargo;
    private ModernTextField txtDepartamento;

    public RegistroFrameUI() {
        // Intenta cargar la imagen de fondo desde la ruta especificada en tu proyecto
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Registro de Usuario - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana al abrirse
    }

    private void initUI() {
        // 1. PANEL DE FONDO: Dibuja la imagen, el filtro y las barras
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g2d.setColor(COLOR_OVERLAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Barras sólidas superior e inferior
                g2d.setColor(COLOR_TERRACOTA);
                int barHeight = 135;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // 2. CONTENEDOR DE CONTENIDO (GridBagLayout para el centrado)
        JPanel contentHost = new JPanel(new GridBagLayout());
        contentHost.setOpaque(false);
        GridBagConstraints gbcMain = new GridBagConstraints();

        // --- LOGO ESTILIZADO (< SAGC) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 60, 25));
        headerPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("< SAGC") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Sombra
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(getText(), 3, 43);
                // Degradado metálico
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(220, 220, 220)));
                g2.drawString(getText(), 0, 40);
                g2.dispose();
            }
        };
        brandLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        brandLabel.setPreferredSize(new Dimension(300, 60));
        headerPanel.add(brandLabel);

        gbcMain.gridx = 0; gbcMain.gridy = 0;
        gbcMain.weightx = 1.0; gbcMain.anchor = GridBagConstraints.NORTHWEST;
        contentHost.add(headerPanel, gbcMain);

        // --- ÁREA CENTRAL (Formulario) ---
        JPanel centeringSpace = new JPanel(new GridBagLayout());
        centeringSpace.setOpaque(false);
        
        // Tarjeta redondeada con sombra
        JPanel formCard = new ShadowRoundedPanel(new GridBagLayout());
        formCard.setBackground(COLOR_FORM_BG);
        formCard.setBorder(new EmptyBorder(40, 60, 45, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.insets = new Insets(8, 0, 8, 0);

        // Título "Registrarse"
        JLabel titleLabel = new JLabel("Registrarse");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_TERRACOTA);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(titleLabel, gbc);

        // Inputs con texto fantasma dinámico
        // { changed code } -> declarar y reutilizar campos
        txtNombre = new ModernTextField("Ej. Pedro Pérez");
        addLabelAndField(formCard, "Nombre:", txtNombre, gbc, 1);

        txtCedula = new ModernTextField("Ej. 12345678");
        addLabelAndField(formCard, "Cédula:", txtCedula, gbc, 2);

        txtEmail = new ModernTextField("Ej. ucvista@ciens.ucv.ve");
        addLabelAndField(formCard, "Correo Institucional:", txtEmail, gbc, 3);

        txtContrasena = new ModernTextField("Mín. 6 caracteres");
        addLabelAndField(formCard, "Contraseña:", txtContrasena, gbc, 4);

        // --- SECCIÓN DE RADIO BUTTONS PERSONALIZADOS ---
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        radioPanel.setOpaque(false);
        JRadioButton studentRadio = createCustomRadio("Estudiante");
        JRadioButton employeeRadio = createCustomRadio("Empleado");
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio); group.add(employeeRadio);
        studentRadio.setSelected(true);
        radioPanel.add(studentRadio); radioPanel.add(employeeRadio);
        gbc.gridy = 5; gbc.insets = new Insets(15, 0, 15, 0);
        formCard.add(radioPanel, gbc);

        // Panel Dinámico (CardLayout)
        cardLayout = new CardLayout();
        specificFieldsPanel = new JPanel(cardLayout);
        specificFieldsPanel.setOpaque(false);

        // Panel Estudiante
        JPanel pEst = new JPanel(new GridBagLayout()); pEst.setOpaque(false);
        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.fill = GridBagConstraints.HORIZONTAL; gbcSub.weightx = 1.0; gbcSub.gridx = 0;
        // { changed code } -> campos estudiante
        txtFacultad = new ModernTextField("Ej. Ciencias");
        addLabelAndField(pEst, "Facultad:", txtFacultad, gbcSub, 0);
        txtCarrera = new ModernTextField("Ej. Computación");
        addLabelAndField(pEst, "Carrera:", txtCarrera, gbcSub, 1);

        // Panel Empleado
        JPanel pEmp = new JPanel(new GridBagLayout()); pEmp.setOpaque(false);
        txtCargo = new ModernTextField("Ej. Profesor");
        addLabelAndField(pEmp, "Cargo:", txtCargo, gbcSub, 0);
        txtDepartamento = new ModernTextField("Ej. Docencia");
        addLabelAndField(pEmp, "Departamento:", txtDepartamento, gbcSub, 1);

        specificFieldsPanel.add(pEst, "Estudiante");
        specificFieldsPanel.add(pEmp, "Empleado");
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 25, 0);
        formCard.add(specificFieldsPanel, gbc);

        // --- BOTÓN REGISTRAR (Navega a MainUI) ---
        JButton btnReg = new JButton("REGISTRAR");
        styleButton(btnReg);
        // { changed code } -> listener que construye Usuario y llama al servicio
        btnReg.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String cedula = txtCedula.getText().trim();
            String email = txtEmail.getText().trim();
            String contr = txtContrasena.getText();

            if (nombre.isEmpty() || cedula.isEmpty() || email.isEmpty() || contr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario nuevo = null;
            if (studentRadio.isSelected()) {
                String carrera = txtCarrera.getText().trim();
                String facultad = txtFacultad.getText().trim();
                if (facultad.isEmpty() || carrera.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete Facultad y Carrera para estudiantes.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nuevo = new Estudiante(cedula, nombre, "", email, contr, carrera, facultad);
            } else {
                String cargo = txtCargo.getText().trim();
                String departamento = txtDepartamento.getText().trim();
                if (cargo.isEmpty() || departamento.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete Cargo y Departamento para empleados.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nuevo = new Empleado(cedula, nombre, "", email, contr, cargo, departamento, "0000");
            }

            ServicioRegistro servicio = new ServicioRegistro();
            try {
                servicio.registrarUsuario(nuevo);
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (DuplicateUserException | InvalidEmailFormatException | InvalidCredentialsException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar en disco: " + ex.getMessage(), "Error IO", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        formCard.add(btnReg, gbc);

        // Centrado final
        centeringSpace.add(formCard, new GridBagConstraints());
        gbcMain.gridy = 1; gbcMain.weighty = 1.0; gbcMain.fill = GridBagConstraints.BOTH;
        contentHost.add(centeringSpace, gbcMain);

        JScrollPane scroll = new JScrollPane(contentHost);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        backgroundPanel.add(scroll, BorderLayout.CENTER);

        // Cambio de campos según el Radio seleccionado
        studentRadio.addActionListener(e -> cardLayout.show(specificFieldsPanel, "Estudiante"));
        employeeRadio.addActionListener(e -> cardLayout.show(specificFieldsPanel, "Empleado"));
    }

    // --- COMPONENTES PERSONALIZADOS ---

    /**
     * Campo de texto moderno con texto fantasma que desaparece correctamente.
     */
    private class ModernTextField extends JTextField {
        private String hint;
        public ModernTextField(String h) { 
            this.hint = h; setOpaque(false); setForeground(Color.WHITE); 
            setCaretColor(Color.WHITE); setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            addKeyListener(new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { repaint(); } });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            if (getText().isEmpty()) { // El texto fantasma solo se pinta si no hay texto
                g2.setColor(COLOR_PLACEHOLDER);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(hint, 15, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
            }
            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * Crea un RadioButton sin el borde azul y con iconos personalizados.
     */
    private JRadioButton createCustomRadio(String texto) {
        JRadioButton radio = new JRadioButton(texto);
        radio.setOpaque(false);
        radio.setFocusPainted(false); // ESTO ELIMINA EL RECUADRO AZUL
        radio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radio.setForeground(new Color(60, 40, 30));
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Aplicamos los círculos dibujados manualmente
        radio.setIcon(new CustomRadioIcon(false));
        radio.setSelectedIcon(new CustomRadioIcon(true));
        return radio;
    }

    /**
     * Dibuja los círculos de los RadioButtons al estilo UCV.
     */
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

    private class ShadowRoundedPanel extends JPanel {
        public ShadowRoundedPanel(LayoutManager lm) { super(lm); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 60)); // Sombra
            g2.fillRoundRect(6, 6, getWidth()-6, getHeight()-6, 30, 30);
            g2.setColor(getBackground()); // Fondo crema
            g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 30, 30);
            g2.dispose();
        }
    }

    private void addLabelAndField(JPanel p, String t, JComponent f, GridBagConstraints g, int y) {
        g.gridy = y;
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(60, 40, 30));
        JPanel c = new JPanel(new BorderLayout(0, 5)); c.setOpaque(false);
        c.add(l, BorderLayout.NORTH); c.add(f, BorderLayout.CENTER);
        p.add(c, g);
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(COLOR_BTN_VERDE); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 30, 10, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroFrameUI().setVisible(true));
    }
}