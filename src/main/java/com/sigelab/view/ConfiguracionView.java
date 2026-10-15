package com.sigelab.view;

import com.sigelab.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class ConfiguracionView extends JFrame {

    private Usuario usuarioActual;

    public ConfiguracionView(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("SIGELAB - Configuración del Sistema");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        // Encabezado
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 500, 60);
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setLayout(null);

        JLabel lblTitulo = new JLabel("Configuración de Usuario y Seguridad");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(20, 15, 400, 30);
        headerPanel.add(lblTitulo);
        panel.add(headerPanel);

        // Opciones de Configuración
        JButton btnCambiarPass = new JButton("🔒 Cambiar Contraseña");
        btnCambiarPass.setBounds(75, 90, 350, 45);
        btnCambiarPass.setBackground(new Color(41, 128, 185));
        btnCambiarPass.setForeground(Color.WHITE);
        btnCambiarPass.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnCambiarPass);

        JButton btnFacial = new JButton("📷 Configurar Reconocimiento Facial");
        btnFacial.setBounds(75, 155, 350, 45);
        btnFacial.setBackground(new Color(39, 174, 96));
        btnFacial.setForeground(Color.WHITE);
        btnFacial.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnFacial);

        JButton btnPreferencias = new JButton("🎨 Preferencias de Interfaz");
        btnPreferencias.setBounds(75, 220, 350, 45);
        btnPreferencias.setBackground(new Color(142, 68, 173));
        btnPreferencias.setForeground(Color.WHITE);
        btnPreferencias.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnPreferencias);

        // Acciones temporales para las opciones
        btnCambiarPass.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo de cambio de contraseña en desarrollo."));
        btnFacial.addActionListener(e -> JOptionPane.showMessageDialog(this, "Asistente de registro y reconocimiento facial próximamente."));
        btnPreferencias.addActionListener(e -> JOptionPane.showMessageDialog(this, "Configuración de temas y accesos directos."));

        add(panel);
    }
}