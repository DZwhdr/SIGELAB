package com.sigelab.view;

import com.sigelab.repository.ReporteDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class ReportesView extends JFrame {

    private final ReporteDAO dao;

    public ReportesView() {

        dao = new ReporteDAO();

        setTitle("SIGELAB - Reportes y Consultas");
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(
                "Préstamos por Fecha",
                crearPanelPrestamosFecha()
        );

        tabbedPane.addTab(
                "Historial de Equipo",
                crearPanelHistorialEquipo()
        );

        tabbedPane.addTab(
                "Historial de Mantenimiento",
                crearPanelMantenimiento()
        );

        add(tabbedPane);
    }

    private DefaultTableModel crearModelo(String[] columnas) {

        return new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setAutoCreateRowSorter(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabla.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        return tabla;
    }

    // PRÉSTAMOS POR FECHA

    private JPanel crearPanelPrestamosFecha() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtInicio = new JTextField(10);
        JTextField txtFin = new JTextField(10);

        JButton btnBuscar = new JButton("Buscar");

        filtros.add(new JLabel("Fecha Inicio (YYYY-MM-DD):"));
        filtros.add(txtInicio);

        filtros.add(new JLabel("Fecha Fin (YYYY-MM-DD):"));
        filtros.add(txtFin);

        filtros.add(btnBuscar);

        String[] columnas = {
                "ID Préstamo",
                "Solicitante",
                "Código Inventario",
                "Equipo",
                "Fecha Préstamo",
                "Devolución Esperada",
                "Estado",
                "Encargado"
        };

        DefaultTableModel modelo = crearModelo(columnas);
        JTable tabla = crearTabla(modelo);

        btnBuscar.addActionListener(e -> {

            try {

                Date inicio = Date.valueOf(txtInicio.getText().trim());
                Date fin = Date.valueOf(txtFin.getText().trim());

                if (inicio.after(fin)) {
                    JOptionPane.showMessageDialog(
                            panel,
                            "La fecha inicial no puede ser posterior a la final."
                    );
                    return;
                }

                modelo.setRowCount(0);

                List<Object[]> datos =
                        dao.obtenerPrestamosEntreFechas(inicio, fin);

                for (Object[] fila : datos) {
                    modelo.addRow(fila);
                }

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        panel,
                        "Formato de fecha inválido. Use YYYY-MM-DD.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    // HISTORIAL POR CÓDIGO DE INVENTARIO

    private JPanel crearPanelHistorialEquipo() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtCodigo = new JTextField(18);
        JButton btnBuscar = new JButton("Buscar Equipo");

        filtros.add(new JLabel("Código de Inventario:"));
        filtros.add(txtCodigo);
        filtros.add(btnBuscar);

        String[] columnas = {
                "Código Inventario",
                "Equipo",
                "ID Préstamo",
                "Solicitante",
                "Fecha Préstamo",
                "Fecha Devolución",
                "Condición"
        };

        DefaultTableModel modelo = crearModelo(columnas);
        JTable tabla = crearTabla(modelo);

        btnBuscar.addActionListener(e -> {

            String codigo = txtCodigo.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Ingrese el código de inventario."
                );
                return;
            }

            modelo.setRowCount(0);

            List<Object[]> datos = dao.obtenerHistorialEquipo(codigo);

            for (Object[] fila : datos) {
                modelo.addRow(fila);
            }

        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    // HISTORIAL DE MANTENIMIENTO

    private JPanel crearPanelMantenimiento() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefrescar = new JButton(
                "Cargar Todo el Historial"
        );

        filtros.add(btnRefrescar);

        String[] columnas = {
                "ID",
                "Código Inventario",
                "Equipo",
                "Fecha Ingreso",
                "Tipo",
                "Estado",
                "Resultado"
        };

        DefaultTableModel modelo = crearModelo(columnas);
        JTable tabla = crearTabla(modelo);

        btnRefrescar.addActionListener(e -> {

            modelo.setRowCount(0);

            List<Object[]> datos =
                    dao.obtenerHistorialMantenimiento();

            for (Object[] fila : datos) {
                modelo.addRow(fila);
            }
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }
}