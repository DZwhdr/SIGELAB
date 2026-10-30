package com.sigelab.view;

import com.sigelab.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPrincipalView extends JFrame {

    private final Usuario usuarioLogueado;
    private boolean modoOscuro;

    private JPanel centerWrapper;
    private JPanel pnlContenido;
    private JPanel headerPanel;
    private JPanel pnlStats;
    private JLabel lblTitulo;
    private JLabel lblUserInfo;
    private JLabel lblAvatarHeader;

    public MenuPrincipalView(Usuario usuario, boolean modoOscuro) {
        this.usuarioLogueado = usuario;
        this.modoOscuro = modoOscuro;

        setTitle("SIGELAB - Sistema de Gestión de Laboratorios Universitarios");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout());

        // Encabezado principal del dashboard
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        lblTitulo = new JLabel("SIGELAB - Dashboard Principal");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JPanel pnlUserHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlUserHeader.setOpaque(false);

        String nombreRol = usuario.getRol() != null
                ? usuario.getRol().getNombre()
                : "SIN ROL";

        lblUserInfo = new JLabel(
                "<html><div style='text-align: right;'>Usuario: <b>" + usuario.getNombre() + " "
                        + usuario.getApellido() + "</b> [" + nombreRol + "]</div></html>"
        );
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        pnlUserHeader.add(lblUserInfo);

        // Avatar en el header para mostrar la foto única del usuario
        lblAvatarHeader = new JLabel();
        lblAvatarHeader.setPreferredSize(new Dimension(40, 40));
        cargarFotoAvatarHeader();
        pnlUserHeader.add(lblAvatarHeader);

        headerPanel.add(pnlUserHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Contenedor central y tarjetas de métricas
        centerWrapper = new JPanel(new GridBagLayout());

        pnlContenido = new JPanel();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));

        pnlStats = new JPanel(new GridLayout(1, 4, 10, 10));
        pnlStats.setPreferredSize(new Dimension(835, 80));
        pnlStats.setMaximumSize(new Dimension(835, 80));

        pnlStats.add(crearMetrica("Total Equipos", "#1e3c72"));
        pnlStats.add(crearMetrica("Disponibles", "#27ae60"));
        pnlStats.add(crearMetrica("Prestados", "#2980b9"));
        pnlStats.add(crearMetrica("Dañados / Mant.", "#c0392b"));

        pnlContenido.add(pnlStats);
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 15)));

        pnlContenido.add(crearFilaBotones(
                crearBoton("Préstamos y Código QR", new Color(41, 128, 185)),
                crearBoton("Devoluciones y Mantenimiento", new Color(39, 174, 96))
        ));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlContenido.add(crearFilaBotones(
                crearBoton("Equipos y Laboratorios", new Color(142, 68, 173)),
                crearBoton("Reportes y Auditoría", new Color(230, 126, 34))
        ));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        pnlContenido.add(crearFilaBotones(
                crearBoton("Gestión de Usuarios (ADMIN)", new Color(52, 73, 94)),
                crearBoton("Configuración del Sistema", new Color(44, 62, 80))
        ));
        pnlContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnLogout = crearBoton("Cerrar Sesión", new Color(192, 57, 43));
        btnLogout.setPreferredSize(new Dimension(250, 45));
        btnLogout.setMaximumSize(new Dimension(250, 45));

        JPanel pnlLogout = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlLogout.add(btnLogout);
        pnlContenido.add(pnlLogout);

        centerWrapper.add(pnlContenido);
        add(centerWrapper, BorderLayout.CENTER);

        aplicarTemaGlobal();
    }

    // Cargo la foto usando el ID único del usuario logueado para que no falle con ningún nombre
    private void cargarFotoAvatarHeader() {
        int idUser = (usuarioLogueado != null) ? usuarioLogueado.getIdUsuario() : 0;
        File archivoFoto = new File("uploads/perfiles/usuario_" + idUser + ".jpg");

        if (archivoFoto.exists()) {
            try {
                BufferedImage imgOriginal = ImageIO.read(archivoFoto);
                if (imgOriginal != null) {
                    Image imgEscalada = imgOriginal.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    lblAvatarHeader.setText("");
                    lblAvatarHeader.setIcon(new ImageIcon(imgEscalada));
                    return;
                }
            } catch (IOException e) {
                // Si falla se queda con el icono por defecto
            }
        }
        lblAvatarHeader.setText("👤");
        lblAvatarHeader.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblAvatarHeader.setForeground(Color.WHITE);
    }

    // Método para refrescar la foto del avatar en el menú al cerrar la configuración
    public void refrescarAvatar() {
        cargarFotoAvatarHeader();
    }

    // Cambiar colores dinámicamente según el modo oscuro o claro
    private void aplicarTemaGlobal() {
        if (modoOscuro) {
            Color fondoOscuro = new Color(20, 24, 33);
            Color headerOscuro = new Color(30, 36, 48);
            Color tarjetaOscura = new Color(30, 36, 48);

            centerWrapper.setBackground(fondoOscuro);
            pnlContenido.setBackground(fondoOscuro);
            headerPanel.setBackground(headerOscuro);

            lblTitulo.setForeground(new Color(220, 230, 242));
            lblUserInfo.setForeground(new Color(255, 215, 0));

            pnlStats.setBackground(tarjetaOscura);
            pnlStats.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 95)),
                    " Resumen de Inventario ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(100, 180, 255)
            ));

            Component[] comps = pnlContenido.getComponents();
            for (Component comp : comps) {
                if (comp instanceof JPanel) {
                    comp.setBackground(fondoOscuro);
                }
            }
        } else {
            Color fondoClaro = new Color(245, 247, 250);
            Color headerClaro = new Color(30, 60, 114);

            centerWrapper.setBackground(fondoClaro);
            pnlContenido.setBackground(fondoClaro);
            headerPanel.setBackground(headerClaro);

            lblTitulo.setForeground(Color.WHITE);
            lblUserInfo.setForeground(new Color(200, 220, 255));

            pnlStats.setBackground(Color.WHITE);
            pnlStats.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(180, 190, 200)),
                    " Resumen de Inventario ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(30, 60, 114)
            ));

            Component[] comps = pnlContenido.getComponents();
            for (Component comp : comps) {
                if (comp instanceof JPanel) {
                    comp.setBackground(fondoClaro);
                }
            }
        }
        repaint();
    }

    private JPanel crearFilaBotones(JButton btn1, JButton btn2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 15, 0));
        fila.setPreferredSize(new Dimension(835, 55));
        fila.setMaximumSize(new Dimension(835, 55));
        fila.add(btn1);
        fila.add(btn2);
        return fila;
    }

    // Manejo de acciones de los botones principales y permisos de admin
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean esAdmin = "ADMIN".equalsIgnoreCase(
                usuarioLogueado.getRol() != null ? usuarioLogueado.getRol().getNombre() : ""
        ) || "ADMINISTRADOR".equalsIgnoreCase(
                usuarioLogueado.getRol() != null ? usuarioLogueado.getRol().getNombre() : ""
        );

        if (texto.equals("Gestión de Usuarios (ADMIN)")) {
            boton.addActionListener(e -> {
                if (esAdmin) {
                    new UsuarioManagementView(modoOscuro).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Esta función requiere privilegios de administrador.",
                            "Permisos insuficientes",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            });
        } else if (texto.equals("Configuración del Sistema")) {
            boton.addActionListener(e -> {
                ConfiguracionView configView = new ConfiguracionView(
                        this,
                        usuarioLogueado,
                        modoOscuro,
                        (nuevoModo) -> {
                            this.modoOscuro = nuevoModo;
                            aplicarTemaGlobal();
                        }
                );
                configView.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        refrescarAvatar();
                    }
                });
                configView.setVisible(true);
            });
        } else if (texto.equals("Préstamos y Código QR")) {
            boton.addActionListener(e -> {
                new EquipoView().setVisible(true);
            });
        } else if (texto.equals("Devoluciones y Mantenimiento")) {
                boton.addActionListener(e -> {
                    JDialog ventana = new JDialog(
                            this,
                            "SIGELAB - Devoluciones y Mantenimiento",
                            true
                    );

                    ventana.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    ventana.setSize(430, 300);
                    ventana.setLocationRelativeTo(this);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(
                            BorderFactory.createEmptyBorder(25, 30, 25, 30)
                    );

                    JLabel titulo = new JLabel(
                            "DEVOLUCIONES Y MANTENIMIENTO",
                            SwingConstants.CENTER
                    );
                    titulo.setFont(new Font("Arial", Font.BOLD, 18));
                    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JButton btnDevoluciones = crearBoton(
                            "Gestión de Devoluciones",
                            new Color(39, 174, 96)
                    );

                    JButton btnMantenimiento = crearBoton(
                            "Gestión de Mantenimiento",
                            new Color(230, 126, 34)
                    );

                    btnDevoluciones.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 60)
                    );
                    btnMantenimiento.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 60)
                    );

                    btnDevoluciones.setAlignmentX(Component.CENTER_ALIGNMENT);
                    btnMantenimiento.setAlignmentX(Component.CENTER_ALIGNMENT);

                    btnDevoluciones.addActionListener(evento -> {
                        ventana.dispose();
                        new DevolucionView().setVisible(true);
                    });

                    btnMantenimiento.addActionListener(eventoMantenimiento -> {
                        ventana.dispose();
                        new MantenimientoView().setVisible(true);
                    });

                    panel.add(titulo);
                    panel.add(Box.createRigidArea(new Dimension(0, 25)));
                    panel.add(btnDevoluciones);
                    panel.add(Box.createRigidArea(new Dimension(0, 12)));
                    panel.add(btnMantenimiento);

                    ventana.add(panel);
                    ventana.setVisible(true);
                });



        } else if (texto.equals("Equipos y Laboratorios")) {
            boton.addActionListener(e -> {
                JDialog ventana = new JDialog(
                        this,
                        "SIGELAB - Equipos y Laboratorios",
                        true
                );

                ventana.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                ventana.setSize(430, 300);
                ventana.setLocationRelativeTo(this);

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(
                        BorderFactory.createEmptyBorder(25, 30, 25, 30)
                );

                JLabel titulo = new JLabel(
                        "EQUIPOS Y LABORATORIOS",
                        SwingConstants.CENTER
                );
                titulo.setFont(new Font("Arial", Font.BOLD, 20));
                titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton btnEquipos = crearBoton(
                        "Gestión de Equipos",
                        new Color(41, 128, 185)
                );

                JButton btnLaboratorios = crearBoton(
                        "Gestión de Laboratorios",
                        new Color(142, 68, 173)
                );

                btnEquipos.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 60)
                );
                btnLaboratorios.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 60)
                );
                btnEquipos.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnLaboratorios.setAlignmentX(Component.CENTER_ALIGNMENT);

                btnEquipos.addActionListener(evento -> {
                    ventana.dispose();
                    new EquipoView(esAdmin).setVisible(true);
                });

                btnLaboratorios.addActionListener(evento -> {
                    ventana.dispose();

                    JFrame ventanaLab = new JFrame(
                            "SIGELAB - Gestión de Laboratorios"
                    );
                    ventanaLab.setDefaultCloseOperation(
                            JFrame.DISPOSE_ON_CLOSE
                    );
                    ventanaLab.setSize(1000, 650);
                    ventanaLab.setLocationRelativeTo(this);
                    ventanaLab.add(new LaboratorioView(esAdmin));
                    ventanaLab.setVisible(true);
                });

                panel.add(titulo);
                panel.add(Box.createRigidArea(new Dimension(0, 25)));
                panel.add(btnEquipos);
                panel.add(Box.createRigidArea(new Dimension(0, 12)));
                panel.add(btnLaboratorios);

                ventana.add(panel);
                ventana.setVisible(true);
            });
        } else if (texto.equals("Reportes y Auditoría")) {
            boton.addActionListener(e -> {
                new ConsultaEquipoQRForm().setVisible(true);
            });
        } else if (texto.equals("Cerrar Sesión")) {
            boton.addActionListener(e -> {
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
        }

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