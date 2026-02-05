package com.comedor.vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;

public class RegistroFrame extends JFrame {
    private Image backgroundImage;
    private String blurredText = ""; // Texto difuminado en el fondo
    // --- Paneles ---
    private JPanel formPanel;
    private CardLayout cardLayout;
    private JPanel specificFieldsPanel;
    private JPanel studentPanel;
    private JPanel employeePanel;
    // --- Campos ---
    private JTextField nombreField, apellidoField, cedulaField, emailField;
    private JPasswordField passwordField;
    private JTextField codigoEstudianteField, carreraField;
    private JSpinner semestreSpinner;
    private JTextField cargoField, departamentoField;
    private ButtonGroup userTypeGroup;
    private JRadioButton studentRadio, employeeRadio;
    // --- Colores y Fuentes ---
    private static final Color NARANJA_UCV = new Color(204, 85, 0);
    private static final Color GRIS_FONDO_FORM = new Color(255, 250, 240, 230);
    private static final Color CAMPO_FONDO = new Color(139, 69, 19);
    private static final Color BOTON_VERDE = new Color(34, 139, 34);
    private static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_SALIR = new Font("Segoe UI", Font.BOLD, 18);

    public RegistroFrame() {
        // --- Cargar Imagen de Fondo ---
        try {
            URL imageUrl = new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/University_cafeteria.jpg/1280px-University_cafeteria.jpg");
            backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen de fondo.");
            backgroundImage = null;
        }

        // --- Configuración del Frame Principal ---
        setTitle("Registro de Usuario - SAGC");
        setMinimumSize(new Dimension(800, 600));
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Panel de Fondo con Imagen y Texto Difuminado ---
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                // Dibujar texto difuminado (semi-transparente) en el fondo
                if (!blurredText.isEmpty()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(255, 255, 255, 50)); // Blanco semi-transparente (difuminado)
                    g2d.setFont(new Font("Segoe UI", Font.ITALIC, 24));
                    // Posicionar el texto debajo del formulario, centrado
                    int x = (getWidth() - g2d.getFontMetrics().stringWidth(blurredText)) / 2;
                    int y = getHeight() * 3 / 4; // Abajo en la ventana
                    g2d.drawString(blurredText, x, y);
                }
            }
        };
        setContentPane(backgroundPanel);

        // --- Franja Superior Naranja ---
        JPanel topStripe = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topStripe.setBackground(NARANJA_UCV);
        topStripe.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel exitLabel = new JLabel("< SAGC");
        exitLabel.setForeground(Color.WHITE);
        exitLabel.setFont(FONT_SALIR);
        exitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        topStripe.add(exitLabel);
        backgroundPanel.add(topStripe, BorderLayout.NORTH);

        // --- Franja Inferior Naranja ---
        JPanel bottomStripe = new JPanel();
        bottomStripe.setBackground(NARANJA_UCV);
        bottomStripe.setPreferredSize(new Dimension(getWidth(), 20));
        backgroundPanel.add(bottomStripe, BorderLayout.SOUTH);

        // --- Contenedor Central ---
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        backgroundPanel.add(centerContainer, BorderLayout.CENTER);

        // --- Panel del Formulario (más profesional con borde suave) ---
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(GRIS_FONDO_FORM);
        formPanel.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1, true), new EmptyBorder(20, 40, 20, 40))); // Borde suave y padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Título "Registrarse"
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel titleLabel = new JLabel("Registrarse");
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(titleLabel, gbc);

        // Campos del formulario (labels arriba de los fields)
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        addVerticalFormField(formPanel, gbc, 1, "Nombre:", nombreField = createStyledTextField());
        addVerticalFormField(formPanel, gbc, 2, "Apellido:", apellidoField = createStyledTextField());
        addVerticalFormField(formPanel, gbc, 3, "Documento:", cedulaField = createStyledTextField()); // Cambiado a "Documento" como en prototipo
        addVerticalFormField(formPanel, gbc, 4, "Correo electrónico:", emailField = createStyledTextField()); // Cambiado como en prototipo
        addVerticalFormField(formPanel, gbc, 5, "Contraseña:", passwordField = createStyledPasswordField());

        // Tipo de Usuario (Radio Buttons con estilo profesional)
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        userTypePanel.setOpaque(false);
        userTypeGroup = new ButtonGroup();
        studentRadio = new JRadioButton("Estudiante");
        employeeRadio = new JRadioButton("Empleado");
        styleRadioButton(studentRadio);
        styleRadioButton(employeeRadio);
        userTypeGroup.add(studentRadio);
        userTypeGroup.add(employeeRadio);
        studentRadio.setSelected(true);
        userTypePanel.add(studentRadio);
        userTypePanel.add(employeeRadio);
        formPanel.add(userTypePanel, gbc);

        // Paneles Específicos
        cardLayout = new CardLayout();
        specificFieldsPanel = new JPanel(cardLayout);
        specificFieldsPanel.setOpaque(false);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(specificFieldsPanel, gbc);

        // Panel Estudiante
        studentPanel = new JPanel(new GridBagLayout());
        studentPanel.setOpaque(false);
        GridBagConstraints studentGbc = new GridBagConstraints();
        studentGbc.insets = new Insets(8, 5, 8, 5);
        studentGbc.fill = GridBagConstraints.BOTH;
        studentGbc.anchor = GridBagConstraints.WEST;
        addVerticalFormField(studentPanel, studentGbc, 0, "Código Estudiante:", codigoEstudianteField = createStyledTextField());
        addVerticalFormField(studentPanel, studentGbc, 1, "Carrera:", carreraField = createStyledTextField());
        addVerticalFormField(studentPanel, studentGbc, 2, "Semestre:", semestreSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1)));

        // Panel Empleado
        employeePanel = new JPanel(new GridBagLayout());
        employeePanel.setOpaque(false);
        GridBagConstraints employeeGbc = new GridBagConstraints();
        employeeGbc.insets = new Insets(8, 5, 8, 5);
        employeeGbc.fill = GridBagConstraints.BOTH;
        employeeGbc.anchor = GridBagConstraints.WEST;
        addVerticalFormField(employeePanel, employeeGbc, 0, "Cargo:", cargoField = createStyledTextField());
        addVerticalFormField(employeePanel, employeeGbc, 1, "Departamento:", departamentoField = createStyledTextField());

        specificFieldsPanel.add(studentPanel, "Estudiante");
        specificFieldsPanel.add(employeePanel, "Empleado");

        // Botón de Registro
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 8, 5);
        JButton registerButton = new JButton("REGISTRAR");
        styleButton(registerButton, BOTON_VERDE);
        formPanel.add(registerButton, gbc);

        // --- Listeners ---
        studentRadio.addActionListener(e -> {
            cardLayout.show(specificFieldsPanel, "Estudiante");
            blurredText = "Código Estudiante - Carrera - Semestre"; // Texto difuminado para estudiante
            backgroundPanel.repaint(); // Repintar fondo
        });
        employeeRadio.addActionListener(e -> {
            cardLayout.show(specificFieldsPanel, "Empleado");
            blurredText = "Cargo - Departamento"; // Texto difuminado para empleado
            backgroundPanel.repaint();
        });
        registerButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Lógica de registro no implementada.", "En Desarrollo", JOptionPane.INFORMATION_MESSAGE);
        });

        // Añadir formulario al contenedor central
        centerContainer.add(formPanel);

        // Inicializar texto difuminado
        blurredText = "Código Estudiante - Carrera - Semestre";
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FONT_CAMPO);
        field.setBackground(CAMPO_FONDO);
        field.setForeground(Color.WHITE);
        field.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1, true), new EmptyBorder(5, 10, 5, 10))); // Borde profesional
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(FONT_CAMPO);
        field.setBackground(CAMPO_FONDO);
        field.setForeground(Color.WHITE);
        field.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1, true), new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private void styleRadioButton(JRadioButton radio) {
        radio.setFont(FONT_LABEL);
        radio.setOpaque(false);
        radio.setForeground(Color.DARK_GRAY);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addVerticalFormField(JPanel panel, GridBagConstraints gbc, int yPos, String labelText, JComponent field) {
        gbc.gridy = yPos;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        // Panel para field vertical: label arriba, field abajo
        JPanel verticalPanel = new JPanel(new BorderLayout(0, 2)); // Espacio pequeño entre label y field
        verticalPanel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        verticalPanel.add(label, BorderLayout.NORTH);
        if (field instanceof JSpinner) {
            ((JSpinner) field).setFont(FONT_CAMPO);
            ((JSpinner) field).setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1, true), new EmptyBorder(5, 10, 5, 10)));
        }
        verticalPanel.add(field, BorderLayout.CENTER);
        panel.add(verticalPanel, gbc);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(FONT_BOTON);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1, true), new EmptyBorder(8, 20, 8, 20))); // Borde profesional
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new RegistroFrame().setVisible(true));
    }
}