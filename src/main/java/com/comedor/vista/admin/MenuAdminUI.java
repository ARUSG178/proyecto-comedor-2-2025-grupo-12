package com.comedor.vista.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz gráfica para la administración del Menú del Comedor.
 */
public class MenuAdminUI extends JFrame {

    // --- PALETA DE COLORES ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);

    private BufferedImage backgroundImage;
    
    // Rutas de imágenes
    private String[] rutasImagenes = new String[4];
    private String[] nombresPlatillos = new String[4];
    private String[] preciosPlatillos = new String[4];

    // Componentes para los platillos
    private JLabel[] labelsImagen = new JLabel[4];
    private JTextField[] fieldsNombre = new JTextField[4];
    private JTextField[] fieldsPrecio = new JTextField[4];

    // Inicializa la interfaz de administración del menú.
    public MenuAdminUI() {
        // Inicializar datos de ejemplo
        inicializarDatosEjemplo();
        
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }
    
    //Inicializa datos de ejemplo para los platillos.
    
    private void inicializarDatosEjemplo() {
        for (int i = 0; i < 4; i++) {
            rutasImagenes[i] = "/com/comedor/resources/images/menu/base.jpg";
            nombresPlatillos[i] = "Platillo " + (i + 1);
            preciosPlatillos[i] = "Bs. 0,00"; //
        }
    }

    private void configurarVentana() {
        setTitle("Administración de Menú - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private ImageIcon cargarImagen(String ruta, int ancho, int alto) {
        try {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                ImageIcon icono = new ImageIcon(url);
                Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(imagen);
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + ruta);
        }
        return crearIconoPlaceholder(ancho, alto);
    }
    
    private ImageIcon crearIconoPlaceholder(int ancho, int alto) {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        
        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ancho, alto);
        
        // Borde
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(0, 0, ancho - 1, alto - 1);
        
        // Emoji de comida
        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String icono = "🍽️";
        int x = (ancho - fm.stringWidth(icono)) / 2;
        int y = (alto - fm.getHeight()) / 2 + fm.getAscent() - 10;
        g2d.drawString(icono, x, y);
        
        // Texto
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.setColor(new Color(120, 120, 120));
        String texto = "Haz clic para cambiar";
        fm = g2d.getFontMetrics();
        x = (ancho - fm.stringWidth(texto)) / 2;
        y = alto - 20;
        g2d.drawString(texto, x, y);
        
