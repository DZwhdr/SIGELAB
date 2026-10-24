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

// La clase extiende de JFrame para crear una ventana gráfica de Java Swing
public class LoginView extends JFrame {

    // Componentes de la interfaz visual
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Instancia del controlador y servicio facial
    private UsuarioController usuarioController;
    private FacialAuthService facialAuthService;

    public LoginView() {
        // Inicializar el controlador y el servicio biométrico
        this.usuarioController = new UsuarioController();
        this.facialAuthService = new FacialAuthService();

        // Configuración principal de la ventana
        setTitle("SIGELAB - Inicio de Sesión");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setResizable(false);

        // Crear panel principal
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        // Título dentro de la ventana
        JLabel lblTitulo = new JLabel("SIGELAB");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(140, 20, 150, 30);
        lblTitulo.setForeground(new Color(30, 60, 114));
        panel.add(lblTitulo);

        // Etiqueta y campo de texto para Usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(50, 75, 80, 25);
        panel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 75, 180, 25);
        panel.add(txtUsername);

        // Etiqueta y campo de contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(50, 115, 80, 25);
        panel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 115, 180, 25);
        panel.add(txtPassword);

        // Botón de Inicio de Sesión (Valida credenciales y exige verificación facial obligatoria)
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(120, 170, 150, 35);
        btnLogin.setBackground(new Color(30, 60, 114));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnLogin);

        // Evento botón "Iniciar Sesión"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarLogin();
            }
        });

        // Permitir iniciar sesión presionando la tecla ENTER
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

        // Agregar el panel a la ventana
        add(panel);
    }

    /**
     * Valida credenciales con la BDD y fuerza la verificación facial obligatoria por webcam.
     */
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

        // 1. Validar usuario y contraseña con el controlador
        Usuario usuario = usuarioController.procesarLogin(username, password);

        if (usuario != null) {
            // 2. Si la contraseña es correcta, exigir el reconocimiento facial obligatorio
            JOptionPane.showMessageDialog(this,
                    "Credenciales correctas. A continuación se requiere verificación facial obligatoria.",
                    "Validación Biométrica Requerida",
                    JOptionPane.INFORMATION_MESSAGE);

            boolean facialVerificado = realizarCapturaYVerificacionFacial(username);

            if (facialVerificado) {
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido(a), " + usuario.getNombre() + " " + usuario.getApellido() + "!\nAcceso concedido.",
                        "Inicio de Sesión Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                MenuPrincipalView menuPrincipal = new MenuPrincipalView(usuario);
                menuPrincipal.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Acceso denegado. Se requiere autenticación facial para ingresar al sistema.",
                        "Error de Autenticación Facial",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos, o el usuario está inactivo.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Extrae la foto biométrica de referencia desde SQL Server y valida contra la captura en vivo de la webcam.
     */
    private boolean realizarCapturaYVerificacionFacial(String username) {
        // 1. Obtener la foto de referencia almacenada en SQL Server para el usuario
        File fotoBD = facialAuthService.obtenerFotoReferenciaBaseDatos(username);

        if (fotoBD == null || !fotoBD.exists()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron muestras biométricas registradas en la base de datos para el usuario: " + username + ".\nContacte al administrador del sistema.",
                    "Foto de Referencia No Encontrada",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Abrir la webcam en modo captura única (1 foto)
        CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(this, "Verificación Facial Obligatoria - " + username, false);
        dialogCamara.setVisible(true);

        File fotoWebcam = dialogCamara.getArchivoCapturado();

        if (fotoWebcam == null || !fotoWebcam.exists()) {
            return false; // Se canceló o cerró la cámara
        }

        // 3. Enviar a verificar la foto capturada contra la imagen obtenida de la base de datos
        return facialAuthService.autenticarRostro(fotoWebcam, fotoBD);
    }

    // Método main para ejecutar la interfaz gráfica
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView vista = new LoginView();
            vista.setVisible(true);
        });
    }
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

