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
package com.sigelab.view;

import com.sigelab.model.Usuario;
import com.sigelab.service.FacialAuthService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ConfiguracionView extends JFrame {

    private Usuario usuarioActual;
    private FacialAuthService facialAuthService;

    // Declaración de los componentes como atributos de la clase
    private JLabel lblEstadoRostro;
    private JButton btnCapturarRostro;
    private JButton btnCerrar;

    public ConfiguracionView(Usuario usuario) {
        this.usuarioActual = usuario;
        this.facialAuthService = new FacialAuthService();

        setTitle("SIGELAB - Configuración de Perfil y Biometría");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        // Título del módulo
        JLabel lblTitulo = new JLabel("Configuración de Perfil");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(40, 20, 350, 25);
        lblTitulo.setForeground(new Color(30, 60, 114));
        panel.add(lblTitulo);

        // Información del Usuario
        String nombreUser = (usuario != null && usuario.getNombre() != null) ? usuario.getNombre() : "Usuario";
        String apellidoUser = (usuario != null && usuario.getApellido() != null) ? usuario.getApellido() : "";

        JLabel lblUsuario = new JLabel("Usuario actual: " + nombreUser + " " + apellidoUser);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUsuario.setBounds(40, 60, 350, 25);
        panel.add(lblUsuario);

        // Separador visual
        JSeparator separator = new JSeparator();
        separator.setBounds(40, 95, 350, 10);
        panel.add(separator);

        // Subtítulo de Biometría
        JLabel lblBiometria = new JLabel("Reconocimiento Facial (2FA)");
        lblBiometria.setFont(new Font("Arial", Font.BOLD, 14));
        lblBiometria.setBounds(40, 110, 300, 25);
        lblBiometria.setForeground(new Color(40, 120, 180));
        panel.add(lblBiometria);

        // Inicialización de la etiqueta de estado
        lblEstadoRostro = new JLabel("Estado: Configura tu rostro para habilitar el acceso facial.");
        lblEstadoRostro.setFont(new Font("Arial", Font.ITALIC, 11));
        lblEstadoRostro.setBounds(40, 135, 360, 20);
        panel.add(lblEstadoRostro);

        // Botón para capturar/actualizar el rostro con la Webcam
        btnCapturarRostro = new JButton("📷 Registrar / Actualizar Mi Rostro");
        btnCapturarRostro.setBounds(40, 170, 350, 40);
        btnCapturarRostro.setBackground(new Color(40, 120, 180));
        btnCapturarRostro.setForeground(Color.WHITE);
        btnCapturarRostro.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(btnCapturarRostro);

        // Botón para cerrar la ventana
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(165, 230, 110, 30);
        btnCerrar.setBackground(new Color(100, 110, 120));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnCerrar);

        // Eventos de los botones
        btnCapturarRostro.addActionListener(e -> registrarRostroPropio());
        btnCerrar.addActionListener(e -> dispose());

        add(panel);
    }

    /**
     * Captura la ráfaga de fotos del usuario actual con la webcam y las guarda en SQL Server.
     */
    private void registrarRostroPropio() {
        String username = (usuarioActual != null && usuarioActual.getUsername() != null)
                ? usuarioActual.getUsername()
                : ((usuarioActual != null && usuarioActual.getNombre() != null) ? usuarioActual.getNombre() : "usuario");

        // Abrir ventana modal para la captura biométrica en ráfaga (52 fotos)
        CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(this, "Captura de Rostro Oficial - " + username, true);
        dialogCamara.setVisible(true);

        List<File> fotosCapturadas = dialogCamara.getListaFotosCapturadas();

        if (fotosCapturadas == null || fotosCapturadas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se realizó ninguna captura.",
                    "Proceso Cancelado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Guardar ráfaga de 52 muestras directamente en la base de datos SQL Server
        boolean guardadoExitoso = facialAuthService.registrarMuestrasBiometricas(fotosCapturadas, username);

        if (guardadoExitoso) {
            lblEstadoRostro.setText("Estado: ¡Rostro registrado y activo correctamente!");
            lblEstadoRostro.setForeground(new Color(39, 174, 96)); // Verde
            btnCapturarRostro.setText("✅ Actualizar Rostro Registrado");

            JOptionPane.showMessageDialog(this,
                    "¡Patrón biométrico facial registrado con éxito!\nSe han guardado las " + fotosCapturadas.size() + " muestras en la base de datos.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar las imágenes biométricas en la base de datos.",
                    "Error de Guardado",
                    JOptionPane.ERROR_MESSAGE);

        }
    }
}