        g2d.dispose();
        return new ImageIcon(imagen);
    }
    
    private JLabel createTabLabel(String text) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tab.setForeground(Color.WHITE);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tab.setForeground(new Color(255, 255, 255, 220));
                tab.setFont(new Font("Segoe UI", Font.BOLD, 19));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                tab.setForeground(Color.WHITE);
                tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        });
        
        return tab;
    }

    private JPanel crearPanelPlatillo(int indice) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel para la imagen (220x270)
        JPanel panelImagen = new JPanel(new GridBagLayout());
        panelImagen.setBackground(Color.WHITE);
        panelImagen.setBorder(BorderFactory.createLineBorder(COLOR_AZUL_INST, 2));
        panelImagen.setPreferredSize(new Dimension(220, 270));
        panelImagen.setMinimumSize(new Dimension(220, 270));
        
        // Crear imagenes
        labelsImagen[indice] = new JLabel();
        labelsImagen[indice].setHorizontalAlignment(SwingConstants.CENTER);
        labelsImagen[indice].setVerticalAlignment(SwingConstants.CENTER);
        
        // Cargar imagenes
        ImageIcon icono = cargarImagen(rutasImagenes[indice], 216, 266);
        labelsImagen[indice].setIcon(icono);
        
        // Cambiar imagen
        labelsImagen[indice].setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelsImagen[indice].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarImagen(indice);
            }
        });
        
        // Panel para el nombre y precio
        JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
        panelInfo.setOpaque(false);
        panelInfo.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        // Nombre
        JPanel panelNombre = new JPanel(new BorderLayout(2, 2));
        panelNombre.setOpaque(false);
        
        JLabel labelNombre = new JLabel("Nombre Platillo " + (indice + 1) + ":");
        labelNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelNombre.setForeground(Color.WHITE);
        
        fieldsNombre[indice] = new JTextField(nombresPlatillos[indice], 15);
        fieldsNombre[indice].setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsNombre[indice].setBackground(new Color(255, 255, 255, 230));
        fieldsNombre[indice].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_AZUL_INST, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsNombre[indice].setPreferredSize(new Dimension(220, 28));
        
        panelNombre.add(labelNombre, BorderLayout.NORTH);
        panelNombre.add(fieldsNombre[indice], BorderLayout.CENTER);
        
        // Precio 
        JPanel panelPrecio = new JPanel(new BorderLayout(2, 2));
        panelPrecio.setOpaque(false);
        panelPrecio.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JLabel labelPrecio = new JLabel("Precio Platillo " + (indice + 1) + ":");
        labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelPrecio.setForeground(Color.WHITE);
        
        fieldsPrecio[indice] = new JTextField(preciosPlatillos[indice], 10);
        fieldsPrecio[indice].setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPrecio[indice].setBackground(new Color(255, 255, 255, 230));
        fieldsPrecio[indice].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 60, 120), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsPrecio[indice].setPreferredSize(new Dimension(220, 28));
        
        fieldsPrecio[indice].setToolTipText("Formato: Bs. 0,00 o 0.00");
        
        panelPrecio.add(labelPrecio, BorderLayout.NORTH);
        panelPrecio.add(fieldsPrecio[indice], BorderLayout.CENTER);
        
        panelInfo.add(panelNombre, BorderLayout.NORTH);
        panelInfo.add(panelPrecio, BorderLayout.CENTER);
        
        panel.add(panelImagen, BorderLayout.CENTER);
        panel.add(panelInfo, BorderLayout.SOUTH);
        
        return panel;
    }
    

    private void seleccionarImagen(int indice) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen para Platillo " + (indice + 1));
        
        // Filtro para imágenes
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Imágenes (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            rutasImagenes[indice] = archivo.getAbsolutePath();
            
            ImageIcon nuevoIcono = new ImageIcon(new ImageIcon(rutasImagenes[indice])
                .getImage().getScaledInstance(216, 266, Image.SCALE_SMOOTH));
            labelsImagen[indice].setIcon(nuevoIcono);
            
            JOptionPane.showMessageDialog(this,
                "Imagen actualizada para Platillo " + (indice + 1),
                "Imagen Actualizada",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Guarda cambios para platillos
    private void guardarCambios() {
        // Validar y actualizar nombres
        for (int i = 0; i < 4; i++) {
            nombresPlatillos[i] = fieldsNombre[i].getText().trim();
            if (nombresPlatillos[i].isEmpty()) {
                nombresPlatillos[i] = "Platillo " + (i + 1);
                fieldsNombre[i].setText(nombresPlatillos[i]);
            }
            
            // Validar y actualizar precios
            preciosPlatillos[i] = fieldsPrecio[i].getText().trim();
            if (preciosPlatillos[i].isEmpty()) {
                preciosPlatillos[i] = "Bs. 0,00";
                fieldsPrecio[i].setText(preciosPlatillos[i]);
            } else {
                // Formatear precio si es necesario
                if (!preciosPlatillos[i].startsWith("Bs.")) {
                    preciosPlatillos[i] = "Bs. " + preciosPlatillos[i];
                    fieldsPrecio[i].setText(preciosPlatillos[i]);
                }
            }
        }
        
        // Aquí iría la lógica para guardar en la Base de Datos
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("FUNCIÓN REAL NO IMPLEMENTADA\n\n");
        mensaje.append("✅ Configuración guardada exitosamente.\n\n");
        mensaje.append("Platillos configurados:\n");
        
        for (int i = 0; i < 4; i++) {
            mensaje.append("\n").append(i + 1).append(". ")
                   .append(nombresPlatillos[i])
                   .append(" - ")
                   .append(preciosPlatillos[i]);
        }
        
        JOptionPane.showMessageDialog(this,
            mensaje.toString(),
            "Cambios Guardados",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Construye interfaz
    private void initUI() {
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

                g2d.setColor(COLOR_AZUL_INST);
                int barHeight = 135;
                g2d.fillRect(0, 0, getWidth(), barHeight);
                g2d.fillRect(0, getHeight() - barHeight, getWidth(), barHeight);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // --- BARRA SUPERIOR CON LOGO Y PESTAÑAS ---
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        
        // Logo SAGC | Admin
        JLabel brandLabel = new JLabel("< SAGC | Admin ") {
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
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        brandLabel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MainAdminUI().setVisible(true);
                MenuAdminUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new MainAdminUI().setVisible(true);
                    MenuAdminUI.this.dispose();
                }
            }
        });

        topBarContainer.add(Box.createRigidArea(new Dimension(20, 0)));

        JPanel verticalCenterPanel = new JPanel();
        verticalCenterPanel.setOpaque(false);
        verticalCenterPanel.setLayout(new BoxLayout(verticalCenterPanel, BoxLayout.Y_AXIS));

        verticalCenterPanel.add(Box.createVerticalGlue());

        verticalCenterPanel.add(brandLabel);

        verticalCenterPanel.add(Box.createVerticalGlue());

        topBarContainer.add(verticalCenterPanel);

        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);
        
        JLabel usuarioTab = createTabLabel("Usuario");
        JLabel menuTab = createTabLabel("Visualizar menú");
        JLabel reservasTab = createTabLabel("Reservas");
        
        tabsPanel.add(usuarioTab);
        tabsPanel.add(menuTab);
        tabsPanel.add(reservasTab);

        usuarioTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Usuario
                // new UsuarioUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(MenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Usuario", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new VerMenuAdminUI().setVisible(true);
                MenuAdminUI.this.dispose();
            }
        });

        reservasTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Reservas
                // new ReservasUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(MenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Reservas", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel tabsContainer = new JPanel(new GridBagLayout());
        tabsContainer.setOpaque(false);
        
        GridBagConstraints gbcTabs = new GridBagConstraints();
        gbcTabs.gridx = 0;
        gbcTabs.gridy = 0;
        gbcTabs.weighty = 1.0;
        gbcTabs.anchor = GridBagConstraints.CENTER;
        
        JPanel tabsVerticalCenter = new JPanel(new BorderLayout());
        tabsVerticalCenter.setOpaque(false);
        tabsVerticalCenter.add(tabsPanel, BorderLayout.CENTER);
        
        tabsContainer.add(tabsVerticalCenter, gbcTabs);
        
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(500, 135));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.weighty = 1.0;
        gbcLogo.anchor = GridBagConstraints.WEST;
        gbcLogo.insets = new Insets(0, -31, 0, 0);

        JPanel logoInnerContainer = new JPanel(new GridBagLayout());
        logoInnerContainer.setOpaque(false);

        GridBagConstraints gbcLogoInner = new GridBagConstraints();
        gbcLogoInner.gridx = 0;
        gbcLogoInner.gridy = 0;
        gbcLogoInner.anchor = GridBagConstraints.WEST;

        logoInnerContainer.add(brandLabel, gbcLogoInner);
        logoPanel.add(logoInnerContainer, gbcLogo);

        topBarContainer.add(logoPanel, BorderLayout.WEST);
        topBarContainer.add(tabsContainer, BorderLayout.EAST);
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        // --- BARRA INFERIOR (solo espacio) ---
        JPanel bottomBarContainer = new JPanel();
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);

        // --- CONTENIDO PRINCIPAL CON SCROLL ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setOpaque(false);
        
        JLabel adminTitle = new JLabel("ADMINISTRACIÓN DE MENÚ", SwingConstants.CENTER);
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        adminTitle.setForeground(Color.WHITE);
        adminTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminTitle.setBorder(new EmptyBorder(40, 0, 20, 0));
        
        JPanel instruccionesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        instruccionesPanel.setOpaque(false);
        instruccionesPanel.setMaximumSize(new Dimension(800, 60));
        instruccionesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel para los 4 platillos
        JPanel platillosContainer = new JPanel();
        platillosContainer.setLayout(new BoxLayout(platillosContainer, BoxLayout.X_AXIS));
        platillosContainer.setOpaque(false);
        platillosContainer.setMaximumSize(new Dimension(1100, 400));
        platillosContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Crear los 4 paneles de configuración
        for (int i = 0; i < 4; i++) {
            JPanel panelPlatillo = crearPanelPlatillo(i);
            platillosContainer.add(panelPlatillo);
            
            // Añadir separación entre platillos
            if (i < 3) {
                platillosContainer.add(Box.createHorizontalStrut(20));
            }
        }
        
        // Panel de botones
        JPanel botonesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        botonesContainer.setOpaque(false);
        botonesContainer.setMaximumSize(new Dimension(600, 100));
        botonesContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón Guardar
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setBackground(new Color(0, 60, 120));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 40, 80), 2),
            new EmptyBorder(12, 25, 12, 25)
        ));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setBackground(new Color(80, 80, 80)); // Gris oscuro para cerrar
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
            new EmptyBorder(12, 25, 12, 25)
        ));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new MainAdminUI().setVisible(true);
                MenuAdminUI.this.dispose();
            });
        });
        
        botonesContainer.add(btnGuardar);
        botonesContainer.add(btnCerrar);
        
        scrollableContent.add(adminTitle);
        scrollableContent.add(instruccionesPanel);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(platillosContainer);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(botonesContainer);
        scrollableContent.add(Box.createVerticalStrut(80));
        
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(0, 20, 0, 20));
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        
        backgroundPanel.add(contentWrapper, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdminUI().setVisible(true));
    }
}