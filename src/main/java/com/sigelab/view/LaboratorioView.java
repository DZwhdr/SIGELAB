package com.sigelab

.view;

import com.sigelab

.controller.LaboratorioController;
import com.sigelab

.model.Laboratorio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class LaboratorioView extends JPanel {

    private final LaboratorioController controller =
            new LaboratorioController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtUbicacion = new JTextField();

    private final JSpinner spnCapacidad = new JSpinner(
            new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)
    );

    private final JComboBox<String> cmbEstado = new JComboBox<>(
            new String[]{"ACTIVO", "INACTIVO"}
    );

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnBuscar = new JButton("Buscar");

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new String[]{
                            "ID", "Código", "Nombre",
                            "Ubicación", "Capacidad", "Estado"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modeloTabla);
    private boolean ocupado;

    public LaboratorioView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "ADMINISTRACIÓN DE LABORATORIOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel formulario = new JPanel(new GridLayout(6, 2, 10, 8));

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Ubicación:"));
        formulario.add(txtUbicacion);
        formulario.add(new JLabel("Capacidad:"));
        formulario.add(spnCapacidad);
        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnBuscar);

        JPanel parteSuperior = new JPanel(new BorderLayout(10, 15));
        parteSuperior.add(titulo, BorderLayout.NORTH);
        parteSuperior.add(formulario, BorderLayout.CENTER);
        parteSuperior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(parteSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        TemaWindowsClasico.aplicar(this);
        TemaWindowsClasico.aplicarTitulo(titulo);
        TemaWindowsClasico.aplicarTabla(tabla);
        btnNuevo.addActionListener(evento -> {
            limpiarFormulario();
            cargarTabla();
        });

        btnGuardar.addActionListener(evento -> guardar());
        btnActualizar.addActionListener(evento -> actualizar());
        btnBuscar.addActionListener(evento -> buscar());

        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && !ocupado) {
                seleccionarFila();
            }
        });

        limpiarFormulario();

        SwingUtilities.invokeLater(this::cargarTabla);
    }

    private int leerCapacidad() {
        try {
            spnCapacidad.commitEdit();
            return ((Number) spnCapacidad.getValue()).intValue();
        } catch (ParseException error) {
            throw new IllegalArgumentException(
                    "Escribe una capacidad válida mayor que cero."
            );
        }
    }

    private void guardar() {
        try {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String ubicacion = txtUbicacion.getText();
            int capacidad = leerCapacidad();
            String estado = (String) cmbEstado.getSelectedItem();

            ejecutar(
                    () -> controller.guardar(
                            codigo, nombre, ubicacion, capacidad, estado
                    ),
                    laboratorio -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Laboratorio guardado. ID: "
                                        + laboratorio.getId()
                        );
                        limpiarFormulario();
                        cargarTabla();
                    }
            );
        } catch (IllegalArgumentException error) {
            mostrarError(error);
        }
    }

    private void actualizar() {
        try {
            if (txtId.getText().isBlank()) {
                throw new IllegalArgumentException(
                        "Selecciona un laboratorio de la tabla."
                );
            }

            int id = Integer.parseInt(txtId.getText());
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String ubicacion = txtUbicacion.getText();
            int capacidad = leerCapacidad();
            String estado = (String) cmbEstado.getSelectedItem();

            ejecutar(
                    () -> {
                        controller.actualizar(
                                id, codigo, nombre,
                                ubicacion, capacidad, estado
                        );
                        return true;
                    },
                    resultado -> {
                        JOptionPane.showMessageDialog(
                                this, "Laboratorio actualizado."
                        );
                        limpiarFormulario();
                        cargarTabla();
                    }
            );
        } catch (IllegalArgumentException error) {
            mostrarError(error);
        }
    }

    private void buscar() {
        String codigo = JOptionPane.showInputDialog(
                this, "Escribe el código del laboratorio:"
        );

        if (codigo == null) {
            return;
        }

        ejecutar(
                () -> controller.buscarPorCodigo(codigo),
                laboratorio -> {
                    if (laboratorio == null) {
                        JOptionPane.showMessageDialog(
                                this, "No existe un laboratorio con ese código."
                        );
                    } else {
                        tabla.clearSelection();
                        mostrarLaboratorio(laboratorio);
                    }
                }
        );
    }

    private void cargarTabla() {
        ejecutar(controller::listar, this::mostrarTabla);
    }

    private void mostrarTabla(List<Laboratorio> laboratorios) {
        modeloTabla.setRowCount(0);

        for (Laboratorio laboratorio : laboratorios) {
            modeloTabla.addRow(new Object[]{
                    laboratorio.getId(),
                    laboratorio.getCodigo(),
                    laboratorio.getNombre(),
                    laboratorio.getUbicacion(),
                    laboratorio.getCapacidad(),
                    laboratorio.getEstado()
            });
        }
    }

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        fila = tabla.convertRowIndexToModel(fila);

        mostrarLaboratorio(new Laboratorio(
                ((Number) modeloTabla.getValueAt(fila, 0)).intValue(),
                (String) modeloTabla.getValueAt(fila, 1),
                (String) modeloTabla.getValueAt(fila, 2),
                (String) modeloTabla.getValueAt(fila, 3),
                ((Number) modeloTabla.getValueAt(fila, 4)).intValue(),
                (String) modeloTabla.getValueAt(fila, 5)
        ));
    }

    private void mostrarLaboratorio(Laboratorio laboratorio) {
        txtId.setText(String.valueOf(laboratorio.getId()));
        txtCodigo.setText(laboratorio.getCodigo());
        txtNombre.setText(laboratorio.getNombre());
        txtUbicacion.setText(laboratorio.getUbicacion());
        spnCapacidad.setValue(laboratorio.getCapacidad());
        cmbEstado.setSelectedItem(laboratorio.getEstado());
        actualizarControles();
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtUbicacion.setText("");
        spnCapacidad.setValue(1);
        cmbEstado.setSelectedItem("ACTIVO");
        actualizarControles();
        txtCodigo.requestFocusInWindow();
    }

    private void actualizarControles() {
        boolean seleccionado = !txtId.getText().isBlank();

        btnNuevo.setEnabled(!ocupado);
        btnBuscar.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado && !seleccionado);
        btnActualizar.setEnabled(!ocupado && seleccionado);

        txtCodigo.setEnabled(!ocupado);
        txtNombre.setEnabled(!ocupado);
        txtUbicacion.setEnabled(!ocupado);
        spnCapacidad.setEnabled(!ocupado);
        cmbEstado.setEnabled(!ocupado);
        tabla.setEnabled(!ocupado);
    }

    private <T> void ejecutar(
            Callable<T> tarea,
            Consumer<T> alTerminar
    ) {
        if (ocupado) {
            return;
        }

        ocupado = true;
        actualizarControles();

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                ocupado = false;
                actualizarControles();

                try {
                    alTerminar.accept(get());
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    mostrarError(error);
                } catch (ExecutionException error) {
                    mostrarError(error.getCause());
                }
            }
        }.execute();
    }

    private void mostrarError(Throwable error) {
        String mensaje = error.getMessage();

        if (error instanceof SQLException sqlError) {
            if (sqlError.getErrorCode() == 2601
                    || sqlError.getErrorCode() == 2627) {
                mensaje = "Ya existe un laboratorio con ese código.";
            }
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje == null ? "Ocurrió un error." : mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}