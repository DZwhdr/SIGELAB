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
package com.sigelab.view;

import com.sigelab.model.Usuario;

import javax.swing.*;
import java.awt.*;

// La clase MenuPrincipalView extiende de JFrame para construir la ventana del Dashboard principal
public class MenuPrincipalView extends JFrame {

    // Variable para almacenar los datos del usuario que acaba de iniciar sesión exitosamente
    private Usuario usuarioLogueado;

    // Constructor que recibe al usuario autenticado para personalizar la interfaz según su rol
    public MenuPrincipalView(Usuario usuario) {
        this.usuarioLogueado = usuario;

        // 1. Configuración básica de la ventana principal
        setTitle("SIGELAB - Sistema de Gestión de Laboratorios Universitarios");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla del usuario
        setResizable(false); // Evita que se pueda redimensionar la ventana

        // Panel principal que contendrá todos los componentes visuales
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250)); // Fondo gris claro con estilo moderno

        // -------------------------------------------------------------------------
        // 2. ENCABEZADO SUPERIOR (Barra azul institucional)
        // -------------------------------------------------------------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 850, 70);
        headerPanel.setBackground(new Color(30, 60, 114)); // Azul corporativo
        headerPanel.setLayout(null);

        // Título principal ubicado en el encabezado
        JLabel lblTitulo = new JLabel("SIGELAB - Dashboard Principal");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(25, 20, 400, 30);
        headerPanel.add(lblTitulo);

        // Extracción del rol del usuario usando getNombre() para mostrarlo y validar permisos
        String nombreRol = (usuario != null && usuario.getRol() != null) ? usuario.getRol().getNombre() : "ADMIN";

        // Etiqueta informativa con el nombre completo y el rol actual en sesión
        String nombreUser = (usuario != null && usuario.getNombre() != null) ? usuario.getNombre() : "Administrador";
        String apellidoUser = (usuario != null && usuario.getApellido() != null) ? usuario.getApellido() : "";

        JLabel lblUserInfo = new JLabel("Usuario: " + nombreUser + " " + apellidoUser + " [" + nombreRol + "]");
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUserInfo.setForeground(new Color(200, 220, 255));
        lblUserInfo.setBounds(480, 25, 350, 20);
        headerPanel.add(lblUserInfo);

        panel.add(headerPanel);

        // -------------------------------------------------------------------------
        // 3. PANEL DE MÉTRICAS (Dashboard)
        // -------------------------------------------------------------------------
        JPanel pnlStats = new JPanel();
        pnlStats.setBounds(25, 85, 785, 75);
        pnlStats.setBackground(Color.WHITE);

        // Borde decorativo con título para simular el resumen de inventario en tiempo real
        pnlStats.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200)),
                " Resumen de Inventario en Tiempo Real (Dashboard) ",
                0, 0, new Font("Arial", Font.BOLD, 12), new Color(30, 60, 114)
        ));
        pnlStats.setLayout(new GridLayout(1, 4, 10, 10)); // Estructura de 1 fila x 4 columnas

        // Etiquetas indicadoras de cantidades
        JLabel lblTotal = new JLabel("<html><center><b>Total Equipos</b><br><font color='#1e3c72' size='5'>--</font></center></html>", SwingConstants.CENTER);
        JLabel lblDisponibles = new JLabel("<html><center><b>Disponibles</b><br><font color='#27ae60' size='5'>--</font></center></html>", SwingConstants.CENTER);
        JLabel lblPrestados = new JLabel("<html><center><b>Prestados</b><br><font color='#2980b9' size='5'>--</font></center></html>", SwingConstants.CENTER);
        JLabel lblDanadosMaint = new JLabel("<html><center><b>Dañados / Mant.</b><br><font color='#c0392b' size='5'>--</font></center></html>", SwingConstants.CENTER);

        // Se agregan las métricas al panel superior de estadísticas
        pnlStats.add(lblTotal);
        pnlStats.add(lblDisponibles);
        pnlStats.add(lblPrestados);
        pnlStats.add(lblDanadosMaint);

        panel.add(pnlStats);

        // -------------------------------------------------------------------------
        // 4. BOTONES DE ACCESO A MÓDULOS (Operación diaria del sistema)
        // -------------------------------------------------------------------------

        // Botón 1: Módulo de Préstamos y Código QR
        JButton btnPrestamos = new JButton("📋 Préstamos y Código QR");
        btnPrestamos.setBounds(50, 185, 330, 75);
        btnPrestamos.setBackground(new Color(41, 128, 185)); // Azul claro
        btnPrestamos.setForeground(Color.WHITE);
        btnPrestamos.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnPrestamos);

        // Botón 2: Módulo de Devoluciones y Mantenimiento
        JButton btnDevoluciones = new JButton("🔄 Devoluciones y Mantenimiento");
        btnDevoluciones.setBounds(450, 185, 330, 75);
        btnDevoluciones.setBackground(new Color(39, 174, 96)); // Verde
        btnDevoluciones.setForeground(Color.WHITE);
        btnDevoluciones.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnDevoluciones);

        // Botón 3: Catálogo de Equipos y Laboratorios
        JButton btnEquipos = new JButton("💻 Equipos y Laboratorios");
        btnEquipos.setBounds(50, 285, 330, 75);
        btnEquipos.setBackground(new Color(142, 68, 173)); // Morado
        btnEquipos.setForeground(Color.WHITE);
        btnEquipos.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnEquipos);

        // Botón 4: Reportes y Auditoría del Sistema
        JButton btnReportes = new JButton("📊 Reportes y Auditoría");
        btnReportes.setBounds(450, 285, 330, 75);
        btnReportes.setBackground(new Color(230, 126, 34)); // Naranja
        btnReportes.setForeground(Color.WHITE);
        btnReportes.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnReportes);

        // Botón 5: Gestión de Usuarios (CRUD exclusivo para el Administrador)
        JButton btnUsuarios = new JButton("👥 Gestión de Usuarios (ADMIN)");
        btnUsuarios.setBounds(50, 385, 350, 50);
        btnUsuarios.setBackground(new Color(52, 73, 94)); // Gris oscuro / Azul pizarra
        btnUsuarios.setForeground(Color.WHITE);
        btnUsuarios.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnUsuarios);

        // Botón 6: Configuración del Sistema
        JButton btnConfiguracion = new JButton("⚙️ Configuración del Sistema");
        btnConfiguracion.setBounds(430, 385, 350, 50);
        btnConfiguracion.setBackground(new Color(44, 62, 80)); // Gris azulado oscuro
        btnConfiguracion.setForeground(Color.WHITE);
        btnConfiguracion.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnConfiguracion);

        // -------------------------------------------------------------------------
        // 5. BOTÓN DE CIERRE DE SESIÓN (LOGOUT)
        // -------------------------------------------------------------------------
        JButton btnLogout = new JButton("🚪 Cerrar Sesión");
        btnLogout.setBounds(335, 455, 180, 40);
        btnLogout.setBackground(new Color(192, 57, 43)); // Rojo alerta
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnLogout);

        // -------------------------------------------------------------------------
        // 6. ACCIONES Y VALIDACIONES DE SEGURIDAD (Eventos y control de roles)
        // -------------------------------------------------------------------------

        boolean esAdmin = "ADMIN".equalsIgnoreCase(nombreRol) || "ADMINISTRADOR".equalsIgnoreCase(nombreRol);

        // Evento del botón de Gestión de Usuarios para abrir el registro/configuración biométrica
        btnUsuarios.addActionListener(e -> {
            if (esAdmin) {
                UsuarioManagementView vistaUsuarios = new UsuarioManagementView();
                vistaUsuarios.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Acceso Denegado: Esta función requiere privilegios exclusivos de ADMINISTRADOR.",
                        "Permisos Insuficientes", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Evento del botón de Configuración
        btnConfiguracion.addActionListener(e -> {
            ConfiguracionView vistaConfig = new ConfiguracionView(usuarioLogueado);
            vistaConfig.setVisible(true);
        });

        // Eventos informativos para los demás módulos operativos
        btnPrestamos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Módulo de Préstamos y QR..."));
        btnDevoluciones.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Módulo de Devoluciones y Mantenimiento..."));
        btnEquipos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Catálogo de Equipos y Laboratorios..."));
        btnReportes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo Reportes y Auditoría..."));

        // Evento del botón Cerrar Sesión
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea cerrar la sesión actual?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        add(panel);
    }
}