// La clase extiende de JFrame para crear una ventana gráfica de Java Swing
public class LoginView extends JFrame {

    // Componentes de la interfaz visual
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Instancia del controlador y servicio facial
    private UsuarioController usuarioController;
    private FacialAuthService facialAuthService;

    public LoginView() {
        // Inicializar el controlador y el servicio biométrico
        this.usuarioController = new UsuarioController();
        this.facialAuthService = new FacialAuthService();

        // Configuración principal de la ventana
        setTitle("SIGELAB - Inicio de Sesión");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setResizable(false);

        // Crear panel principal
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        // Título dentro de la ventana
        JLabel lblTitulo = new JLabel("SIGELAB");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(140, 20, 150, 30);
        lblTitulo.setForeground(new Color(30, 60, 114));
        panel.add(lblTitulo);

        // Etiqueta y campo de texto para Usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(50, 75, 80, 25);
        panel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 75, 180, 25);
        panel.add(txtUsername);

        // Etiqueta y campo de contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(50, 115, 80, 25);
        panel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 115, 180, 25);
        panel.add(txtPassword);

        // Botón de Inicio de Sesión (Valida credenciales y exige verificación facial obligatoria)
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(120, 170, 150, 35);
        btnLogin.setBackground(new Color(30, 60, 114));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnLogin);

        // Evento botón "Iniciar Sesión"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarLogin();
            }
        });

        // Permitir iniciar sesión presionando la tecla ENTER
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

        // Agregar el panel a la ventana
        add(panel);
    }

    /**
     * Valida credenciales con la BDD y fuerza la verificación facial obligatoria por webcam.
     */
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

        // 1. Validar usuario y contraseña con el controlador
        Usuario usuario = usuarioController.procesarLogin(username, password);

        if (usuario != null) {
            // 2. Si la contraseña es correcta, exigir el reconocimiento facial obligatorio
            JOptionPane.showMessageDialog(this,
                    "Credenciales correctas. A continuación se requiere verificación facial obligatoria.",
                    "Validación Biométrica Requerida",
                    JOptionPane.INFORMATION_MESSAGE);

            boolean facialVerificado = realizarCapturaYVerificacionFacial(username);

            if (facialVerificado) {
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido(a), " + usuario.getNombre() + " " + usuario.getApellido() + "!\nAcceso concedido.",
                        "Inicio de Sesión Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                MenuPrincipalView menuPrincipal = new MenuPrincipalView(usuario);
                menuPrincipal.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Acceso denegado. Se requiere autenticación facial para ingresar al sistema.",
                        "Error de Autenticación Facial",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos, o el usuario está inactivo.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Extrae la foto biométrica de referencia desde SQL Server y valida contra la captura en vivo de la webcam.
     */
    private boolean realizarCapturaYVerificacionFacial(String username) {
        // 1. Obtener la foto de referencia almacenada en SQL Server para el usuario
        File fotoBD = facialAuthService.obtenerFotoReferenciaBaseDatos(username);

        if (fotoBD == null || !fotoBD.exists()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron muestras biométricas registradas en la base de datos para el usuario: " + username + ".\nContacte al administrador del sistema.",
                    "Foto de Referencia No Encontrada",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Abrir la webcam en modo captura única (1 foto)
        CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(this, "Verificación Facial Obligatoria - " + username, false);
        dialogCamara.setVisible(true);

        File fotoWebcam = dialogCamara.getArchivoCapturado();

        if (fotoWebcam == null || !fotoWebcam.exists()) {
            return false; // Se canceló o cerró la cámara
        }

        // 3. Enviar a verificar la foto capturada contra la imagen obtenida de la base de datos
        return facialAuthService.autenticarRostro(fotoWebcam, fotoBD);
    }

    // Método main para ejecutar la interfaz gráfica
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView vista = new LoginView();
            vista.setVisible(true);
        });
    }
}