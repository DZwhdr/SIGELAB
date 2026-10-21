package com.sigelab;

import com.sigelab.view.EquipoView;
import com.sigelab.view.LaboratorioView;

import javax.swing.*;

public class TestModuloKarla {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame(
                    "SIGELAB - Laboratorios y Equipos"
            );

            JTabbedPane pestanas = new JTabbedPane();
            pestanas.addTab(
                    "Laboratorios",
                    new LaboratorioView()
            );
            pestanas.addTab(
                    "Equipos",
                    new EquipoView()
            );

            ventana.add(pestanas);
            ventana.setSize(1100, 700);
            ventana.setLocationRelativeTo(null);
            ventana.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );
            ventana.setVisible(true);
        });
    }
}