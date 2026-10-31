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
import java.util.regex.Pattern;

public class AuditoriaView extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private TableRowSorter<DefaultTableModel> sorter;

    public AuditoriaView() {

        setTitle("SIGELAB - Auditoría y Movimientos");
        setSize(1250, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 15, 10)
        );

        pnlTop.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        JLabel lblBuscar = new JLabel(
                "Buscar usuario, acción, código o equipo:"
        );

        txtBuscar = new JTextField(25);

        JButton btnRefrescar = new JButton("Refrescar Datos");
        JButton btnExportar = new JButton("Exportar a CSV");

        pnlTop.add(lblBuscar);
        pnlTop.add(txtBuscar);
        pnlTop.add(btnRefrescar);
        pnlTop.add(btnExportar);

        add(pnlTop, BorderLayout.NORTH);

        String[] columnas = {
                "ID",
                "Fecha y Hora",
                "Usuario",
                "Acción",
                "Código de Inventario",
                "Equipo",
                "Detalle"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabla.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        int[] anchos = {
                60, 160, 170, 170, 180, 180, 400
        };

        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        btnRefrescar.addActionListener(e -> cargarDatos());

        txtBuscar.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }
                }
        );

        btnExportar.addActionListener(e -> exportarCSV());

        cargarDatos();
    }

    private void cargarDatos() {

        modeloTabla.setRowCount(0);

        AuditoriaDAO dao = new AuditoriaDAO();

        List<AuditoriaMovimiento> movimientos =
                dao.obtenerHistorialCompleto();

        for (AuditoriaMovimiento mov : movimientos) {

            modeloTabla.addRow(new Object[]{
                    mov.getId(),
                    mov.getFecha(),
                    mov.getUsuarioResponsable(),
                    mov.getTipoMovimiento(),
                    mostrarValor(mov.getCodigoInventario()),
                    mostrarValor(mov.getNombreEquipo()),
                    mov.getDetalle()
            });
        }
    }

    private String mostrarValor(String valor) {

        if (valor == null || valor.isBlank()) {
            return "No aplica";
        }

        return valor;
    }

    private void filtrar() {

        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(texto)
                    )
            );
        }
    }

    private String escaparCSV(Object valor) {

        String texto = valor == null
                ? ""
                : valor.toString();

        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    private void exportarCSV() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle(
                "Guardar reporte de auditoría"
        );

        fileChooser.setSelectedFile(
                new java.io.File("Reporte_Auditoria.csv")
        );

        if (fileChooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileWriter writer = new FileWriter(
                fileChooser.getSelectedFile())) {

            for (int i = 0; i < modeloTabla.getColumnCount(); i++) {

                writer.write(
                        escaparCSV(modeloTabla.getColumnName(i))
                );

                if (i < modeloTabla.getColumnCount() - 1) {
                    writer.write(",");
                }
            }

            writer.write("\n");

            for (int i = 0; i < tabla.getRowCount(); i++) {

                int filaModelo =
                        tabla.convertRowIndexToModel(i);

                for (int j = 0;
                     j < modeloTabla.getColumnCount();
                     j++) {

                    writer.write(
                            escaparCSV(
                                    modeloTabla.getValueAt(
                                            filaModelo, j
                                    )
                            )
                    );

                    if (j < modeloTabla.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }

                writer.write("\n");
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Reporte exportado con éxito.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al exportar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}