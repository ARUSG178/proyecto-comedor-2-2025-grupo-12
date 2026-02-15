package com.comedor.vista.usuario;

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
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz gráfica para el Menú del Comedor del sistema SAGC UCV.
 */
public class MenuUserUI extends JFrame {

    // --- PALETA DE COLORES (Basada en el diseño institucional) ---
    private static final Color COLOR_AZUL_INST = new Color(0, 51, 102);            // Barras y Títulos
    private static final Color COLOR_OVERLAY = new Color(0, 51, 102, 140);      // Filtro sobre imagen

    private BufferedImage backgroundImage;

    public MenuUserUI() {
        try {
            URL imageUrl = getClass().getResource("/com/comedor/resources/images/registro_e_inicio_sesion/com_reg_bg.jpg");
            if (imageUrl != null) backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Imagen de fondo no encontrada.");
        }
        
        configurarVentana();
        initUI();
    }

    private void configurarVentana() {
        setTitle("Menú del Comedor - SAGC UCV");
        setSize(1400, 950);
        setMinimumSize(new Dimension(900, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia en pantalla completa
    }

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
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(new Color(0, 51, 102, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                g2d.setColor(new Color(100, 100, 100, 200));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                String numero = "PLATO " + numeroPlatillo;
                int x = (getWidth() - fm.stringWidth(numero)) / 2;
                int y = 30;
                g2d.drawString(numero, x, y);
                
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                String icono = "🍽️";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(icono)) / 2;
                y = (getHeight() + fm.getAscent()) / 2 - 10;
                g2d.drawString(icono, x, y);
                
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
        }
        
        // Botón para seleccionar
        JButton btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSeleccionar.setBackground(new Color(0, 60, 120));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel botonPanel = new JPanel(new GridBagLayout());
        botonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        botonPanel.add(btnSeleccionar, gbc);
        
        platilloPanel.add(imagenPanel, BorderLayout.CENTER);
        platilloPanel.add(botonPanel, BorderLayout.SOUTH);
        
        return platilloPanel;
    }


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

        // --- LOGO ESTILIZADO (< SAGC)
        JLabel brandLabel = new JLabel("< SAGC") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(getText(), 3, 43);
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
                new MainUserUI().setVisible(true);
                MenuUserUI.this.dispose();
            }
        });

        brandLabel.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP) {
                    new MainUserUI().setVisible(true);
                    MenuUserUI.this.dispose();
                }
            }
        });

        JPanel topBarContainer = new JPanel();
        topBarContainer.setOpaque(false);
        topBarContainer.setPreferredSize(new Dimension(getWidth(), 135));
        topBarContainer.setLayout(new BoxLayout(topBarContainer, BoxLayout.X_AXIS));

        topBarContainer.add(Box.createRigidArea(new Dimension(20, 0)));

        JPanel verticalCenterPanel = new JPanel();
        verticalCenterPanel.setOpaque(false);
        verticalCenterPanel.setLayout(new BoxLayout(verticalCenterPanel, BoxLayout.Y_AXIS));

        verticalCenterPanel.add(Box.createVerticalGlue());

        verticalCenterPanel.add(brandLabel);

        verticalCenterPanel.add(Box.createVerticalGlue());

        topBarContainer.add(verticalCenterPanel);

        topBarContainer.add(Box.createHorizontalGlue());

        backgroundPanel.add(topBarContainer, BorderLayout.NORTH);

        // --- CONTENIDO CENTRAL CON LOS 4 PLATILLOS EN HORIZONTAL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        centerPanel.add(Box.createVerticalGlue());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel menuTitle = new JLabel("Seleccione su platillo", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(new EmptyBorder(0, 0, 50, 0));
        
        JPanel filaPlatillos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        filaPlatillos.setOpaque(false);
        filaPlatillos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel platillo1 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 1);
        JPanel platillo2 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 2);
        JPanel platillo3 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 3);
        JPanel platillo4 = crearPlatilloPanel("/com/comedor/resources/images/menu/base.jpg", 4);
        
        filaPlatillos.add(platillo1);
        filaPlatillos.add(platillo2);
        filaPlatillos.add(platillo3);
        filaPlatillos.add(platillo4);
        
        contentPanel.add(menuTitle);
        contentPanel.add(filaPlatillos);
        
        centerPanel.add(contentPanel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuUserUI().setVisible(true));
    }
}