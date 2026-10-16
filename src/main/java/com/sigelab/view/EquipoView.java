package com.sigelab.view;

import com.sigelab

.controller.EquipoController;
import com.sigelab

.enums.EstadoEquipo;
import com.sigelab

.model.Equipo;
import com.sigelab

.model.Laboratorio;
import com.sigelab

.model.TipoEquipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class EquipoView extends JPanel {

    private final EquipoController controller = new EquipoController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtMarca = new JTextField();
    private final JTextField txtModelo = new JTextField();
    private final JTextField txtSerie = new JTextField();
    private final JTextField txtObservaciones = new JTextField();

    private final JComboBox<TipoEquipo> cmbTipo = new JComboBox<>();
    private final JComboBox<Laboratorio> cmbLaboratorio = new JComboBox<>();
    private final JComboBox<EstadoEquipo> cmbEstado =
            new JComboBox<>(EstadoEquipo.values());

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnBuscar = new JButton("Buscar por código");
    private final JButton btnRecargar = new JButton("Recargar");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{
                    "ID", "Código", "Nombre", "Tipo",
                    "Marca", "Modelo", "Serie", "Laboratorio", "Estado"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);
    private List<Equipo> equipos = new ArrayList<>();

    private int idSeleccionado = 0;
    private boolean ocupado = false;

    public EquipoView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(
                "ADMINISTRACIÓN DE EQUIPOS", SwingConstants.CENTER
        );
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        txtId.setEditable(false);

        JPanel formulario = new JPanel(new GridLayout(10, 2, 8, 5));
        agregarCampo(formulario, "ID (automático):", txtId);
        agregarCampo(formulario, "Código de inventario:", txtCodigo);
        agregarCampo(formulario, "Nombre:", txtNombre);
        agregarCampo(formulario, "Tipo:", cmbTipo);
        agregarCampo(formulario, "Marca:", txtMarca);
        agregarCampo(formulario, "Modelo:", txtModelo);
        agregarCampo(formulario, "Serie:", txtSerie);
        agregarCampo(formulario, "Laboratorio:", cmbLaboratorio);
        agregarCampo(formulario, "Estado:", cmbEstado);
        agregarCampo(formulario, "Observaciones:", txtObservaciones);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnBuscar);
        botones.add(btnRecargar);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(24);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        TemaWindowsClasico.aplicar(this);
        TemaWindowsClasico.aplicarTitulo(titulo);
        TemaWindowsClasico.aplicarTabla(tabla);
        btnNuevo.addActionListener(evento -> limpiarFormulario());
        btnGuardar.addActionListener(evento -> guardar());
        btnActualizar.addActionListener(evento -> actualizar());
        btnBuscar.addActionListener(evento -> buscar());
        btnRecargar.addActionListener(evento -> cargarDatos());

        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && !ocupado) {
                int fila = tabla.getSelectedRow();

                if (fila >= 0) {
                    int indice = tabla.convertRowIndexToModel(fila);
                    mostrarEquipo(equipos.get(indice));
                }
            }
        });

        actualizarBotones();
        SwingUtilities.invokeLater(this::cargarDatos);
    }

    private void agregarCampo(
            JPanel formulario, String etiqueta, JComponent campo
    ) {
        formulario.add(new JLabel(etiqueta));
        formulario.add(campo);
    }

    private void cargarDatos() {
        ejecutar(() -> new Datos(
                controller.listarTipos(),
                controller.listarLaboratorios(),
                controller.listar()
        ), datos -> {
            cmbTipo.removeAllItems();
            for (TipoEquipo tipo : datos.tipos()) {
                cmbTipo.addItem(tipo);
            }

            cmbLaboratorio.removeAllItems();
            for (Laboratorio laboratorio : datos.laboratorios()) {
                cmbLaboratorio.addItem(laboratorio);
            }

            limpiarFormulario();
            equipos = datos.equipos();
            modeloTabla.setRowCount(0);

            for (Equipo equipo : equipos) {
                modeloTabla.addRow(new Object[]{
                        equipo.getId(),
                        equipo.getCodigoInventario(),
                        equipo.getNombre(),
                        equipo.getTipoEquipo().getNombre(),
                        equipo.getMarca(),
                        equipo.getModelo(),
                        equipo.getSerie(),
                        equipo.getLaboratorio().getCodigo(),
                        equipo.getEstado()
                });
            }
        });
    }

    private Equipo leerFormulario() {
        Equipo equipo = new Equipo();

        equipo.setId(idSeleccionado);
        equipo.setCodigoInventario(txtCodigo.getText());
        equipo.setNombre(txtNombre.getText());
        equipo.setTipoEquipo((TipoEquipo) cmbTipo.getSelectedItem());
        equipo.setMarca(txtMarca.getText());
        equipo.setModelo(txtModelo.getText());
        equipo.setSerie(txtSerie.getText());
        equipo.setLaboratorio(
                (Laboratorio) cmbLaboratorio.getSelectedItem()
        );
        equipo.setEstado((EstadoEquipo) cmbEstado.getSelectedItem());
        equipo.setObservaciones(txtObservaciones.getText());

        return equipo;
    }

    private void guardar() {
        Equipo equipo = leerFormulario();

        ejecutar(() -> {
            controller.guardar(equipo);
            return equipo.getId();
        }, id -> {
            JOptionPane.showMessageDialog(
                    this, "Equipo guardado. ID: " + id
            );
            cargarDatos();
        });
    }

    private void actualizar() {
        Equipo equipo = leerFormulario();

        ejecutar(() -> {
            controller.actualizar(equipo);
            return true;
        }, resultado -> {
            JOptionPane.showMessageDialog(
                    this, "Equipo actualizado correctamente."
            );
            cargarDatos();
        });
    }

    private void buscar() {
        String codigo = JOptionPane.showInputDialog(
                this, "Escribe el código de inventario:"
        );

        if (codigo == null) {
            return;
        }

        ejecutar(() -> controller.buscarPorCodigo(codigo), equipo -> {
            if (equipo == null) {
                JOptionPane.showMessageDialog(
                        this, "No existe un equipo con ese código."
                );
            } else {
                tabla.clearSelection();
                mostrarEquipo(equipo);
            }
        });
    }

    private void mostrarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();

        txtId.setText(String.valueOf(equipo.getId()));
        txtCodigo.setText(equipo.getCodigoInventario());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca());
        txtModelo.setText(equipo.getModelo());
        txtSerie.setText(equipo.getSerie());
        txtObservaciones.setText(equipo.getObservaciones());
        cmbEstado.setSelectedItem(equipo.getEstado());

        seleccionarTipo(equipo.getTipoEquipo());
        seleccionarLaboratorio(equipo.getLaboratorio());
        actualizarBotones();
    }

    private void seleccionarTipo(TipoEquipo tipo) {
        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            if (cmbTipo.getItemAt(i).getId() == tipo.getId()) {
                cmbTipo.setSelectedIndex(i);
                return;
            }
        }

        cmbTipo.addItem(tipo);
        cmbTipo.setSelectedIndex(cmbTipo.getItemCount() - 1);
    }

    private void seleccionarLaboratorio(Laboratorio laboratorio) {
        for (int i = 0; i < cmbLaboratorio.getItemCount(); i++) {
            if (cmbLaboratorio.getItemAt(i).getId() == laboratorio.getId()) {
                cmbLaboratorio.setSelectedIndex(i);
                return;
            }
        }

        cmbLaboratorio.addItem(laboratorio);
        cmbLaboratorio.setSelectedIndex(
                cmbLaboratorio.getItemCount() - 1
        );
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;
        tabla.clearSelection();

        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtSerie.setText("");
        txtObservaciones.setText("");

        cmbTipo.setSelectedIndex(-1);
        cmbLaboratorio.setSelectedIndex(-1);
        cmbEstado.setSelectedItem(EstadoEquipo.DISPONIBLE);

        actualizarBotones();
    }

    private void actualizarBotones() {
        btnNuevo.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado && idSeleccionado == 0);
        btnActualizar.setEnabled(!ocupado && idSeleccionado > 0);
        btnBuscar.setEnabled(!ocupado);
        btnRecargar.setEnabled(!ocupado);

        tabla.setEnabled(!ocupado);
        txtCodigo.setEnabled(!ocupado);
        txtNombre.setEnabled(!ocupado);
        txtMarca.setEnabled(!ocupado);
        txtModelo.setEnabled(!ocupado);
        txtSerie.setEnabled(!ocupado);
        txtObservaciones.setEnabled(!ocupado);
        cmbTipo.setEnabled(!ocupado);
        cmbLaboratorio.setEnabled(!ocupado);
        cmbEstado.setEnabled(!ocupado);
    }

    private <T> void ejecutar(
            Callable<T> tarea, Consumer<T> alTerminar
    ) {
        if (ocupado) {
            return;
        }

        ocupado = true;
        actualizarBotones();

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                ocupado = false;
                actualizarBotones();

                try {
                    alTerminar.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError("La operación fue interrumpida.");
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();

                    if (causa instanceof SQLException sql
                            && (sql.getErrorCode() == 2601
                            || sql.getErrorCode() == 2627)) {
                        mostrarError(
                                "Ya existe un equipo con ese código."
                        );
                    } else {
                        mostrarError(causa.getMessage());
                    }
                } catch (RuntimeException ex) {
                    mostrarError(ex.getMessage());
                }
            }
        }.execute();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje == null ? "No se pudo completar la operación."
                        : mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private record Datos(
            List<TipoEquipo> tipos,
            List<Laboratorio> laboratorios,
            List<Equipo> equipos
    ) {
    }
}