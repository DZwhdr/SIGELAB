package com.sigelab.view;

import com.sigelab.model.Usuario;
import org.example.view.TipoEquipoForm;
import com.sigelab.view.GenerarEquipoQRForm;
import com.sigelab.view.ConsultaEquipoQRForm;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    private final Usuario usuarioLogueado;

    public MenuPrincipalView(Usuario usuario) {
        this.usuarioLogueado = usuario;

        setTitle("SIGELAB - Sistema de Gestión de Laboratorios Universitarios");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 247, 250));

        // ENCABEZADO
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(0, 0, 900, 75);
        headerPanel.setBackground(new Color(30, 60, 114));

        JLabel lblTitulo = new JLabel("SIGELAB - Dashboard Principal");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(25, 20, 430, 35);
        headerPanel.add(lblTitulo);

        String nombreRol = usuario.getRol() != null
                ? usuario.getRol().getNombre()
                : "SIN ROL";

        JLabel lblUserInfo = new JLabel(
                "Usuario: " + usuario.getNombre() + " "
                        + usuario.getApellido() + " [" + nombreRol + "]"
        );
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUserInfo.setForeground(new Color(200, 220, 255));
        lblUserInfo.setBounds(490, 25, 390, 20);
        headerPanel.add(lblUserInfo);

        panel.add(headerPanel);

        // RESUMEN DE INVENTARIO
        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 10, 10));
        pnlStats.setBounds(25, 90, 835, 80);
        pnlStats.setBackground(Color.WHITE);
        pnlStats.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200)),
                " Resumen de Inventario ",
                0,
                0,
                new Font("Arial", Font.BOLD, 12),
                new Color(30, 60, 114)
        ));

        pnlStats.add(crearMetrica("Total Equipos", "#1e3c72"));
        pnlStats.add(crearMetrica("Disponibles", "#27ae60"));
        pnlStats.add(crearMetrica("Prestados", "#2980b9"));
        pnlStats.add(crearMetrica("Dañados / Mant.", "#c0392b"));

        panel.add(pnlStats);

        // MÓDULOS EXISTENTES
        JButton btnPrestamos = crearBoton(
                "Préstamos y Código QR",
                40, 195, 390, 65,
                new Color(41, 128, 185)
        );

        JButton btnDevoluciones = crearBoton(
                "Devoluciones y Mantenimiento",
                460, 195, 390, 65,
                new Color(39, 174, 96)
        );

        JButton btnEquipos = crearBoton(
                "Equipos y Laboratorios",
                40, 275, 390, 65,
                new Color(142, 68, 173)
        );

        JButton btnReportes = crearBoton(
                "Reportes y Auditoría",
                460, 275, 390, 65,
                new Color(230, 126, 34)
        );

        panel.add(btnPrestamos);
        panel.add(btnDevoluciones);
        panel.add(btnEquipos);
        panel.add(btnReportes);

        // TUS MÓDULOS
        JLabel lblMisModulos = new JLabel(
                "Gestión de Tipos de Equipo y Códigos QR"
        );
        lblMisModulos.setFont(new Font("Arial", Font.BOLD, 15));
        lblMisModulos.setForeground(new Color(30, 60, 114));
        lblMisModulos.setBounds(40, 355, 500, 25);
        panel.add(lblMisModulos);

        JButton btnTipos = crearBoton(
                "Tipos de Equipo",
                40, 390, 260, 60,
                new Color(52, 93, 140)
        );

        JButton btnGenerarQR = crearBoton(
                "Generar QR",
                315, 390, 260, 60,
                new Color(38, 125, 110)
        );

        JButton btnEscanearQR = crearBoton(
                "Escanear QR",
                590, 390, 260, 60,
                new Color(83, 98, 160)
        );

        panel.add(btnTipos);
        panel.add(btnGenerarQR);
        panel.add(btnEscanearQR);

        // ADMINISTRACIÓN Y CONFIGURACIÓN
        JButton btnUsuarios = crearBoton(
                "Gestión de Usuarios (ADMIN)",
                40, 480, 390, 55,
                new Color(52, 73, 94)
        );

        JButton btnConfiguracion = crearBoton(
                "Configuración del Sistema",
                460, 480, 390, 55,
                new Color(44, 62, 80)
        );

        panel.add(btnUsuarios);
        panel.add(btnConfiguracion);

        // CERRAR SESIÓN
        JButton btnLogout = crearBoton(
                "Cerrar Sesión",
                345, 580, 200, 45,
                new Color(192, 57, 43)
        );
        panel.add(btnLogout);

        // EVENTOS DE TUS MÓDULOS
        btnTipos.addActionListener(e ->
                new TipoEquipoForm().setVisible(true)
        );

        btnGenerarQR.addActionListener(e ->
                new GenerarEquipoQRForm().setVisible(true)
        );

        btnEscanearQR.addActionListener(e ->
                new ConsultaEquipoQRForm().setVisible(true)
        );

        // EVENTOS EXISTENTES
        boolean esAdmin =
                "ADMIN".equalsIgnoreCase(nombreRol)
                        || "ADMINISTRADOR".equalsIgnoreCase(nombreRol);

        btnUsuarios.addActionListener(e -> {
            if (esAdmin) {
                new UsuarioManagementView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Esta función requiere privilegios de administrador.",
                        "Permisos insuficientes",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        btnConfiguracion.addActionListener(e ->
                new ConfiguracionView(usuarioLogueado).setVisible(true)
        );

        // Se conservan los eventos temporales de tus compañeros.
        btnPrestamos.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Abriendo Módulo de Préstamos y QR..."
                )
        );

        btnDevoluciones.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Abriendo Módulo de Devoluciones y Mantenimiento..."
                )
        );

        btnEquipos.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Abriendo Catálogo de Equipos y Laboratorios..."
                )
        );

        btnReportes.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Abriendo Reportes y Auditoría..."
                )
        );

        btnLogout.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea cerrar la sesión actual?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        setContentPane(panel);
    }

    private JButton crearBoton(
            String texto,
            int x,
            int y,
            int ancho,
            int alto,
            Color color
    ) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, ancho, alto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        return boton;
    }

    private JLabel crearMetrica(String titulo, String color) {
        return new JLabel(
                "<html><center><b>" + titulo + "</b>"
                        + "<br><font color='" + color
                        + "' size='5'>--</font></center></html>",
                SwingConstants.CENTER
        );
    }
}
