package com.sigelab.view;

import com.sigelab.controller.UsuarioController;
import com.sigelab.model.Usuario;
import com.sigelab.service.FacialAuthService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Consumer;

public class ConfiguracionView extends JFrame {

    private final Usuario usuarioActual;
    private boolean modoOscuroActual;
    private final Consumer<Boolean> onThemeChanged;
    private FacialAuthService facialAuthService;
    private UsuarioController usuarioController;

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JTextField txtEmail;
    private JPasswordField txtPassActual;
    private JPasswordField txtPassNueva;
    private JCheckBox chkModoOscuro;
    private JCheckBox chkSonidos;
    private JLabel lblEstadoRostro;
    private JButton btnCapturarRostro;
    private JLabel lblAvatarIcon;

    public ConfiguracionView(JFrame parent, Usuario usuario, boolean modoOscuro, Consumer<Boolean> onThemeChanged) {
        this.usuarioActual = usuario;
        this.modoOscuroActual = modoOscuro;
        this.onThemeChanged = onThemeChanged;
        this.facialAuthService = new FacialAuthService();
        this.usuarioController = new UsuarioController();

        setTitle("SIGELAB - Panel de Control y Configuración");
        setSize(620, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        aplicarTemaConfiguracion();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Pestaña 1: Mi perfil y foto
        JPanel pnlPerfil = new JPanel(null);
        pnlPerfil.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tarjeta de arriba con la info del usuario
        JPanel pnlCardId = new JPanel(null);
        pnlCardId.setBounds(20, 15, 550, 115);
        pnlCardId.setBackground(new Color(30, 90, 150));
        pnlCardId.setBorder(BorderFactory.createLineBorder(new Color(20, 70, 120), 1, true));

        // Aquí puse el avatar para que al hacer click abra el explorador y cambie la foto de una vez
        lblAvatarIcon = new JLabel("👤", SwingConstants.CENTER);
        lblAvatarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblAvatarIcon.setBounds(15, 20, 75, 75);
        lblAvatarIcon.setForeground(Color.WHITE);
        lblAvatarIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblAvatarIcon.setToolTipText("Haz clic para cambiar tu foto");

        cargarFotoPerfilGuardada();

        lblAvatarIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarYGuardarFotoPerfil();
            }
        });
        pnlCardId.add(lblAvatarIcon);

        String nombreCompleto = (usuarioActual != null) ? usuarioActual.getNombre() + " " + usuarioActual.getApellido() : "Usuario SIGELAB";
        JLabel lblNombreCard = new JLabel(nombreCompleto);
        lblNombreCard.setFont(new Font("Arial", Font.BOLD, 16));
        lblNombreCard.setForeground(Color.WHITE);
        lblNombreCard.setBounds(105, 15, 425, 24);
        pnlCardId.add(lblNombreCard);

        String rolStr = (usuarioActual != null && usuarioActual.getRol() != null) ? usuarioActual.getRol().getNombre() : "ESTÁNDAR";
        JLabel lblRolCard = new JLabel("Rol del Sistema: " + rolStr + "  |  Estado: Activo 🟢");
        lblRolCard.setFont(new Font("Arial", Font.BOLD, 12));
        lblRolCard.setForeground(new Color(220, 235, 255));
        lblRolCard.setBounds(105, 42, 425, 20);
        pnlCardId.add(lblRolCard);

        JLabel lblDeptoCard = new JLabel("<html>Departamento: Seguridad y Gestión de Riesgos / Lab TI<br><font color='#b0e0ff'><b>💡 Haz clic sobre tu foto para cambiarla</b></font></html>");
        lblDeptoCard.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDeptoCard.setForeground(Color.WHITE);
        lblDeptoCard.setBounds(105, 65, 425, 38);
        pnlCardId.add(lblDeptoCard);

        pnlPerfil.add(pnlCardId);

        JLabel lblEmailTitulo = new JLabel("Gestión de Contacto y Notificaciones");
        lblEmailTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblEmailTitulo.setBounds(20, 145, 300, 20);
        pnlPerfil.add(lblEmailTitulo);

        JLabel lblEmail = new JLabel("Correo Electrónico Corporativo:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEmail.setBounds(20, 172, 250, 20);
        pnlPerfil.add(lblEmail);

        String correoPorDefecto = (usuarioActual != null && usuarioActual.getUsername() != null)
                ? usuarioActual.getUsername() + "@sigelab.com"
                : "keller.cameros@sigelab.com";

        txtEmail = new JTextField(correoPorDefecto);
        txtEmail.setBounds(20, 197, 380, 38);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 13));
        pnlPerfil.add(txtEmail);

        JButton btnGuardarEmail = new JButton("Actualizar Correo");
        btnGuardarEmail.setBounds(410, 197, 160, 38);
        btnGuardarEmail.setBackground(new Color(39, 174, 96));
        btnGuardarEmail.setForeground(Color.WHITE);
        btnGuardarEmail.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardarEmail.setFocusPainted(false);
        btnGuardarEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardarEmail.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Correo actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        pnlPerfil.add(btnGuardarEmail);

        // Cuadrito abajo con datos extra de la sesión
        JPanel pnlInfoExtra = new JPanel(null);
        pnlInfoExtra.setBounds(20, 250, 550, 115);
        pnlInfoExtra.setBackground(new Color(235, 240, 245));
        pnlInfoExtra.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 230), 1, true));

        JLabel lblInfoTitle = new JLabel("ℹ️ Info de sesión y seguridad");
        lblInfoTitle.setFont(new Font("Arial", Font.BOLD, 11));
        lblInfoTitle.setBounds(12, 10, 300, 18);
        pnlInfoExtra.add(lblInfoTitle);

        JLabel lblInfoBody1 = new JLabel("• Protocolo: ISO 8583 / Biometría 2FA activa.");
        lblInfoBody1.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfoBody1.setBounds(15, 35, 520, 18);
        pnlInfoExtra.add(lblInfoBody1);

        JLabel lblInfoBody2 = new JLabel("• Último inicio de sesión desde estación principal.");
        lblInfoBody2.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfoBody2.setBounds(15, 58, 520, 18);
        pnlInfoExtra.add(lblInfoBody2);

        JLabel lblInfoBody3 = new JLabel("• Nivel de cifrado: AES-256 bits.");
        lblInfoBody3.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfoBody3.setBounds(15, 81, 520, 18);
        pnlInfoExtra.add(lblInfoBody3);

        pnlPerfil.add(pnlInfoExtra);

        tabbedPane.addTab("👤 Perfil Corporativo", pnlPerfil);

        // Pestaña 2: Seguridad y pass
        JPanel pnlSeguridad = new JPanel(null);
        pnlSeguridad.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblBioTitulo = new JLabel("Reconocimiento Facial (2FA)");
        lblBioTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblBioTitulo.setBounds(20, 15, 350, 20);
        pnlSeguridad.add(lblBioTitulo);

        lblEstadoRostro = new JLabel("Estado: Patrón biométrico activo en la base de datos.");
        lblEstadoRostro.setFont(new Font("Arial", Font.ITALIC, 11));
        lblEstadoRostro.setBounds(20, 38, 540, 20);
        pnlSeguridad.add(lblEstadoRostro);

        btnCapturarRostro = new JButton("📷 Registrar / Actualizar Rostro");
        btnCapturarRostro.setBounds(20, 65, 550, 42);
        btnCapturarRostro.setBackground(new Color(41, 128, 185));
        btnCapturarRostro.setForeground(Color.WHITE);
        btnCapturarRostro.setFont(new Font("Arial", Font.BOLD, 12));
        btnCapturarRostro.setFocusPainted(false);
        btnCapturarRostro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCapturarRostro.addActionListener(e -> registrarRostroPropio());
        pnlSeguridad.add(btnCapturarRostro);

        JSeparator sep = new JSeparator();
        sep.setBounds(20, 125, 550, 10);
        pnlSeguridad.add(sep);

        JLabel lblPassTitulo = new JLabel("Cambio de Contraseña");
        lblPassTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassTitulo.setBounds(20, 145, 300, 20);
        pnlSeguridad.add(lblPassTitulo);

        JLabel lblPassAct = new JLabel("Contraseña Actual:");
        lblPassAct.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPassAct.setBounds(20, 180, 150, 25);
        pnlSeguridad.add(lblPassAct);
        txtPassActual = new JPasswordField();
        txtPassActual.setBounds(170, 175, 250, 35);
        pnlSeguridad.add(txtPassActual);

        JLabel lblPassNew = new JLabel("Nueva Contraseña:");
        lblPassNew.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPassNew.setBounds(20, 225, 150, 25);
        pnlSeguridad.add(lblPassNew);
        txtPassNueva = new JPasswordField();
        txtPassNueva.setBounds(170, 220, 250, 35);
        pnlSeguridad.add(txtPassNueva);

        JButton btnGuardarPass = new JButton("Actualizar Credencial");
        btnGuardarPass.setBounds(430, 220, 140, 35);
        btnGuardarPass.setBackground(new Color(39, 174, 96));
        btnGuardarPass.setForeground(Color.WHITE);
        btnGuardarPass.setFont(new Font("Arial", Font.BOLD, 11));
        btnGuardarPass.setFocusPainted(false);
        btnGuardarPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Aquí conecté el cambio de pass directo a la BD para que la vieja ya no sirva
        btnGuardarPass.addActionListener(e -> {
            String passActual = new String(txtPassActual.getPassword());
            String passNueva = new String(txtPassNueva.getPassword());

            if (passActual.isEmpty() || passNueva.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Llena ambos campos de contraseña porfa.",
                        "Campos Vacíos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = (usuarioActual != null) ? usuarioActual.getUsername() : "";

            // Primero valido que la pass actual sea correcta
            Usuario validacion = usuarioController.procesarLogin(username, passActual);
            if (validacion == null) {
                JOptionPane.showMessageDialog(this,
                        "La contraseña actual no coincide.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtPassActual.setText("");
                return;
            }

            // Si todo bien, la actualizo en la BD
            boolean actualizado = usuarioController.actualizarPassword(username, passNueva);
            if (actualizado) {
                JOptionPane.showMessageDialog(this,
                        "¡Contraseña cambiada con éxito en la base de datos!",
                        "Listo",
                        JOptionPane.INFORMATION_MESSAGE);
                txtPassActual.setText("");
                txtPassNueva.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Falló al actualizar la contraseña en la BD.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        pnlSeguridad.add(btnGuardarPass);

        tabbedPane.addTab("🔒 Seguridad y 2FA", pnlSeguridad);

        // Pestaña 3: Preferencias y modo oscuro
        JPanel pnlPreferencias = new JPanel(null);
        pnlPreferencias.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblPrefTitulo = new JLabel("Ajustes del Sistema");
        lblPrefTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblPrefTitulo.setBounds(20, 20, 400, 20);
        pnlPreferencias.add(lblPrefTitulo);

        chkModoOscuro = new JCheckBox("Activar Modo Oscuro", modoOscuroActual);
        chkModoOscuro.setFont(new Font("Arial", Font.BOLD, 13));
        chkModoOscuro.setBounds(20, 60, 450, 30);
        chkModoOscuro.setFocusPainted(false);
        chkModoOscuro.addActionListener(e -> {
            modoOscuroActual = chkModoOscuro.isSelected();
            aplicarTemaConfiguracion();
            if (onThemeChanged != null) {
                onThemeChanged.accept(modoOscuroActual);
            }
        });
        pnlPreferencias.add(chkModoOscuro);

        chkSonidos = new JCheckBox("Habilitar efectos de sonido", true);
        chkSonidos.setFont(new Font("Arial", Font.PLAIN, 13));
        chkSonidos.setBounds(20, 110, 450, 30);
        chkSonidos.setFocusPainted(false);
        pnlPreferencias.add(chkSonidos);

        tabbedPane.addTab("⚙️ Preferencias", pnlPreferencias);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotones.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());
        pnlBotones.add(btnCerrar);

        mainPanel.add(pnlBotones, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // Ruta basada en el ID único del usuario para que cada uno tenga su propia foto sin importar el nombre
    private String obtenerRutaArchivoFoto() {
        int idUser = (usuarioActual != null) ? usuarioActual.getIdUsuario() : 0;
        File dir = new File("uploads/perfiles");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return "uploads/perfiles/usuario_" + idUser + ".jpg";
    }

    // Cargo la foto si ya existe guardada localmente
    private void cargarFotoPerfilGuardada() {
        File archivoFoto = new File(obtenerRutaArchivoFoto());
        if (archivoFoto.exists()) {
            try {
                BufferedImage imgOriginal = ImageIO.read(archivoFoto);
                if (imgOriginal != null) {
                    Image imgEscalada = imgOriginal.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                    lblAvatarIcon.setText("");
                    lblAvatarIcon.setIcon(new ImageIcon(imgEscalada));
                }
            } catch (IOException e) {
                // Si falla se queda con el icono por defecto
            }
        }
    }

    // Abre el explorador para elegir la foto y la guarda vinculada al ID
    private void seleccionarYGuardarFotoPerfil() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Elige tu foto de perfil");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                File archivoDestino = new File(obtenerRutaArchivoFoto());
                Files.copy(archivoSeleccionado.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                BufferedImage imgOriginal = ImageIO.read(archivoDestino);
                Image imgEscalada = imgOriginal.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                lblAvatarIcon.setText("");
                lblAvatarIcon.setIcon(new ImageIcon(imgEscalada));

                JOptionPane.showMessageDialog(this, "¡Foto de perfil actualizada!", "Listo", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarRostroPropio() {
        String username = (usuarioActual != null && usuarioActual.getUsername() != null)
                ? usuarioActual.getUsername()
                : "usuario";

        CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(this, "Captura de Rostro - " + username, true);
        dialogCamara.setVisible(true);

        List<File> fotosCapturadas = dialogCamara.getListaFotosCapturadas();

        if (fotosCapturadas == null || fotosCapturadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se capturó nada.", "Cancelado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean guardadoExitoso = facialAuthService.registrarMuestrasBiometricas(fotosCapturadas, username);

        if (guardadoExitoso) {
            lblEstadoRostro.setText("Estado: ¡Rostro registrado y activo!");
            lblEstadoRostro.setForeground(new Color(39, 174, 96));
            btnCapturarRostro.setText("✅ Actualizar Rostro");

            JOptionPane.showMessageDialog(this, "¡Rostro guardado con éxito en la BD!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos biométricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Manejo de colores para el tema oscuro/claro en tiempo real
    private void aplicarTemaConfiguracion() {
        Color bgColor = modoOscuroActual ? new Color(20, 24, 33) : new Color(245, 247, 250);
        Color panelColor = modoOscuroActual ? new Color(30, 36, 48) : Color.WHITE;
        Color textColor = modoOscuroActual ? Color.WHITE : new Color(40, 40, 40);
        Color cardBgDark = modoOscuroActual ? new Color(40, 48, 62) : new Color(235, 240, 245);
        Color textMuted = modoOscuroActual ? new Color(180, 195, 215) : new Color(100, 110, 120);

        mainPanel.setBackground(bgColor);

        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panelTab = (JPanel) comp;
                panelTab.setBackground(panelColor);
                actualizarComponentesRecursivo(panelTab, textColor, cardBgDark, textMuted, modoOscuroActual);
            }
        }

        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && comp != tabbedPane) {
                comp.setBackground(bgColor);
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void actualizarComponentesRecursivo(Container contenedor, Color textColor, Color cardBg, Color textMuted, boolean esOscuro) {
        for (Component hijo : contenedor.getComponents()) {
            if (hijo instanceof JLabel) {
                JLabel lbl = (JLabel) hijo;
                if (lbl == lblAvatarIcon) continue;
                if (lbl.getText().startsWith("•") || lbl.getText().startsWith("ℹ️") || lbl.getText().startsWith("⚡")) {
                    lbl.setForeground(textMuted);
                } else if (hijo != lblEstadoRostro || !lblEstadoRostro.getText().contains("¡Rostro registrado")) {
                    lbl.setForeground(textColor);
                }
            } else if (hijo instanceof JCheckBox) {
                JCheckBox chk = (JCheckBox) hijo;
                chk.setForeground(textColor);
                chk.setOpaque(false);
            } else if (hijo instanceof JTextField || hijo instanceof JPasswordField) {
                hijo.setBackground(esOscuro ? new Color(40, 48, 62) : Color.WHITE);
                hijo.setForeground(esOscuro ? Color.WHITE : Color.BLACK);
                if (hijo instanceof JTextField) {
                    ((JTextField) hijo).setCaretColor(esOscuro ? Color.WHITE : Color.BLACK);
                }
            } else if (hijo instanceof JPanel) {
                JPanel pnl = (JPanel) hijo;
                if (pnl.getBackground() != null && !pnl.getBackground().equals(new Color(30, 90, 150))) {
                    pnl.setBackground(cardBg);
                }
                actualizarComponentesRecursivo(pnl, textColor, cardBg, textMuted, esOscuro);
            } else if (hijo instanceof Container) {
                actualizarComponentesRecursivo((Container) hijo, textColor, cardBg, textMuted, esOscuro);
            }
        }
    }
}