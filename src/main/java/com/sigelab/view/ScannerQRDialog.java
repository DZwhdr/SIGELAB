
package org.example.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import org.example.util.QRUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ScannerQRDialog extends JDialog {

    private Webcam webcam;
    private WebcamPanel panel;

    private final Consumer<String> alDetectarQR;

    private ExecutorService executor;
    private volatile boolean escaneando = false;

    public ScannerQRDialog(
            Window parent,
            Consumer<String> alDetectarQR
    ) {
        super(
                parent,
                "Escáner de Código QR - SIGELAB",
                ModalityType.APPLICATION_MODAL
        );

        this.alDetectarQR = alDetectarQR;

        configurarVentana();
        iniciarCamara();
    }

    private void configurarVentana() {
        setLayout(new BorderLayout(10, 10));
        setSize(550, 450);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel titulo = new JLabel(
                "Escanear código QR del equipo",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 5, 10)
        );

        add(titulo, BorderLayout.NORTH);

        JLabel instrucciones = new JLabel(
                "Coloque el código QR frente a la cámara",
                SwingConstants.CENTER
        );

        instrucciones.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 15, 10)
        );

        add(instrucciones, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                detenerEscaneo();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                detenerEscaneo();
            }
        });
    }

    private void iniciarCamara() {
        try {
            webcam = Webcam.getDefault();

            if (webcam == null) {
                mostrarError(
                        "No se detectó ninguna cámara web."
                );
                return;
            }

            panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(false);
            panel.setDisplayDebugInfo(false);
            panel.setImageSizeDisplayed(false);
            panel.setMirrored(true);

            add(panel, BorderLayout.CENTER);

            escaneando = true;

            executor = Executors.newSingleThreadExecutor();

            executor.execute(this::leerCodigosQR);

        } catch (Exception e) {
            mostrarError(
                    "No se pudo iniciar la cámara: " + e.getMessage()
            );
        }
    }

    private void leerCodigosQR() {
        try {
            // Esperar a que el panel abra la cámara.
            while (escaneando && !webcam.isOpen()) {
                Thread.sleep(100);
            }

            while (escaneando && webcam.isOpen()) {

                Thread.sleep(150);

                BufferedImage imagen = webcam.getImage();

                if (imagen == null) {
                    continue;
                }

                String codigo;

                try {
                    codigo = QRUtils.leerQRDesdeImagen(imagen);
                } catch (Exception e) {
                    continue;
                }

                if (codigo != null && !codigo.isBlank()) {

                    escaneando = false;

                    String codigoDetectado = codigo.trim();

                    SwingUtilities.invokeLater(() -> {
                        dispose();

                        if (alDetectarQR != null) {
                            alDetectarQR.accept(codigoDetectado);
                        }
                    });

                    break;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            if (escaneando) {
                SwingUtilities.invokeLater(() ->
                        mostrarError(
                                "Error al escanear: " + e.getMessage()
                        )
                );
            }
        }
    }

    private void detenerEscaneo() {
        escaneando = false;

        if (executor != null) {
            executor.shutdownNow();
        }

        // WebcamPanel administra la apertura de la cámara.
        // El cierre se realiza en otro hilo para no bloquear Swing.
        Webcam camaraActual = webcam;

        if (camaraActual != null) {
            Thread hiloCierre = new Thread(() -> {
                try {
                    if (camaraActual.isOpen()) {
                        camaraActual.close();
                    }
                } catch (Exception ignored) {
                }
            }, "SIGELAB-CierreCamara");

            hiloCierre.setDaemon(true);
            hiloCierre.start();
        }
    }

    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Escáner QR",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
        });
    }
}
