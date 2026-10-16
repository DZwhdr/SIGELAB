package com.sigelab

.view;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;

public final class TemaWindowsClasico {

    public static final Color AZUL = new Color(58, 110, 165);
    public static final Color AZUL_OSCURO = new Color(10, 36, 106);
    public static final Color GRIS_CLARO = new Color(212, 208, 200);
    public static final Color BLANCO = Color.WHITE;
    public static final Color NEGRO = Color.BLACK;

    private static final Font FUENTE =
            new Font("Tahoma", Font.PLAIN, 12);

    private TemaWindowsClasico() {
    }

    public static void aplicar(Component componente) {
        componente.setFont(FUENTE);

        if (componente instanceof JPanel panel) {
            panel.setBackground(GRIS_CLARO);
        }

        if (componente instanceof JButton boton) {
            boton.setBackground(GRIS_CLARO);
            boton.setForeground(NEGRO);
            boton.setFocusPainted(false);
            boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AZUL_OSCURO),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
        }

        if (componente instanceof JTextComponent texto) {
            texto.setBackground(BLANCO);
            texto.setForeground(NEGRO);
            texto.setCaretColor(NEGRO);
            texto.setBorder(BorderFactory.createLineBorder(AZUL));
        }

        if (componente instanceof JComboBox<?> combo) {
            combo.setBackground(BLANCO);
            combo.setForeground(NEGRO);
        }

        if (componente instanceof JSpinner spinner) {
            spinner.setBackground(BLANCO);
            spinner.setForeground(NEGRO);
        }

        if (componente instanceof JScrollPane scroll) {
            scroll.getViewport().setBackground(BLANCO);
            scroll.setBorder(BorderFactory.createLineBorder(AZUL_OSCURO));
        }

        if (componente instanceof Container contenedor) {
            for (Component hijo : contenedor.getComponents()) {
                aplicar(hijo);
            }
        }
    }

    public static void aplicarTitulo(JLabel titulo) {
        titulo.setForeground(AZUL_OSCURO);
        titulo.setFont(new Font("Tahoma", Font.BOLD, 18));
    }

    public static void aplicarTabla(JTable tabla) {
        tabla.setBackground(BLANCO);
        tabla.setForeground(NEGRO);
        tabla.setGridColor(AZUL);
        tabla.setSelectionBackground(AZUL_OSCURO);
        tabla.setSelectionForeground(BLANCO);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(AZUL);
        encabezado.setForeground(BLANCO);
        encabezado.setFont(new Font("Tahoma", Font.BOLD, 12));
        encabezado.setReorderingAllowed(false);
    }
}