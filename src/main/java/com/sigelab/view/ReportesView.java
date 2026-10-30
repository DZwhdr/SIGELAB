package com.sigelab.view;

import com.sigelab.repository.ReporteDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class ReportesView extends JFrame {

    private ReporteDAO dao;

    public ReportesView() {
        dao = new ReporteDAO();
        setTitle("SIGELAB - Reportes y Consultas");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Préstamos por Fecha", crearPanelPrestamosFecha());
        tabbedPane.addTab("Historial de Equipo", crearPanelHistorialEquipo());
        tabbedPane.addTab("Historial de Mantenimiento", crearPanelMantenimiento());

        add(tabbedPane);
    }

    // --- PESTAÑA 1: Préstamos entre fechas ---
    private JPanel crearPanelPrestamosFecha() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtInicio = new JTextField(10);
        JTextField txtFin = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");

        filtros.add(new JLabel("Fecha Inicio (YYYY-MM-DD):")); filtros.add(txtInicio);
        filtros.add(new JLabel("Fecha Fin (YYYY-MM-DD):")); filtros.add(txtFin);
        filtros.add(btnBuscar);

        String[] cols = {"ID", "Solicitante", "Fecha Préstamo", "Devolución Esperada", "Estado", "Encargado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        btnBuscar.addActionListener(e -> {
            try {
                Date inicio = Date.valueOf(txtInicio.getText());
                Date fin = Date.valueOf(txtFin.getText());
                modelo.setRowCount(0);
                List<Object[]> datos = dao.obtenerPrestamosEntreFechas(inicio, fin);
                for (Object[] fila : datos) modelo.addRow(fila);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel, "Formato de fecha inválido. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // --- PESTAÑA 2: Historial de un equipo ---
    private JPanel crearPanelHistorialEquipo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtIdEquipo = new JTextField(5);
        JButton btnBuscar = new JButton("Buscar Equipo");

        filtros.add(new JLabel("ID del Equipo:")); filtros.add(txtIdEquipo); filtros.add(btnBuscar);

        String[] cols = {"ID Préstamo", "Solicitante", "Fecha Préstamo", "Fecha Devolución", "Condición"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdEquipo.getText());
                modelo.setRowCount(0);
                List<Object[]> datos = dao.obtenerHistorialEquipo(id);
                for (Object[] fila : datos) modelo.addRow(fila);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // --- PESTAÑA 3: Historial de Mantenimiento ---
    private JPanel crearPanelMantenimiento() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefrescar = new JButton("Cargar Todo el Historial");
        filtros.add(btnRefrescar);

        String[] cols = {"ID", "Equipo", "Fecha Ingreso", "Tipo", "Estado", "Resultado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0);
        JTable tabla = new JTable(modelo);

        btnRefrescar.addActionListener(e -> {
            modelo.setRowCount(0);
            List<Object[]> datos = dao.obtenerHistorialMantenimiento();
            for (Object[] fila : datos) modelo.addRow(fila);
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }
}