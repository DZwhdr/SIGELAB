package com.sigelab.view;

import com.sigelab.controller.DevolucionController;
import com.sigelab.model.Devolucion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DevolucionView extends JFrame {

    private final DevolucionController controller;

    private JTextField txtEquipoId;
    private JTextField txtUsuarioId;
    private JComboBox<String> cmbEstadoEquipo;
    private JTextArea txtObservaciones;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = -1;

    public DevolucionView() {
        controller = new DevolucionController();

        setTitle("SIGELAB - Gestión de Devoluciones");
        setSize(950, 600);
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
                "GESTIÓN DE DEVOLUCIONES",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));
        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos de la devolución")
        );

        txtEquipoId = new JTextField();
        txtUsuarioId = new JTextField();

        cmbEstadoEquipo = new JComboBox<>(new String[]{
                "BUENO",
                "DAÑADO",
                "EN REVISIÓN"
        });

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        formulario.add(new JLabel("ID del equipo:"));
        formulario.add(txtEquipoId);

        formulario.add(new JLabel("ID del usuario:"));
        formulario.add(txtUsuarioId);

        formulario.add(new JLabel("Estado del equipo:"));
        formulario.add(cmbEstadoEquipo);

        formulario.add(new JLabel("Observaciones:"));
        formulario.add(new JScrollPane(txtObservaciones));

        contenido.add(formulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Equipo",
                        "Usuario",
                        "Fecha",
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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnGuardar = crearBoton("Guardar", new Color(39, 174, 96));
        JButton btnActualizar = crearBoton("Actualizar", new Color(41, 128, 185));
        JButton btnEliminar = crearBoton("Eliminar", new Color(192, 57, 43));
        JButton btnLimpiar = crearBoton("Limpiar", new Color(127, 140, 141));

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

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
        boton.setPreferredSize(new Dimension(130, 38));
        return boton;
    }

    private void guardar() {
        try {
            int equipoId = leerNumero(txtEquipoId, "ID del equipo");
            int usuarioId = leerNumero(txtUsuarioId, "ID del usuario");

            boolean guardado = controller.guardar(
                    equipoId,
                    usuarioId,
                    cmbEstadoEquipo.getSelectedItem().toString(),
                    txtObservaciones.getText().trim()
            );

            if (guardado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Devolución registrada correctamente."
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
                    "Seleccione una devolución de la tabla."
            );
            return;
        }

        try {
            int equipoId = leerNumero(txtEquipoId, "ID del equipo");
            int usuarioId = leerNumero(txtUsuarioId, "ID del usuario");

            boolean actualizado = controller.actualizar(
                    idSeleccionado,
                    equipoId,
                    usuarioId,
                    cmbEstadoEquipo.getSelectedItem().toString(),
                    txtObservaciones.getText().trim()
            );

            if (actualizado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Devolución actualizada correctamente."
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
                    "Seleccione una devolución de la tabla."
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la devolución seleccionada?",
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
                        "Devolución eliminada correctamente."
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
            List<Devolucion> devoluciones = controller.listar();

            for (Devolucion devolucion : devoluciones) {
                modeloTabla.addRow(new Object[]{
                        devolucion.getId(),
                        devolucion.getEquipoId(),
                        devolucion.getUsuarioId(),
                        devolucion.getFechaDevolucion(),
                        devolucion.getEstadoEquipo(),
                        devolucion.getObservaciones()
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

        txtUsuarioId.setText(
                modeloTabla.getValueAt(fila, 2).toString()
        );

        cmbEstadoEquipo.setSelectedItem(
                modeloTabla.getValueAt(fila, 4).toString()
        );

        Object observaciones = modeloTabla.getValueAt(fila, 5);
        txtObservaciones.setText(
                observaciones == null ? "" : observaciones.toString()
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
        txtUsuarioId.setText("");
        cmbEstadoEquipo.setSelectedIndex(0);
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