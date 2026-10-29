
package com.sigelab.view;

import com.sigelab.controller.LaboratorioController;
import com.sigelab.model.Laboratorio;

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
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnRegresar = new JButton("Regresar al menú");
    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Código",
                            "Nombre",
                            "Ubicación",
                            "Capacidad",
                            "Estado"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modeloTabla);

    private boolean ocupado = false;
    private final boolean esAdmin;

    public LaboratorioView() {
        this(false);
    }

    public LaboratorioView(boolean esAdmin) {
        this.esAdmin = esAdmin;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "ADMINISTRACIÓN DE LABORATORIOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel formulario = new JPanel(
                new GridLayout(6, 2, 10, 8)
        );

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

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnBuscar);

        btnEliminar.setVisible(esAdmin);
        botones.add(btnEliminar);

        botones.add(btnRegresar);
        JPanel parteSuperior = new JPanel(
                new BorderLayout(10, 15)
        );

        parteSuperior.add(titulo, BorderLayout.NORTH);
        parteSuperior.add(formulario, BorderLayout.CENTER);
        parteSuperior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setAutoCreateRowSorter(true);

        add(parteSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Aplicar el tema si la clase está disponible.
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
        btnEliminar.addActionListener(evento -> eliminar());
        btnRegresar.addActionListener(evento -> {
            java.awt.Window ventana = SwingUtilities.getWindowAncestor(this);

            if (ventana != null) {
                ventana.dispose();
            }
        });

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

            int capacidad = ((Number) spnCapacidad.getValue())
                    .intValue();

            if (capacidad <= 0) {
                throw new IllegalArgumentException(
                        "La capacidad debe ser mayor que cero."
                );
            }

            return capacidad;

        } catch (ParseException error) {
            throw new IllegalArgumentException(
                    "Escribe una capacidad válida mayor que cero."
            );
        }
    }

    private void guardar() {
        try {
            String codigo = txtCodigo.getText().strip();
            String nombre = txtNombre.getText().strip();
            String ubicacion = txtUbicacion.getText().strip();
            int capacidad = leerCapacidad();
            String estado = (String) cmbEstado.getSelectedItem();

            validarFormulario(codigo, nombre, ubicacion, estado);

            ejecutar(
                    () -> controller.guardar(
                            codigo,
                            nombre,
                            ubicacion,
                            capacidad,
                            estado
                    ),
                    laboratorio -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Laboratorio guardado correctamente. ID: "
                                        + laboratorio.getId(),
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE
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

            String codigo = txtCodigo.getText().strip();
            String nombre = txtNombre.getText().strip();
            String ubicacion = txtUbicacion.getText().strip();
            int capacidad = leerCapacidad();
            String estado = (String) cmbEstado.getSelectedItem();

            validarFormulario(codigo, nombre, ubicacion, estado);

            ejecutar(
                    () -> {
                        controller.actualizar(
                                id,
                                codigo,
                                nombre,
                                ubicacion,
                                capacidad,
                                estado
                        );

                        return true;
                    },
                    resultado -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Laboratorio actualizado correctamente.",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE
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
                this,
                "Escribe el código del laboratorio:"
        );

        if (codigo == null) {
            return;
        }

        if (codigo.isBlank()) {
            mostrarError(
                    new IllegalArgumentException(
                            "Debes escribir un código para buscar."
                    )
            );
            return;
        }

        ejecutar(
                () -> controller.buscarPorCodigo(codigo.strip()),
                laboratorio -> {
                    if (laboratorio == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "No existe un laboratorio con ese código.",
                                "Sin resultados",
                                JOptionPane.INFORMATION_MESSAGE
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

        Laboratorio laboratorio = new Laboratorio(
                ((Number) modeloTabla.getValueAt(fila, 0)).intValue(),
                (String) modeloTabla.getValueAt(fila, 1),
                (String) modeloTabla.getValueAt(fila, 2),
                (String) modeloTabla.getValueAt(fila, 3),
                ((Number) modeloTabla.getValueAt(fila, 4)).intValue(),
                (String) modeloTabla.getValueAt(fila, 5)
        );

        mostrarLaboratorio(laboratorio);
    }

    private void mostrarLaboratorio(Laboratorio laboratorio) {
        txtId.setText(String.valueOf(laboratorio.getId()));
        txtCodigo.setText(laboratorio.getCodigo());
        txtNombre.setText(laboratorio.getNombre());
        txtUbicacion.setText(laboratorio.getUbicacion());

        spnCapacidad.setValue(
                Math.max(1, laboratorio.getCapacidad())
        );

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

    private void validarFormulario(
            String codigo,
            String nombre,
            String ubicacion,
            String estado
    ) {
        if (codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del laboratorio es obligatorio."
            );
        }

        if (nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del laboratorio es obligatorio."
            );
        }

        if (ubicacion.isBlank()) {
            throw new IllegalArgumentException(
                    "La ubicación del laboratorio es obligatoria."
            );
        }

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "Selecciona el estado del laboratorio."
            );
        }
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
                    T resultado = get();
                    alTerminar.accept(resultado);

                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    mostrarError(error);

                } catch (ExecutionException error) {
                    Throwable causa = error.getCause();

                    mostrarError(
                            causa != null ? causa : error
                    );
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
                mensaje == null
                        ? "Ocurrió un error inesperado."
                        : mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Eliminar el laboratorio seleccionado
    private void eliminar() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(
                    this,
                    "Esta función requiere privilegios de administrador.",
                    "Permisos insuficientes",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String textoId = txtId.getText().trim();

        if (textoId.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un laboratorio de la tabla.",
                    "Laboratorio no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el laboratorio seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(textoId);
            boolean eliminado = controller.eliminar(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Laboratorio eliminado correctamente."
                );

                cargarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el laboratorio.",
                        "No eliminado",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (SQLException | IllegalArgumentException error) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar el laboratorio.\n"
                            + error.getMessage(),
                    "Error al eliminar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
