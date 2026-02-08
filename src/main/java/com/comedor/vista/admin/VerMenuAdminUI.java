package com.comedor.vista.admin;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Interfaz gráfica para el Menú del Comedor del sistema SAGC UCV.
 */
public class VerMenuAdminUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(160, 70, 40, 140);      // Filtro sobre imagen

    private BufferedImage backgroundImage;

    /**
     * Inicializa la interfaz del menú del comedor y carga recursos.
     */
    public VerMenuAdminUI() {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    /**
     * Configura propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setTitle("Vista del Menú del Comedor | Admin - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia en pantalla completa
    }

    /**
     * Crea un panel de platillo individual con solo imagen y botón.
     */
    private JPanel crearPlatilloPanel(String rutaImagen, int numeroPlatillo) {
        JPanel platilloPanel = new JPanel();
        platilloPanel.setLayout(new BorderLayout(0, 15));
        platilloPanel.setOpaque(false);
        
        // Panel para la imagen (250x300)
        JPanel imagenPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Dibujar fondo con borde redondeado
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Dibujar borde decorativo
                g2d.setColor(new Color(160, 70, 40, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                // Dibujar número del platillo
                g2d.setColor(new Color(100, 100, 100, 200));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                String numero = "PLATO " + numeroPlatillo;
                int x = (getWidth() - fm.stringWidth(numero)) / 2;
                int y = 30;
                g2d.drawString(numero, x, y);
                
                // Dibujar icono de placeholder
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                String icono = "🍽️";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(icono)) / 2;
                y = (getHeight() + fm.getAscent()) / 2 - 10;
                g2d.drawString(icono, x, y);
                
                // Texto indicativo
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g2d.setColor(new Color(120, 120, 120, 180));
                String texto = "Imagen 250x300";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(texto)) / 2;
                y = getHeight() - 20;
                g2d.drawString(texto, x, y);
            }
        };
        
        // Tamaño fijo 250x300
        imagenPanel.setPreferredSize(new Dimension(250, 300));
        imagenPanel.setMinimumSize(new Dimension(250, 300));
        imagenPanel.setMaximumSize(new Dimension(250, 300));
        imagenPanel.setBackground(new Color(245, 245, 245));
        
        // Aquí iría la carga de la imagen real cuando esté implementada
        try {
            ImageIcon imagen = new ImageIcon(getClass().getResource(rutaImagen));
            Image scaled = imagen.getImage().getScaledInstance(246, 296, Image.SCALE_SMOOTH);
            JLabel imagenLabel = new JLabel(new ImageIcon(scaled));
            imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagenPanel.add(imagenLabel);
        } catch (Exception e) {
            // Fallback al mensaje dibujado
        }
        
        // Botón para seleccionar
        JButton btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSeleccionar.setBackground(new Color(75, 105, 50));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Panel para centrar el botón
        JPanel botonPanel = new JPanel(new GridBagLayout());
        botonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        botonPanel.add(btnSeleccionar, gbc);
        
        // Agregar componentes al panel principal
        platilloPanel.add(imagenPanel, BorderLayout.CENTER);
        platilloPanel.add(botonPanel, BorderLayout.SOUTH);
        
        return platilloPanel;
    }

    private JLabel createTabLabel(String text) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tab.setForeground(Color.WHITE);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
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

    /**
     * Construye y organiza los componentes del menú.
     */
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

        // --- LOGO ESTILIZADO (< SAGC)
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
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener para redirigir a MainUserUI
        brandLabel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MainAdminUI().setVisible(true);
                VerMenuAdminUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new MainAdminUI().setVisible(true);
                    VerMenuAdminUI.this.dispose();
                }
            }
        });

        // Crear un panel con BoxLayout para control preciso
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        // Margen izquierdo
        topBarContainer.add(Box.createRigidArea(new Dimension(20, 0)));

        // Contenedor interno para centrar verticalmente
        JPanel verticalCenterPanel = new JPanel();
        verticalCenterPanel.setOpaque(false);
        verticalCenterPanel.setLayout(new BoxLayout(verticalCenterPanel, BoxLayout.Y_AXIS));

        // Espacio flexible arriba
        verticalCenterPanel.add(Box.createVerticalGlue());

        // Agregar el logo
        verticalCenterPanel.add(brandLabel);

        // Espacio flexible abajo
        verticalCenterPanel.add(Box.createVerticalGlue());

        // Agregar el contenedor de centrado vertical al contenedor principal
        topBarContainer.add(verticalCenterPanel);

        // Empujar todo hacia la izquierda
        topBarContainer.add(Box.createHorizontalGlue());

        // Agregar el contenedor directamente a la parte norte del backgroundPanel
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);
        
        // Crear pestañas
        JLabel usuarioTab = createTabLabel("Usuario");
        JLabel menuTab = createTabLabel("Visualizar menú");
        JLabel reservasTab = createTabLabel("Reservas");
        JLabel historialTab = createTabLabel("Historial");
        
        // Añadir pestañas al panel
        tabsPanel.add(usuarioTab);
        tabsPanel.add(menuTab);
        tabsPanel.add(reservasTab);
        tabsPanel.add(historialTab);
        
        // Pestañas de redireccionamiento
            // Pestañas de redireccionamiento para las funcionalidades
            usuarioTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Usuario 
                // new HistorialUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(VerMenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Gestión de Usuarios", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });


        reservasTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Reservas
                // new ReservasUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(VerMenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Reservas", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        historialTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Historial
                // new HistorialUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(VerMenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Historial", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Panel para las pestañas (derecha)
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

        // --- CONTENIDO CENTRAL CON LOS 4 PLATILLOS EN HORIZONTAL ---
        // Usamos un JPanel con BoxLayout para mejor control del centrado vertical
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Espacio flexible arriba para empujar el contenido hacia abajo desde la barra superior
        centerPanel.add(Box.createVerticalGlue());
        
        // Panel contenedor principal del contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título del menú
        JLabel menuTitle = new JLabel("Seleccione su platillo", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(new EmptyBorder(0, 0, 50, 0));
        
        // Panel para los 4 platillos en HORIZONTAL
        JPanel filaPlatillos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        filaPlatillos.setOpaque(false);
        filaPlatillos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Crear los 4 platillos
        JPanel platillo1 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 1);
        JPanel platillo2 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 2);
        JPanel platillo3 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 3);
        JPanel platillo4 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 4);
        
        // Añadir los platillos en horizontal
        filaPlatillos.add(platillo1);
        filaPlatillos.add(platillo2);
        filaPlatillos.add(platillo3);
        filaPlatillos.add(platillo4);
        
        // Ensamblar el contentPanel
        contentPanel.add(menuTitle);
        contentPanel.add(filaPlatillos);
        
        // Añadir el contentPanel al centroPanel
        centerPanel.add(contentPanel);

        topBarContainer.add(brandLabel, BorderLayout.WEST);     // Logo a la izquierda
        topBarContainer.add(tabsContainer, BorderLayout.EAST); // Pestañas a la derecha
        
        // 5. Agregar al fondo
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        // Espacio flexible abajo para empujar el contenido hacia arriba desde la barra inferior
        centerPanel.add(Box.createVerticalGlue());
        
        // Añadir el centerPanel al backgroundPanel
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerMenuAdminUI().setVisible(true));
    }
}