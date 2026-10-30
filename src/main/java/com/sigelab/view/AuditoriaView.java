package com.sigelab.view;

import com.sigelab.model.AuditoriaMovimiento;
import com.sigelab.repository.AuditoriaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AuditoriaView extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private TableRowSorter<DefaultTableModel> sorter;

    public AuditoriaView() {
        setTitle("SIGELAB - Auditoría y Movimientos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR (Filtros y Botones) ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBuscar = new JLabel("Buscar (Usuario, Acción o Detalle):");
        txtBuscar = new JTextField(25);
        JButton btnRefrescar = new JButton("Refrescar Datos");
        JButton btnExportar = new JButton("Exportar a CSV");

        pnlTop.add(lblBuscar);
        pnlTop.add(txtBuscar);
        pnlTop.add(btnRefrescar);
        pnlTop.add(btnExportar);

        add(pnlTop, BorderLayout.NORTH);

        // --- TABLA CENTRAL ---
        String[] columnas = {"ID", "Fecha y Hora", "Usuario", "Acción (Tipo)", "Detalle"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla de solo lectura
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        // Ajustar el tamaño de la columna detalle para que sea más ancha
        tabla.getColumnModel().getColumn(4).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        // --- EVENTOS ---
        btnRefrescar.addActionListener(e -> cargarDatos());

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        btnExportar.addActionListener(e -> exportarCSV());

        // Cargar datos al abrir la ventana
        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        AuditoriaDAO dao = new AuditoriaDAO();
        List<AuditoriaMovimiento> movimientos = dao.obtenerHistorialCompleto();

        for (AuditoriaMovimiento mov : movimientos) {
            modeloTabla.addRow(new Object[]{
                    mov.getId(),
                    mov.getFecha(),
                    mov.getUsuarioResponsable(),
                    mov.getTipoMovimiento(),
                    mov.getDetalle()
            });
        }
    }

    private void filtrar() {
        String texto = txtBuscar.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de auditoría");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Auditoria.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    writer.write(modeloTabla.getColumnName(i) + (i == modeloTabla.getColumnCount() - 1 ? "" : ","));
                }
                writer.write("\n");

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                        writer.write(modeloTabla.getValueAt(i, j).toString().replace(",", ";") + (j == modeloTabla.getColumnCount() - 1 ? "" : ","));
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Reporte exportado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}