package com.sigelab.view;

import com.sigelab.controller.UsuarioController;
import com.sigelab.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// La clase extiende de JFrame para crear una ventana gráfica de Java Swing
public class LoginView extends JFrame {

    // Componentes de la interfaz visual
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Instancia del controlador para enviar los datos ingresados
    private UsuarioController usuarioController;

    public LoginView() {
        // Inicializar el controlador
        this.usuarioController = new UsuarioController();

        // Configuración principal de la ventana
        setTitle("SIGELAB - Inicio de Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setResizable(false);

        // Crear panel principal con diseño de cuadrícula (Layout)
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

        // Botón de Inicio de Sesión
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(120, 170, 150, 35);
        btnLogin.setBackground(new Color(30, 60, 114));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnLogin);

        // Evento cuando el usuario hace clic en el botón "Iniciar Sesión"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarLogin();
            }
        });

        // -------------------------------------------------------------------------
        // MEJORA: Permitir iniciar sesión presionando la tecla ENTER
        // -------------------------------------------------------------------------
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
     * Captura los datos de los campos de texto y los pasa al Controlador.
     */
    private void ejecutarLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // El controlador procesa la petición enviándola a la capa de servicio
        Usuario usuario = usuarioController.procesarLogin(username, password);

        if (usuario != null) {
            // Muestra mensaje flotante de éxito
            JOptionPane.showMessageDialog(this,
                    "¡Bienvenido(a), " + usuario.getNombre() + " " + usuario.getApellido() + "!\nInicio de sesión exitoso.",
                    "Acceso Concedido",
                    JOptionPane.INFORMATION_MESSAGE);

            // Cierra la ventana de login actual y abre el Menú Principal (Dashboard)
            this.dispose();
            MenuPrincipalView menuPrincipal = new MenuPrincipalView(usuario);
            menuPrincipal.setVisible(true);

        } else {
            // Muestra mensaje flotante de error
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos, o el usuario está inactivo.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método main para ejecutar directamente la interfaz gráfica
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView vista = new LoginView();
            vista.setVisible(true);
        });
    }
}