package org.example.util;

import com.sigelab.view.ScannerQRDialog;

import javax.swing.*;

public class PruebaScannerQR {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ScannerQRDialog scanner = new ScannerQRDialog(
                    null,
                    codigo -> JOptionPane.showMessageDialog(
                            null,
                            "Código QR detectado:\n" + codigo,
                            "SIGELAB - Prueba QR",
                            JOptionPane.INFORMATION_MESSAGE
                    )
            );

            scanner.setVisible(true);
        });
    }
}
