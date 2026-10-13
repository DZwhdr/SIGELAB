package org.example;

import org.example.view.EquipoView;
import org.example.view.LaboratorioView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JTabbedPane pestanas = new JTabbedPane();

            pestanas.addTab("Laboratorios", new LaboratorioView());
            pestanas.addTab("Equipos", new EquipoView());

            JFrame ventana = new JFrame("SIGELAB");
            ventana.setContentPane(pestanas);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(1100, 750);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}