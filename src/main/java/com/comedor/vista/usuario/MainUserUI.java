package com.comedor.vista.usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import com.comedor.vista.ISUI;
/**
 * Interfaz gráfica para el registro de usuarios del sistema SAGC UCV.
 */
public class MainUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_TERRACOTA = new Color(160, 70, 40);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(160, 70, 40, 140);      // Filtro sobre imagen

    private BufferedImage backgroundImage;
    // private double saldoActual = 0.0;

    /**
     * Inicializa la interfaz de registro y carga recursos (imagen de fondo).
     */
    public MainUserUI() {
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
        setTitle("Usuario - SAGC UCV");
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
        JLabel brandLabel = new JLabel("SAGC") {
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
        brandLabel.setFocusable(true);

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new ISUI().setVisible(true);
                    MainUserUI.this.dispose();
                }
            }
        });

        // --- PESTAÑAS DE FUNCIONALIDADES ---
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        tabsPanel.setOpaque(false);

        // Crear funcionalidades
        JLabel menuTab = createTabLabel("Menú");
        JLabel reservasTab = createTabLabel("Reservas");
        JLabel historialTab = createTabLabel("Historial");

        // Pestañas de redireccionamiento para las funcionalidades

        menuTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                new MenuUserUI().setVisible(true);
                MainUserUI.this.dispose();
            }
        });

        reservasTab.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                // Redirigir a Reservas
                // new ReservasUI().setVisible(true);
                // MainUserUI.this.dispose();
                JOptionPane.showMessageDialog(MainUserUI.this, 
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
                // MainUserUI.this.dispose();
                JOptionPane.showMessageDialog(MainUserUI.this, 
                    "Funcionalidad no implementada.", 
                    "Historial", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Añadir pestañas al panel
        tabsPanel.add(menuTab);
        tabsPanel.add(reservasTab);
        tabsPanel.add(historialTab);

        // --- CONTENEDOR PRINCIPAL DE LA BARRA SUPERIOR ---
        JPanel topBarContainer = new JPanel(new BorderLayout());
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        // Panel para el logo (izquierda)
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(300, 135));

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.anchor = GridBagConstraints.WEST;
        gbcLogo.insets = new Insets(0, -28, 0, 0);

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

        // --- BARRA INFERIOR CON SALDO ---
        JPanel bottomBarContainer = new JPanel(new BorderLayout());
        bottomBarContainer.setOpaque(false);
        bottomBarContainer.setPreferredSize(new Dimension(getWidth(), 135));

        // Saldo en el lado derecho
        JPanel saldoPanel = new JPanel(new GridBagLayout());
        saldoPanel.setOpaque(false);
        saldoPanel.setPreferredSize(new Dimension(300, 135)); // Ancho fijo para el saldo

        // Icono de monedero (puedes reemplazar con una imagen real)
        JLabel iconoMonedero = new JLabel("💰") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("💰")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("💰", x, y);
                g2.dispose();
            }
        };
        iconoMonedero.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
        iconoMonedero.setForeground(Color.WHITE);

        // Información de saldo
        JPanel infoSaldoPanel = new JPanel(new GridBagLayout());
        infoSaldoPanel.setOpaque(false);

        JLabel saldoLabel = new JLabel("Saldo:");
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saldoLabel.setForeground(new Color(255, 255, 255, 200)); // Blanco semi-transparente

        JLabel montoLabel = new JLabel("Bs. 0,00");
        montoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        montoLabel.setForeground(Color.WHITE);

        JLabel recargarLabel = new JLabel("[Click para recargar]");
        recargarLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        recargarLabel.setForeground(new Color(255, 255, 255, 180));
        recargarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Opción de recargar
        recargarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Lógica funcional para recargar saldo
                // String input = JOptionPane.showInputDialog(
                //     MainUserUI.this,
                //     "Ingrese el monto a recargar (Bs.):\nEj: 100.50 o 100,50",
                //     "Recargar Saldo",
                JOptionPane.showMessageDialog(MainUserUI.this, 
                    "Funcionalidad no implementada.", 
                    "Recargar saldo", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
                //     JOptionPane.QUESTION_MESSAGE
                // );
                
        //         if (input != null && !input.trim().isEmpty()) {
        //             try {
        //                 // Reemplazar coma por punto para parseo
        //                 String inputNormalizado = input.trim().replace(",", ".");
        //                 double montoRecarga = Double.parseDouble(inputNormalizado);
                        
        //                 if (montoRecarga > 0 && montoRecarga <= 10000) { // Límite de 10,000 Bs.
        //                     // Actualizar la variable de saldo
        //                     saldoActual += montoRecarga;
                            
        //                     // Actualizar el label visual
        //                     actualizarDisplaySaldo();
                            
        //                     // Mostrar confirmación
        //                     String montoFormateado = formatSaldo(montoRecarga);
        //                     String nuevoSaldoFormateado = formatSaldo(saldoActual);
                            
        //                     JOptionPane.showMessageDialog(
        //                         MainUserUI.this,
        //                         "¡Recarga exitosa!\n\n" +
        //                         "Monto recargado: " + montoFormateado + "\n" +
        //                         "Nuevo saldo: " + nuevoSaldoFormateado,
        //                         "Recarga Exitosa",
        //                         JOptionPane.INFORMATION_MESSAGE
        //                     );
                            
        //                 } else if (montoRecarga <= 0) {
        //                     mostrarError("Ingrese un monto mayor a cero.");
        //                 } else {
        //                     mostrarError("El monto máximo de recarga es Bs. 10.000,00");
        //                 }
        //             } catch (NumberFormatException ex) {
        //                 mostrarError("Formato inválido. Use números.\nEj: 100.50 o 100,50");
        //             }
        //         }
        //     }
            
        //     private void actualizarDisplaySaldo() {
        //         montoLabel.setText(formatSaldo(saldoActual));
        //     }
            
        //     private String formatSaldo(double monto) {
        //         // Formato: "Bs. 1.250,00"
        //         return String.format("Bs. %,.2f", monto)
        //             .replace(",", "X")
        //             .replace(".", ",")
        //             .replace("X", ".");
        //     }
            
        //     private void mostrarError(String mensaje) {
        //         JOptionPane.showMessageDialog(
        //             MainUserUI.this,
        //             mensaje,
        //             "Error",
        //             JOptionPane.ERROR_MESSAGE
        //         );
        //     }
            
        //     @Override
        //     public void mouseEntered(MouseEvent e) {
        //         recargarLabel.setForeground(Color.WHITE);
        //         recargarLabel.setText("<html><u>Click para recargar</u></html>");
        //     }
            
        //     @Override
        //     public void mouseExited(MouseEvent e) {
        //         recargarLabel.setForeground(new Color(255, 255, 255, 180));
        //         recargarLabel.setText("[Click para recargar]");
        //     }
        // });

        // Organizar componentes del saldo
        GridBagConstraints gbcSaldo = new GridBagConstraints();
        gbcSaldo.gridx = 0;
        gbcSaldo.gridy = 0;
        gbcSaldo.anchor = GridBagConstraints.WEST;
        gbcSaldo.insets = new Insets(0, 0, 5, 0);
        infoSaldoPanel.add(saldoLabel, gbcSaldo);

        gbcSaldo.gridy = 1;
        gbcSaldo.insets = new Insets(0, 0, 5, 0);
        infoSaldoPanel.add(montoLabel, gbcSaldo);

        gbcSaldo.gridy = 2;
        gbcSaldo.insets = new Insets(0, 0, 0, 0);
        infoSaldoPanel.add(recargarLabel, gbcSaldo);

        // Contenedor para centrar verticalmente todo el bloque de saldo
        JPanel saldoVerticalCenter = new JPanel(new GridBagLayout());
        saldoVerticalCenter.setOpaque(false);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 0;
        gbcCenter.anchor = GridBagConstraints.CENTER;

        // Panel que contiene icono + información
        JPanel saldoCompletoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        saldoCompletoPanel.setOpaque(false);
        saldoCompletoPanel.add(iconoMonedero);
        saldoCompletoPanel.add(infoSaldoPanel);

        saldoVerticalCenter.add(saldoCompletoPanel, gbcCenter);

        // Añadir margen derecho
        saldoPanel.setBorder(new EmptyBorder(0, 0, 0, 40));
        saldoPanel.add(saldoVerticalCenter);

        // Añadir el panel de saldo a la derecha de la barra inferior
        bottomBarContainer.add(saldoPanel, BorderLayout.EAST);

        // Añadir barra inferior al backgroundPanel
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
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUserUI().setVisible(true));
    };

};