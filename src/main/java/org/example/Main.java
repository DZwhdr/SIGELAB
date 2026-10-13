package org.example;

import org.example.view.LaboratorioView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("SIGELAB - Laboratorios");

            ventana.setContentPane(new LaboratorioView());
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(900, 600);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}