package com.comedor.vista.admin;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.comedor.controlador.ServicioMenu;

/**
 * Interfaz gráfica para la administración del Menú del Comedor.
 */
public class MenuAdminUI extends JFrame {

    // --- PALETA DE COLORES ---
    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);
    private static final Color COLOR_OVERLAY = new Color(160, 70, 40, 140);

    private BufferedImage backgroundImage;
    
    // Rutas de imágenes
    private String[] rutasImagenes = new String[4];
    private String[] nombresPlatillos = new String[4];
    private String[] preciosPlatillos = new String[4];

    // Componentes para los platillos
    private JLabel[] labelsImagen = new JLabel[4];
    private JTextField[] fieldsNombre = new JTextField[4];
    private JTextField[] fieldsPrecio = new JTextField[4];

    /**
     * Inicializa la interfaz de administración del menú.
     */
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
    
    /**
     * Inicializa datos de ejemplo para los platillos.
     */
    private void inicializarDatosEjemplo() {
        for (int i = 0; i < 4; i++) {
            rutasImagenes[i] = "/com/comedor/resources/images/menu/base.jpg";
            nombresPlatillos[i] = "Platillo " + (i + 1);
            preciosPlatillos[i] = "Bs. 0,00"; //
        }
    }

    /**
     * Configura propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setTitle("Administración de Menú - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Carga una imagen y la redimensiona.
     */
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
        // Crear placeholder personalizado
        return crearIconoPlaceholder(ancho, alto);
    }
    
    /**
     * Crea un icono de placeholder.
     */
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
        
        // Icono de comida
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
    
    /**
     * Crea una pestaña de navegación.
     */
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
     * Crea un panel para configurar un platillo.
     */
    private JPanel crearPanelPlatillo(int indice) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel para la imagen (220x270)
        JPanel panelImagen = new JPanel(new GridBagLayout());
        panelImagen.setBackground(Color.WHITE);
        panelImagen.setBorder(BorderFactory.createLineBorder(COLOR_TERRACOTA, 2));
        panelImagen.setPreferredSize(new Dimension(220, 270));
        panelImagen.setMinimumSize(new Dimension(220, 270));
        
        // Label para mostrar la imagen
        labelsImagen[indice] = new JLabel();
        labelsImagen[indice].setHorizontalAlignment(SwingConstants.CENTER);
        labelsImagen[indice].setVerticalAlignment(SwingConstants.CENTER);
        
        // Cargar imagen actual
        ImageIcon icono = cargarImagen(rutasImagenes[indice], 216, 266);
        labelsImagen[indice].setIcon(icono);
        
        // Hacer la imagen clickeable para cambiar
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
        
        // Panel para nombre
        JPanel panelNombre = new JPanel(new BorderLayout(2, 2));
        panelNombre.setOpaque(false);
        
        JLabel labelNombre = new JLabel("Nombre Platillo " + (indice + 1) + ":");
        labelNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelNombre.setForeground(Color.WHITE);
        
        fieldsNombre[indice] = new JTextField(nombresPlatillos[indice], 15);
        fieldsNombre[indice].setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsNombre[indice].setBackground(new Color(255, 255, 255, 230));
        fieldsNombre[indice].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TERRACOTA, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsNombre[indice].setPreferredSize(new Dimension(220, 28));
        
        panelNombre.add(labelNombre, BorderLayout.NORTH);
        panelNombre.add(fieldsNombre[indice], BorderLayout.CENTER);
        
        // Panel para precio (NUEVO)
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
            BorderFactory.createLineBorder(new Color(75, 105, 50), 1), // Verde diferente
            new EmptyBorder(4, 8, 4, 8)
        ));
        fieldsPrecio[indice].setPreferredSize(new Dimension(220, 28));
        
        // Placeholder para formato de precio
        fieldsPrecio[indice].setToolTipText("Formato: Bs. 0,00 o 0.00");
        
        panelPrecio.add(labelPrecio, BorderLayout.NORTH);
        panelPrecio.add(fieldsPrecio[indice], BorderLayout.CENTER);
        
        // Agregar nombre y precio al panel de info
        panelInfo.add(panelNombre, BorderLayout.NORTH);
        panelInfo.add(panelPrecio, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        panel.add(panelImagen, BorderLayout.CENTER);
        panel.add(panelInfo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Método para seleccionar una nueva imagen.
     */
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
            // Aquí deberías copiar la imagen a la carpeta de recursos
            // Por ahora solo actualizamos el path temporalmente
            rutasImagenes[indice] = archivo.getAbsolutePath();
            
            // Actualizar la imagen mostrada
            ImageIcon nuevoIcono = new ImageIcon(new ImageIcon(rutasImagenes[indice])
                .getImage().getScaledInstance(216, 266, Image.SCALE_SMOOTH));
            labelsImagen[indice].setIcon(nuevoIcono);
            
            JOptionPane.showMessageDialog(this,
                "Imagen actualizada para Platillo " + (indice + 1),
                "Imagen Actualizada",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Guarda los cambios realizados en los platillos.
     */
    private void guardarCambios() {
        // Validar y actualizar nombres
        for (int i = 0; i < 4; i++) {
            nombresPlatillos[i] = fieldsNombre[i].getText().trim();
            if (nombresPlatillos[i].isEmpty()) {
                nombresPlatillos[i] = "Platillo " + (i + 1);
                fieldsNombre[i].setText(nombresPlatillos[i]);
            }
            
            // Validar y actualizar precios (NUEVO)
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

    /**
     * Construye y organiza los componentes de la interfaz.
     */
    private void initUI() {
        // 1. PANEL DE FONDO con dibujo de barras
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

                // Barras sólidas superior e inferior (135px) - PARTE DEL FONDO
                g2d.setColor(COLOR_TERRACOTA);
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
        
        // Listener para redirigir a MainUserUI
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
                JOptionPane.showMessageDialog(MenuAdminUI.this, 
                    "Funcionalidad no implementada.", 
                    "Gestión de Usuarios", 
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

        historialTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Historial
                // new HistorialUI().setVisible(true);
                // MainAdminUI.this.dispose();
                JOptionPane.showMessageDialog(MenuAdminUI.this, 
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
        
        // Panel para el logo (izquierda)
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

        // Ensamblar barra superior
        topBarContainer.add(logoPanel, BorderLayout.WEST);    // Logo a la izquierda
        topBarContainer.add(tabsContainer, BorderLayout.EAST); // Pestañas a la derecha
        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);
        
        // --- BARRA INFERIOR (solo espacio) ---
        JPanel bottomBarContainer = new JPanel();
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        backgroundPanel.add(bottomBarContainer, BorderLayout.SOUTH);

        // --- CONTENIDO PRINCIPAL CON SCROLL ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Panel principal del contenido que se desplazará
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setOpaque(false);
        
        // Título de administración
        JLabel adminTitle = new JLabel("ADMINISTRACIÓN DE MENÚ", SwingConstants.CENTER);
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        adminTitle.setForeground(Color.WHITE);
        adminTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminTitle.setBorder(new EmptyBorder(40, 0, 20, 0));
        
        // Instrucciones (centrado)
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
        
        // Panel de botones (centrado)
        JPanel botonesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        botonesContainer.setOpaque(false);
        botonesContainer.setMaximumSize(new Dimension(600, 100));
        botonesContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón Guardar
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setBackground(new Color(75, 105, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 40), 2),
            new EmptyBorder(12, 25, 12, 25)
        ));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setBackground(new Color(180, 70, 70));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 50, 50), 2),
            new EmptyBorder(12, 25, 12, 25)
        ));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> {
            // Redirigir a MainAdminUI
            SwingUtilities.invokeLater(() -> {
                new MainAdminUI().setVisible(true);
                MenuAdminUI.this.dispose();
            });
        });
        
        botonesContainer.add(btnGuardar);
        botonesContainer.add(btnCerrar);
        
        // Ensamblar el contenido desplazable
        scrollableContent.add(adminTitle);
        scrollableContent.add(instruccionesPanel);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(platillosContainer);
        scrollableContent.add(Box.createVerticalStrut(40));
        scrollableContent.add(botonesContainer);
        scrollableContent.add(Box.createVerticalStrut(80)); // Espacio extra abajo
        
        // Crear JScrollPane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Personalizar la barra de scroll
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel contenedor para el contenido con márgenes
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