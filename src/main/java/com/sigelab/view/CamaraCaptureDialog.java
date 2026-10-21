package com.sigelab.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
    private boolean modoRafaga = true; // true = 52 fotos (Registro), false = 1 foto (Login)

    // Constructor 1: Por defecto ejecuta el registro completo de 52 fotos (para UsuarioManagementView / ConfiguracionView)
    public CamaraCaptureDialog(Frame parent, String titulo) {
        this(parent, titulo, true);
    }

    // Constructor 2: Permite especificar el modo de uso (true = 52 fotos, false = 1 foto instantánea)
    public CamaraCaptureDialog(Frame parent, String titulo, boolean modoRafaga) {
        super(parent, titulo, true);
        this.modoRafaga = modoRafaga;

        setLayout(new BorderLayout());
        setSize(680, 560);
        setLocationRelativeTo(parent);

        // Inicializar Webcam
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            panelCamara = new WebcamPanel(webcam);
            panelCamara.setFPSDisplayed(true);
            panelCamara.setMirrored(true);
            add(panelCamara, BorderLayout.CENTER);
        } else {
            add(new JLabel("No se detectó ninguna cámara web conectada.", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        // Panel inferior de control
        JPanel panelControl = new JPanel(new GridLayout(modoRafaga ? 3 : 2, 1, 5, 5));
        panelControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (modoRafaga) {
            lblEstado = new JLabel("Póngase frente a la cámara y presione 'Iniciar Registro Biométrico'", SwingConstants.CENTER);
            lblEstado.setFont(new Font("Arial", Font.BOLD, 13));

            progressBar = new JProgressBar(0, TOTAL_FOTOS);
            progressBar.setStringPainted(true);
            progressBar.setValue(0);

            btnIniciarCaptura = new JButton("📷 Iniciar Registro Biométrico (52 Muestras)");
            btnIniciarCaptura.setFont(new Font("Arial", Font.BOLD, 14));
            btnIniciarCaptura.setBackground(new Color(41, 128, 185));
            btnIniciarCaptura.setForeground(Color.WHITE);

            btnIniciarCaptura.addActionListener(e -> ejecutarRafagaCaptura());

            panelControl.add(lblEstado);
            panelControl.add(progressBar);
            panelControl.add(btnIniciarCaptura);
        } else {
            // MODO LOGIN: Captura única e instantánea
            lblEstado = new JLabel("Mire directamente a la cámara para verificar su identidad", SwingConstants.CENTER);
            lblEstado.setFont(new Font("Arial", Font.BOLD, 13));

            btnIniciarCaptura = new JButton("📷 Capturar Rostro para Login");
            btnIniciarCaptura.setFont(new Font("Arial", Font.BOLD, 14));
            btnIniciarCaptura.setBackground(new Color(39, 174, 96)); // Verde
            btnIniciarCaptura.setForeground(Color.WHITE);

            btnIniciarCaptura.addActionListener(e -> ejecutarCapturaUnica());

            panelControl.add(lblEstado);
            panelControl.add(btnIniciarCaptura);
        }

        add(panelControl, BorderLayout.SOUTH);
    }

    /**
     * FLujo 1: Ráfaga de 52 fotogramas (Uso exclusivo para creación/configuración de usuario)
     */
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
                    Thread.sleep(60); // Espera 60 ms entre fotogramas

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

                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("✅ Registro biométrico completado exitosamente.");
                    JOptionPane.showMessageDialog(this, "Se han capturado las 52 muestras biométricas.");
                    dispose();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error durante la captura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnIniciarCaptura.setEnabled(true);
                });
            }
        }).start();
    }

    /**
     * Flujo 2: Toma 1 sola foto instantánea en vivo (Uso exclusivo para el Inicio de Sesión / Login)
     */
    private void ejecutarCapturaUnica() {
        if (webcam == null || !webcam.isOpen()) {
            JOptionPane.showMessageDialog(this, "La cámara no está activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                listaFotosCapturadas.clear();
                File tempFile = File.createTempFile("login_captura_", ".jpg");
                ImageIO.write(image, "JPG", tempFile);
                listaFotosCapturadas.add(tempFile);

                dispose(); // Cierra el diálogo para proceder a autenticar
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo obtener imagen de la webcam.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la captura del login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Devuelve toda la ráfaga (52 imágenes) para ser persistidas en SQL Server
     */
    public List<File> getListaFotosCapturadas() {
        return listaFotosCapturadas;
    }

    /**
     * Devuelve la primera o única foto de la lista (Garantiza compatibilidad con LoginView)
     */
    public File getArchivoCapturado() {
        if (listaFotosCapturadas != null && !listaFotosCapturadas.isEmpty()) {
            return listaFotosCapturadas.get(0);
        }
        return null;
    }

    @Override
    public void dispose() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        super.dispose();
    }
}