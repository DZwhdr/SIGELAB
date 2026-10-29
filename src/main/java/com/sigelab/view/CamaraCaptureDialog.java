package com.sigelab.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.sigelab.service.FacialAuthService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CamaraCaptureDialog extends JDialog {

    private Webcam webcam;
    private WebcamPanel panelCamara;
    private JButton btnIniciarCaptura;
    private JLabel lblEstado;
    private JProgressBar progressBar;

    private static final int TOTAL_FOTOS = 52;
    private List<File> listaFotosCapturadas = new ArrayList<>();
    private boolean modoRafaga = true;
    private Timer timerAutoCaptura;
    private String nombreUsuario;

    // ==================== CONSTRUCTORES SOBRECARGADOS ====================

    public CamaraCaptureDialog(Frame parent, String titulo) {
        this(parent, titulo, true, null);
    }

    public CamaraCaptureDialog(Frame parent, String titulo, boolean modoRafaga) {
        this(parent, titulo, modoRafaga, null);
    }

    public CamaraCaptureDialog(Frame parent, String titulo, boolean modoRafaga, String nombreUsuario) {
        super(parent, titulo, true);
        this.modoRafaga = modoRafaga;

        // Si no mandan un usuario válido o mandan el texto genérico, lo dejamos en null para evitar falsas búsquedas en BDD
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() || nombreUsuario.equals("Sistema SIGELAB")) {
            this.nombreUsuario = null;
        } else {
            this.nombreUsuario = nombreUsuario.trim();
        }

        setSize(560, 660);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 24, 33));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior para la cabecera y el badge del usuario
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(20, 24, 33));

        JLabel lblHeaderTitle = new JLabel("AUTENTICACIÓN BIOMÉTRICA", SwingConstants.CENTER);
        lblHeaderTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHeaderTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblHeaderTitle.setForeground(new Color(200, 220, 255));
        topPanel.add(lblHeaderTitle);

        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Muestro el nombre del usuario al que le vamos a tomar la foto (o un texto neutral si es null)
        JLabel lblUserBadge = new JLabel("👤 " + (this.nombreUsuario != null ? this.nombreUsuario : "Registro General"), SwingConstants.CENTER);
        lblUserBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUserBadge.setFont(new Font("Arial", Font.BOLD, 14));
        lblUserBadge.setForeground(new Color(255, 215, 0));
        topPanel.add(lblUserBadge);

        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        if (modoRafaga) {
            lblEstado = new JLabel("Póngase frente a la cámara y presione 'Iniciar Registro'", SwingConstants.CENTER);
        } else {
            lblEstado = new JLabel("⏳ Esperando presencia frente al lente...", SwingConstants.CENTER);
        }
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(140, 155, 175));
        topPanel.add(lblEstado);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Contenedor de la webcam con su borde estilizado
        JPanel cameraContainer = new JPanel(new BorderLayout());
        cameraContainer.setBackground(new Color(15, 18, 25));
        cameraContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 0, 15, 0),
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true)
        ));

        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            panelCamara = new WebcamPanel(webcam);
            panelCamara.setFPSDisplayed(false);
            panelCamara.setMirrored(true);
            cameraContainer.add(panelCamara, BorderLayout.CENTER);
        } else {
            JLabel lblError = new JLabel("No se detectó ninguna cámara web conectada.", SwingConstants.CENTER);
            lblError.setForeground(Color.WHITE);
            cameraContainer.add(lblError, BorderLayout.CENTER);
        }

        mainPanel.add(cameraContainer, BorderLayout.CENTER);

        // Panel inferior para la barra de progreso y el botón de acción
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 24, 33));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        if (modoRafaga) {
            progressBar = new JProgressBar(0, TOTAL_FOTOS);
            progressBar.setStringPainted(true);
            progressBar.setValue(0);
            progressBar.setPreferredSize(new Dimension(0, 25));
            progressBar.setForeground(new Color(41, 128, 185));
            progressBar.setBackground(new Color(30, 35, 45));

            bottomPanel.add(progressBar);
            bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

            btnIniciarCaptura = new JButton("📷 Iniciar Registro Biométrico (52 Muestras)");
            btnIniciarCaptura.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnIniciarCaptura.setPreferredSize(new Dimension(0, 42));
            btnIniciarCaptura.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btnIniciarCaptura.setBackground(new Color(41, 128, 185));
            btnIniciarCaptura.setForeground(Color.WHITE);
            btnIniciarCaptura.setFont(new Font("Arial", Font.BOLD, 13));
            btnIniciarCaptura.setFocusPainted(false);
            btnIniciarCaptura.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btnIniciarCaptura.addActionListener(e -> ejecutarRafagaCaptura());
            bottomPanel.add(btnIniciarCaptura);
        } else {
            btnIniciarCaptura = new JButton("⏳ Analizando Rostro en Vivo...");
            btnIniciarCaptura.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnIniciarCaptura.setPreferredSize(new Dimension(0, 42));
            btnIniciarCaptura.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btnIniciarCaptura.setBackground(new Color(39, 174, 96));
            btnIniciarCaptura.setForeground(Color.WHITE);
            btnIniciarCaptura.setFont(new Font("Arial", Font.BOLD, 13));
            btnIniciarCaptura.setEnabled(false);

            bottomPanel.add(btnIniciarCaptura);

            // Chequeo automático para ver si hay un rostro frente a la cámara en el login
            final int[] intentosEspera = {0};
            timerAutoCaptura = new Timer(500, e -> {
                if (webcam == null || !webcam.isOpen()) return;

                BufferedImage imgPrueba = webcam.getImage();
                if (imgPrueba != null) {
                    if (esRostroDetectadoOImagenValida(imgPrueba)) {
                        intentosEspera[0]++;
                        lblEstado.setText("⚡ Rostro detectado. Capturando...");

                        if (intentosEspera[0] >= 3) { // Si aguanta 1.5 seg frente al lente, tira la captura
                            timerAutoCaptura.stop();
                            ejecutarCapturaUnicaAutomatica();
                        }
                    } else {
                        intentosEspera[0] = 0;
                        lblEstado.setText("⚠️ No se detecta rostro. Por favor, colóquese frente al lente.");
                    }
                }
            });
            timerAutoCaptura.start();
        }

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Apago el timer al cerrar la ventana para que no chupe recursos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timerAutoCaptura != null) {
                    timerAutoCaptura.stop();
                }
            }
        });
    }

    // Validación sencilla de brillo para confirmar que hay luz y no está tapada la cámara
    private boolean esRostroDetectadoOImagenValida(BufferedImage image) {
        if (image == null) return false;

        long sumaBrillo = 0;
        int muestras = 0;
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = width / 4; x < (width * 3) / 4; x += 20) {
            for (int y = height / 4; y < (height * 3) / 4; y += 20) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                sumaBrillo += (r + g + b) / 3;
                muestras++;
            }
        }

        int brilloPromedio = (muestras > 0) ? (int) (sumaBrillo / muestras) : 0;
        return brilloPromedio > 30 && brilloPromedio < 240;
    }

    // Tiro la ráfaga de 52 fotos y las guardo directo en SQL Server usando el FacialAuthService
    private void ejecutarRafagaCaptura() {
        if (webcam == null || !webcam.isOpen()) {
            JOptionPane.showMessageDialog(this, "La cámara no está activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnIniciarCaptura.setEnabled(false);
        listaFotosCapturadas.clear();

        new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL_FOTOS; i++) {
                    Thread.sleep(60);

                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        File tempFile = File.createTempFile("muestra_" + i + "_", ".jpg");
                        ImageIO.write(image, "JPG", tempFile);
                        listaFotosCapturadas.add(tempFile);
                    }

                    int conteoActual = i;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(conteoActual);
                        lblEstado.setText("Mueva levemente la cabeza... Muestra " + conteoActual + " de " + TOTAL_FOTOS);
                    });
                }

                // Validamos si hay un usuario real antes de intentar guardar en la base de datos
                if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                    FacialAuthService facialService = new FacialAuthService();
                    boolean guardadoExitoso = facialService.registrarMuestrasBiometricas(listaFotosCapturadas, nombreUsuario);

                    SwingUtilities.invokeLater(() -> {
                        if (guardadoExitoso) {
                            lblEstado.setText("✅ Registro biométrico completado y guardado.");
                            JOptionPane.showMessageDialog(this, "Se han capturado y guardado las 52 muestras biométricas para: " + nombreUsuario);
                        } else {
                            JOptionPane.showMessageDialog(this, "Las fotos se tomaron, pero hubo un error al guardarlas en la base de datos.", "Error BD", JOptionPane.ERROR_MESSAGE);
                        }
                        dispose();
                    });
                } else {
                    // Si se abrió sin un usuario asignado, solo cerramos limpiamente sin tirar error
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText("✅ Captura completada.");
                        JOptionPane.showMessageDialog(this, "Se han capturado las 52 muestras correctamente.");
                        dispose();
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error durante la captura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnIniciarCaptura.setEnabled(true);
                });
            }
        }).start();
    }

    // Captura rápida para cuando entra al login automático
    private void ejecutarCapturaUnicaAutomatica() {
        if (webcam == null || !webcam.isOpen()) {
            return;
        }

        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                listaFotosCapturadas.clear();
                File tempFile = File.createTempFile("login_auto_", ".jpg");
                ImageIO.write(image, "JPG", tempFile);
                listaFotosCapturadas.add(tempFile);

                lblEstado.setText("✅ ¡Rostro capturado correctamente para " + (nombreUsuario != null ? nombreUsuario : "") + "!");

                Timer cierreTimer = new Timer(500, ev -> dispose());
                cierreTimer.setRepeats(false);
                cierreTimer.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<File> getListaFotosCapturadas() {
        return listaFotosCapturadas;
    }

    public File getArchivoCapturado() {
        if (listaFotosCapturadas != null && !listaFotosCapturadas.isEmpty()) {
            return listaFotosCapturadas.get(0);
        }
        return null;
    }

    // Cierro la webcam al destruir la ventana para liberar la camarita
    @Override
    public void dispose() {
        if (timerAutoCaptura != null) {
            timerAutoCaptura.stop();
        }
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        super.dispose();
    }
}