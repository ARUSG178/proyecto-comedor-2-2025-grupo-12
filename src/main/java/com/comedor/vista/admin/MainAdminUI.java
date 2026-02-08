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
 * Interfaz gráfica para el registro de usuarios del sistema SAGC UCV.
 */
public class MainAdminUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(160, 70, 40, 140);      // Filtro sobre imagen

    private BufferedImage backgroundImage;

    /**
     * Inicializa la interfaz de registro y carga recursos (imagen de fondo).
     */
    public MainAdminUI() {
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
     * Configura propiedades básicas de la ventana de registro.
     */
    private void configurarVentana() {
        setTitle("Admin - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana al abrirse
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
    }

    /**
     * Construye y organiza los componentes del formulario de registro.
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

        // 2. CONTENEDOR DE CONTENIDO (GridBagLayout para el centrado)
        JPanel contentHost = new JPanel(new GridBagLayout());
        contentHost.setOpaque(false);

        // --- LOGO ESTILIZADO (SAGC) Y PESTAÑAS DE NAVEGACIÓN ---
        JLabel brandLabel = new JLabel("Admin - SAGC UCV") {
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
        
        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);

        // Crear funcionalidades
        JLabel usuarioTab = createTabLabel("Usuario");
        JLabel menuTab = createTabLabel("Editar Menú");
        JLabel reservasTab = createTabLabel("Reservas");
        JLabel historialTab = createTabLabel("Historial");

        // Pestañas de redireccionamiento para las funcionalidades
        usuarioTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Usuario 
                // new HistorialUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(MainAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Gestión de Usuarios", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        menuTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MenuAdminUI().setVisible(true);
                MainAdminUI.this.dispose();
            }
        });

        reservasTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Reservas
                // new ReservasUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(MainAdminUI.this, 
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
                JOptionPane.showMessageDialog(MainAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Historial", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Añadir pestañas al panel
        tabsPanel.add(usuarioTab);
        tabsPanel.add(menuTab);
        tabsPanel.add(reservasTab);
        tabsPanel.add(historialTab);

        // --- BARRA INFERIOR ---
        JPanel bottomBarContainer = new JPanel(new BorderLayout());
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        
        // --- CONTENEDOR PRINCIPAL DE LA BARRA SUPERIOR ---
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        // Panel para el logo (izquierda)
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(500, 135));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.anchor = GridBagConstraints.WEST;
        gbcLogo.insets = new Insets(0, -31, 0, 0);

        // Contenedor para centrar verticalmente el logo
        JPanel logoVerticalCenter = new JPanel(new GridBagLayout());
        logoVerticalCenter.setOpaque(false);
        GridBagConstraints gbcLogoCenter = new GridBagConstraints();
        gbcLogoCenter.gridx = 0;
        gbcLogoCenter.gridy = 0;
        gbcLogoCenter.weighty = 1.0;
        gbcLogoCenter.anchor = GridBagConstraints.CENTER;
        logoVerticalCenter.add(brandLabel, gbcLogoCenter);

        logoPanel.add(logoVerticalCenter, gbcLogo);

        // Panel para las pestañas (derecha)
        JPanel tabsContainer = new JPanel(new GridBagLayout());
        tabsContainer.setOpaque(false);

        GridBagConstraints gbcTabs = new GridBagConstraints();
        gbcTabs.gridx = 0;
        gbcTabs.gridy = 0;
        gbcTabs.weighty = 1.0;
        gbcTabs.anchor = GridBagConstraints.CENTER;

        // Contenedor para centrar verticalmente las pestañas
        JPanel tabsVerticalCenter = new JPanel(new BorderLayout());
        tabsVerticalCenter.setOpaque(false);
        tabsVerticalCenter.add(tabsPanel, BorderLayout.CENTER);

        tabsContainer.add(tabsVerticalCenter, gbcTabs);

        // Añadir logo a la izquierda y pestañas a la derecha
        topBarContainer.add(logoPanel, BorderLayout.WEST);
        topBarContainer.add(tabsContainer, BorderLayout.EAST);

        // Agregar el contenedor directamente a la parte norte del backgroundPanel
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        // --- MENSAJE DE BIENVENIDA ---
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setOpaque(false);
        
        // Panel contenedor con márgenes y posible fondo
        JPanel welcomeContainer = new JPanel(new GridBagLayout());
        welcomeContainer.setOpaque(false);
        welcomeContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // Título principal
        JLabel welcomeTitle = new JLabel("<html><div style='text-align: center;'>"
            + "Panel de Administración<br>"
            + "Sistema de Asignación y Gestión del Comedor"
            + "</div></html>");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeTitle.setForeground(Color.WHITE);
        
        // Panel para el mensaje de bienvenida
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(20, 0, 0, 0));
    
        JLabel iconLabel = new JLabel("🔧") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("🔧")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("🔧", x, y);
                g2.dispose();
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setPreferredSize(new Dimension(100, 100));

        
        // Configurar GridBagConstraints para centrar todo
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Emoji 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        welcomeContainer.add(iconLabel, gbc);
        
        // Título
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        welcomeContainer.add(welcomeTitle, gbc);
        
        // Mensaje
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        welcomeContainer.add(messagePanel, gbc);
        
        // Añadir el contenedor al panel de bienvenida
        welcomePanel.add(welcomeContainer);
        
        // Añadir el panel de bienvenida al centro del backgroundPanel
        backgroundPanel.add(welcomePanel, BorderLayout.CENTER);

        // // Añadir barra inferior al backgroundPanel
        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);
    };

    private JLabel createTabLabel(String text) {
    JLabel tab = new JLabel(text);
    tab.setFont(new Font("Segoe UI", Font.BOLD, 18));
    tab.setForeground(Color.WHITE);
    tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Efecto hover
    tab.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            tab.setForeground(new Color(255, 255, 255, 220)); // Más opaco
            tab.setFont(new Font("Segoe UI", Font.BOLD, 19)); // Ligero aumento
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            tab.setForeground(Color.WHITE); // Blanco normal
            tab.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Tamaño normal
        }
    });
    
    return tab;
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainAdminUI().setVisible(true));
    };

};