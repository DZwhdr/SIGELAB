package com.sigelab.view;

import com.sigelab.controller.MantenimientoController;
import com.sigelab.model.Mantenimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MantenimientoView extends JFrame {

    private final MantenimientoController controller;

    private JTextField txtEquipoId;
    private JTextField txtMotivo;
    private JComboBox<String> cmbEstado;
    private JTextArea txtObservaciones;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = -1;

    public MantenimientoView() {
        controller = new MantenimientoController();

        setTitle("SIGELAB - Gestión de Mantenimiento");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearInterfaz();
        cargarTabla();
    }

    private void crearInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "GESTIÓN DE MANTENIMIENTO",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));
        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos del mantenimiento")
        );

        txtEquipoId = new JTextField();
        txtMotivo = new JTextField();

        cmbEstado = new JComboBox<>(new String[]{
                "EN_PROCESO",
                "FINALIZADO"
        });

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        formulario.add(new JLabel("ID del equipo:"));
        formulario.add(txtEquipoId);

        formulario.add(new JLabel("Motivo:"));
        formulario.add(txtMotivo);

        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);

        formulario.add(new JLabel("Observaciones:"));
        formulario.add(new JScrollPane(txtObservaciones));

        contenido.add(formulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Equipo",
                        "Fecha inicio",
                        "Fecha fin",
                        "Motivo",
                        "Estado",
                        "Observaciones"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        JButton btnGuardar = crearBoton(
                "Guardar",
                new Color(39, 174, 96)
        );

        JButton btnActualizar = crearBoton(
                "Actualizar",
                new Color(41, 128, 185)
        );

        JButton btnEliminar = crearBoton(
                "Eliminar",
                new Color(192, 57, 43)
        );

        JButton btnLimpiar = crearBoton(
                "Limpiar",
                new Color(127, 140, 141)
        );

        JButton btnVolver = crearBoton(
                "Volver al menú",
                new Color(52, 73, 94)
        );

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnVolver);

        contenido.add(panelBotones, BorderLayout.SOUTH);
        panelPrincipal.add(contenido, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setPreferredSize(new Dimension(145, 38));
        return boton;
    }

    private void guardar() {
        try {
            int equipoId = leerNumero(
                    txtEquipoId,
                    "ID del equipo"
            );

            boolean guardado = controller.guardar(
                    equipoId,
                    txtMotivo.getText().trim(),
                    cmbEstado.getSelectedItem().toString(),
                    txtObservaciones.getText().trim()
            );

            if (guardado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mantenimiento registrado correctamente."
                );

                limpiarFormulario();
                cargarTabla();
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un mantenimiento de la tabla."
            );
            return;
        }

        try {
            int equipoId = leerNumero(
                    txtEquipoId,
                    "ID del equipo"
            );

            boolean actualizado = controller.actualizar(
                    idSeleccionado,
                    equipoId,
                    txtMotivo.getText().trim(),
                    cmbEstado.getSelectedItem().toString(),
                    txtObservaciones.getText().trim()
            );

            if (actualizado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mantenimiento actualizado correctamente."
                );

                limpiarFormulario();
                cargarTabla();
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un mantenimiento de la tabla."
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el mantenimiento seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controller.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mantenimiento eliminado correctamente."
                );

                limpiarFormulario();
                cargarTabla();
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        try {
            List<Mantenimiento> mantenimientos = controller.listar();

            for (Mantenimiento mantenimiento : mantenimientos) {
                modeloTabla.addRow(new Object[]{
                        mantenimiento.getId(),
                        mantenimiento.getEquipoId(),
                        mantenimiento.getFechaInicio(),
                        mantenimiento.getFechaFin(),
                        mantenimiento.getMotivo(),
                        mantenimiento.getEstado(),
                        mantenimiento.getObservaciones()
                });
            }
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        idSeleccionado = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        txtEquipoId.setText(
                modeloTabla.getValueAt(fila, 1).toString()
        );

        txtMotivo.setText(
                modeloTabla.getValueAt(fila, 4).toString()
        );

        cmbEstado.setSelectedItem(
                modeloTabla.getValueAt(fila, 5).toString()
        );

        Object observaciones = modeloTabla.getValueAt(fila, 6);

        txtObservaciones.setText(
                observaciones == null
                        ? ""
                        : observaciones.toString()
        );
    }

    private int leerNumero(JTextField campo, String nombre) {
        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(
                    nombre + " es obligatorio."
            );
        }

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    nombre + " debe ser un número."
            );
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtEquipoId.setText("");
        txtMotivo.setText("");
        cmbEstado.setSelectedIndex(0);
        txtObservaciones.setText("");
        tabla.clearSelection();
        txtEquipoId.requestFocus();
    }

    private void mostrarError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}