
package com.sigelab.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.sigelab.util.QRUtils;

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
    private volatile boolean cerrando = false;

    private JButton btnVolver;

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
        setSize(550, 490);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Título
        JLabel titulo = new JLabel(
                "Escanear código QR del equipo",
                SwingConstants.CENTER
        );

        titulo.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );

        titulo.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 5, 10)
        );

        add(titulo, BorderLayout.NORTH);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(0, 12));

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 15, 15)
        );

        JLabel instrucciones = new JLabel(
                "Coloque el código QR frente a la cámara",
                SwingConstants.CENTER
        );

        panelInferior.add(
                instrucciones,
                BorderLayout.NORTH
        );

        // Botón para regresar
        btnVolver = new JButton("← Volver al menú principal");

        btnVolver.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        btnVolver.setBackground(
                new Color(30, 60, 114)
        );

        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);

        btnVolver.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        JPanel panelBoton = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );

        panelBoton.add(btnVolver);

        panelInferior.add(
                panelBoton,
                BorderLayout.SOUTH
        );

        add(panelInferior, BorderLayout.SOUTH);

        // Regresar y cerrar cámara
        btnVolver.addActionListener(e -> cerrarEscaner());

        // Liberar cámara al cerrar la ventana
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

            // Esperar a que la cámara se abra
            while (escaneando && !webcam.isOpen()) {
                Thread.sleep(100);
            }

            // Leer códigos QR
            while (escaneando && webcam.isOpen()) {

                Thread.sleep(150);

                if (!escaneando) {
                    break;
                }

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

                        // Evitar procesar códigos al cerrar
                        if (cerrando) {
                            return;
                        }

                        cerrarEscaner();

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

            if (escaneando && !cerrando) {

                SwingUtilities.invokeLater(() ->
                        mostrarError(
                                "Error al escanear: " + e.getMessage()
                        )
                );
            }
        }
    }

    // Cerrar escáner y regresar a la ventana anterior
    private void cerrarEscaner() {
        if (cerrando) {
            return;
        }

        cerrando = true;
        detenerEscaneo();
        dispose();
    }

    // Detener lectura y liberar cámara
    private void detenerEscaneo() {
        escaneando = false;

        if (executor != null) {
            executor.shutdownNow();
        }

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

            if (cerrando) {
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Escáner QR",
                    JOptionPane.ERROR_MESSAGE
            );

            cerrarEscaner();
        });
    }
}
