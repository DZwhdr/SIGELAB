package com.sigelab;

import com.sigelab.view.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("¡SIGELAB iniciado con éxito!");
        com.sigelab.repository.InicializadorBD.crearTablas();

        // Usamos SwingUtilities.invokeLater para asegurar que la interfaz gráfica
        // se cargue de forma segura en el hilo de eventos de Java (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true); // Muestra la ventana de Login al arrancar el programa
        });
    }
}