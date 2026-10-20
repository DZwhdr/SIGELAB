
package org.example.view;

import org.example.controller.TipoEquipoController;
import org.example.model.TipoEquipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TipoEquipoForm extends JFrame {

    private final TipoEquipoController controller;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTable tablaTipos;
    private DefaultTableModel tableModel;

    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnDarDeBaja;

    public TipoEquipoForm() {
        this.controller = new TipoEquipoController();
        initComponents();
        cargarDatosTabla();
    }

    private void initComponents() {
        setTitle("SIGELAB - Gestión de Tipos de Equipo");
        setSize(700, 450);
        setMinimumSize(new Dimension(650, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(
                "Datos del Tipo de Equipo"
        ));

        txtId = new JTextField();
        txtId.setEditable(false);

        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Descripción:"));
        panelForm.add(txtDescripcion);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnDarDeBaja = new JButton("Dar de Baja");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnDarDeBaja);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelForm, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTipos = new JTable(tableModel);
        tablaTipos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(tablaTipos);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Listado de Tipos de Equipo Activos"
        ));

        add(scrollPane, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnDarDeBaja.addActionListener(e -> darDeBaja());

        tablaTipos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tablaTipos.getSelectedRow() != -1) {

                int fila = tablaTipos.convertRowIndexToModel(
                        tablaTipos.getSelectedRow()
                );

                txtId.setText(
                        tableModel.getValueAt(fila, 0).toString()
                );
                txtNombre.setText(
                        tableModel.getValueAt(fila, 1).toString()
                );

                Object descripcion = tableModel.getValueAt(fila, 2);
                txtDescripcion.setText(
                        descripcion != null
                                ? descripcion.toString()
                                : ""
                );
            }
        });
    }

    private void cargarDatosTabla() {
        try {
            List<TipoEquipo> lista = controller.listarActivos();

            tableModel.setRowCount(0);

            for (TipoEquipo tipo : lista) {
                tableModel.addRow(new Object[]{
                        tipo.getId(),
                        tipo.getNombre(),
                        tipo.getDescripcion()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre es obligatorio.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            if (txtId.getText().isEmpty()) {
                controller.registrar(nombre, descripcion);

                JOptionPane.showMessageDialog(
                        this,
                        "Tipo de equipo registrado correctamente."
                );
            } else {
                int id = Integer.parseInt(txtId.getText());

                controller.actualizar(id, nombre, descripcion);

                JOptionPane.showMessageDialog(
                        this,
                        "Tipo de equipo actualizado correctamente."
                );
            }

            limpiarFormulario();
            cargarDatosTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void darDeBaja() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un tipo de equipo de la tabla.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de dar de baja este tipo de equipo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());

            controller.darDeBaja(id);

            JOptionPane.showMessageDialog(
                    this,
                    "Tipo de equipo dado de baja correctamente."
            );

            limpiarFormulario();
            cargarDatosTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al dar de baja: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        tablaTipos.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TipoEquipoForm().setVisible(true)
        );
    }
}
