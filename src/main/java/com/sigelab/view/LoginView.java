package com.sigelab.view;

import com.sigelab.controller.UsuarioController;
import com.sigelab.model.Usuario;
import com.sigelab.service.FacialAuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnThemeToggle;

    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel lblTitulo;
    private JLabel lblUser;
    private JLabel lblPass;

    private UsuarioController usuarioController;
    private FacialAuthService facialAuthService;

    // Control de intentos fallidos para bloquear al usuario si se pasa
    private int intentosFallidos = 0;
    private static final int MAX_INTENTOS = 3;
    private boolean esModoOscuro = false;

    public LoginView() {
        this.usuarioController = new UsuarioController();
        this.facialAuthService = new FacialAuthService();

        setTitle("SIGELAB - Inicio de Sesión");
        setSize(750, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridLayout(1, 2));

        // Panel izquierdo con el branding y el logo de la app
        leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(30, 60, 114));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblLogo = new JLabel("⚡ SIGELAB");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        leftPanel.add(lblLogo, gbc);

        gbc.gridy = 1;
        JLabel lblSub = new JLabel("<html><center>Sistema de Gestión de<br>Laboratorios Universitarios</center></html>");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(new Color(200, 220, 255));
        leftPanel.add(lblSub, gbc);

        add(leftPanel);

        // Panel derecho donde va el formulario de acceso y el botón de tema
        rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);

        // Botón para cambiar entre modo oscuro y claro arriba a la derecha
        btnThemeToggle = new JButton("🌙");
        btnThemeToggle.setBounds(315, 15, 45, 30);
        btnThemeToggle.setFocusPainted(false);
        btnThemeToggle.setBackground(new Color(240, 240, 240));
        btnThemeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemeToggle.setToolTipText("Cambiar Tema");
        rightPanel.add(btnThemeToggle);

        lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(40, 40, 40));
        lblTitulo.setBounds(35, 35, 250, 30);
        rightPanel.add(lblTitulo);

        lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        lblUser.setForeground(new Color(100, 100, 100));
        lblUser.setBounds(35, 85, 200, 20);
        rightPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(35, 110, 300, 35);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtUsername);

        lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        lblPass.setForeground(new Color(100, 100, 100));
        lblPass.setBounds(35, 155, 200, 20);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(35, 180, 300, 35);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(txtPassword);

        btnLogin = new JButton("Ingresar con Facial");
        btnLogin.setBounds(35, 240, 300, 42);
        btnLogin.setBackground(new Color(30, 60, 114));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(btnLogin);

        add(rightPanel);

        // Listeners para los botones y para que funcione con Enter
        btnThemeToggle.addActionListener(e -> alternarModoOscuro());
        btnLogin.addActionListener(e -> ejecutarLogin());

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ejecutarLogin();
                }
            }
        };

        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    // Método para cambiar los colores de la ventana según el tema
    private void alternarModoOscuro() {
        esModoOscuro = !esModoOscuro;

        if (esModoOscuro) {
            rightPanel.setBackground(new Color(20, 24, 33));
            lblTitulo.setForeground(new Color(220, 230, 242));
            lblUser.setForeground(new Color(150, 165, 185));
            lblPass.setForeground(new Color(150, 165, 185));

            txtUsername.setBackground(new Color(30, 36, 48));
            txtUsername.setForeground(Color.WHITE);
            txtUsername.setCaretColor(Color.WHITE);

            txtPassword.setBackground(new Color(30, 36, 48));
            txtPassword.setForeground(Color.WHITE);
            txtPassword.setCaretColor(Color.WHITE);

            btnThemeToggle.setText("☀️");
            btnThemeToggle.setBackground(new Color(40, 48, 62));
            btnThemeToggle.setForeground(Color.WHITE);
        } else {
            rightPanel.setBackground(Color.WHITE);
            lblTitulo.setForeground(new Color(40, 40, 40));
            lblUser.setForeground(new Color(100, 100, 100));
            lblPass.setForeground(new Color(100, 100, 100));

            txtUsername.setBackground(Color.WHITE);
            txtUsername.setForeground(Color.BLACK);

            txtPassword.setBackground(Color.WHITE);
            txtPassword.setForeground(Color.BLACK);

            btnThemeToggle.setText("🌙");
            btnThemeToggle.setBackground(new Color(240, 240, 240));
            btnThemeToggle.setForeground(Color.BLACK);
        }

        rightPanel.repaint();
    }

    // Animación suave de aparición para que no abra de golpe
    public void animarAperturaVentana() {
        try {
            setOpacity(0.0f);
            setVisible(true);

            Timer timerFade = new Timer(15, null);
            timerFade.addActionListener(new ActionListener() {
                float opacidad = 0.0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacidad += 0.06f;
                    if (opacidad >= 1.0f) {
                        opacidad = 1.0f;
                        timerFade.stop();
                    }
                    try {
                        setOpacity(opacidad);
                    } catch (Exception ex) {
                        timerFade.stop();
                    }
                }
            });
            timerFade.start();
        } catch (Exception e) {
            setVisible(true);
        }
    }

    // Aquí valido las credenciales, checo si es primer login o si pasa a biometría
    private void ejecutarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese su usuario y contraseña.",
                    "Campos Incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioController.procesarLogin(username, password);

        if (usuario != null) {
            intentosFallidos = 0;

            // Si es la primera vez que entra, lo mando directo a cambiar contraseña y registrar rostro
            if (usuario.isPrimerLogin()) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>⚠️ Primer Acceso Detectado</b><br><br>"
                                + "Por seguridad corporativa, es obligatorio actualizar su contraseña temporal<br>"
                                + "y registrar su patrón biométrico facial antes de ingresar al sistema.</html>",
                        "Seguridad Obligatoria - SIGELAB",
                        JOptionPane.WARNING_MESSAGE);

                ConfiguracionView configView = new ConfiguracionView(this, usuario, esModoOscuro, (nuevoModo) -> {
                    this.esModoOscuro = nuevoModo;
                });
                configView.setVisible(true);
                txtPassword.setText("");
                return; // Corto aquí para que no abra el menú principal todavía
            }

            // Si ya pasó el primer filtro, le pido su verificación facial normal
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            boolean facialVerificado = realizarCapturaYVerificacionFacial(username, nombreCompleto);

            if (facialVerificado) {
                String mensajeBienvenida = "<html><div style='padding: 8px; font-family: Arial; width: 280px;'>"
                        + "<h3 style='color: #1E3C72; margin: 0 0 5px 0;'>⚡ ¡Acceso Biométrico Autorizado!</h3>"
                        + "<hr style='border: 0.5px solid #2980b9; margin-bottom: 8px;'>"
                        + "<p style='font-size: 12px; color: #333333; margin: 0 0 5px 0;'>Bienvenido(<span style='color: #2980b9;'>a</span>), <b>" + usuario.getNombre() + " " + usuario.getApellido() + "</b>.</p>"
                        + "<p style='font-size: 11px; color: #666666; margin: 0;'>Identidad verificada con éxito. Abriendo sistema...</p>"
                        + "</div></html>";

                JOptionPane.showMessageDialog(this,
                        new JLabel(mensajeBienvenida),
                        "SIGELAB - Sesión Iniciada",
                        JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                MenuPrincipalView menuPrincipal = new MenuPrincipalView(usuario, esModoOscuro);
                menuPrincipal.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Acceso denegado. Se requiere autenticación facial para ingresar al sistema.",
                        "Error de Autenticación Facial",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }

        } else {
            intentosFallidos++;
            int intentosRestantes = MAX_INTENTOS - intentosFallidos;

            if (intentosFallidos >= MAX_INTENTOS) {
                JOptionPane.showMessageDialog(this,
                        "Ha superado el límite de 3 intentos fallidos.\nSu usuario ha sido BLOQUEADO por seguridad.",
                        "Usuario Bloqueado",
                        JOptionPane.ERROR_MESSAGE);

                btnLogin.setEnabled(false);
                txtUsername.setEnabled(false);
                txtPassword.setEnabled(false);
            } else if (intentosFallidos == 2) {
                JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos.\n⚠️ ADVERTENCIA: Le queda 1 intento más antes de que su usuario sea bloqueado.",
                        "Advertencia de Seguridad",
                        JOptionPane.WARNING_MESSAGE);
                txtPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario o contraseña incorrectos, o el usuario está inactivo.\nIntentos restantes: " + intentosRestantes,
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }
        }
    }

    // Abro la camarita para validar el rostro contra la foto guardada en BD
    private boolean realizarCapturaYVerificacionFacial(String username, String nombreCompleto) {
        File fotoBD = facialAuthService.obtenerFotoReferenciaBaseDatos(username);

        if (fotoBD == null || !fotoBD.exists()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron muestras biométricas registradas en la base de datos para el usuario: " + username + ".\nContacte al administrador del sistema.",
                    "Foto de Referencia No Encontrada",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(this, "Verificación Facial Obligatoria - " + username, false, nombreCompleto);
        dialogCamara.setVisible(true);

        File fotoWebcam = dialogCamara.getArchivoCapturado();

        if (fotoWebcam == null || !fotoWebcam.exists()) {
            return false;
        }

        return facialAuthService.autenticarRostro(fotoWebcam, fotoBD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView vista = new LoginView();
            vista.animarAperturaVentana();
        });
    }
}