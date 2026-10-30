
package com.sigelab.view;

import com.sigelab.model.Equipo;
import com.sigelab.model.Laboratorio;
import com.sigelab.model.TipoEquipo;
import org.example.enums.EstadoEquipo;
import com.sigelab.controller.EquipoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class EquipoView extends JFrame {

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
        setTitle("SIGELAB - Administración de Equipos");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        JLabel titulo = new JLabel(
                "ADMINISTRACIÓN DE EQUIPOS",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

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

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnBuscar.addActionListener(e -> buscar());
        btnRecargar.addActionListener(e -> cargarDatos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !ocupado) {
                int fila = tabla.getSelectedRow();

                if (fila >= 0) {
                    int indice = tabla.convertRowIndexToModel(fila);

                    if (indice >= 0 && indice < equipos.size()) {
                        mostrarEquipo(equipos.get(indice));
                    }
                }
            }
        });

        actualizarBotones();
        SwingUtilities.invokeLater(this::cargarDatos);
    }

    private void agregarCampo(
            JPanel panel,
            String etiqueta,
            JComponent campo
    ) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    private void cargarDatos() {
        ejecutar(
                () -> new Datos(
                        controller.listarTipos(),
                        controller.listarLaboratorios(),
                        controller.listar()
                ),
                datos -> {
                    cmbTipo.removeAllItems();

                    for (TipoEquipo tipo : datos.tipos()) {
                        cmbTipo.addItem(tipo);
                    }

                    cmbLaboratorio.removeAllItems();

                    for (Laboratorio lab : datos.laboratorios()) {
                        cmbLaboratorio.addItem(lab);
                    }

                    equipos = datos.equipos();
                    modeloTabla.setRowCount(0);

                    for (Equipo eq : equipos) {
                        String nombreLab =
                                eq.getLaboratorio() != null
                                        ? eq.getLaboratorio().getCodigo()
                                        : "N/A";

                        modeloTabla.addRow(new Object[]{
                                eq.getId(),
                                eq.getCodigoInventario(),
                                eq.getNombre(),
                                eq.getTipoEquipo() != null
                                        ? eq.getTipoEquipo().getNombre()
                                        : "N/A",
                                eq.getMarca(),
                                eq.getModelo(),
                                eq.getSerie(),
                                nombreLab,
                                eq.getEstado()
                        });
                    }

                    limpiarFormulario();
                }
        );
    }

    private Equipo leerFormulario() {
        Equipo eq = new Equipo();

        eq.setId(idSeleccionado);
        eq.setCodigoInventario(txtCodigo.getText().trim());
        eq.setNombre(txtNombre.getText().trim());
        eq.setTipoEquipo((TipoEquipo) cmbTipo.getSelectedItem());
        eq.setMarca(txtMarca.getText().trim());
        eq.setModelo(txtModelo.getText().trim());
        eq.setSerie(txtSerie.getText().trim());
        eq.setLaboratorio(
                (Laboratorio) cmbLaboratorio.getSelectedItem()
        );
        eq.setEstado((EstadoEquipo) cmbEstado.getSelectedItem());
        eq.setObservaciones(txtObservaciones.getText().trim());

        return eq;
    }

    private void guardar() {
        Equipo eq = leerFormulario();

        ejecutar(() -> {
            controller.guardar(eq);
            return eq.getId();
        }, id -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Equipo guardado con éxito. ID: " + id
            );
            cargarDatos();
        });
    }

    private void actualizar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un equipo para actualizar."
            );
            return;
        }

        Equipo eq = leerFormulario();

        ejecutar(() -> {
            controller.actualizar(eq);
            return true;
        }, resultado -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Equipo actualizado correctamente."
            );
            cargarDatos();
        });
    }

    private void buscar() {
        String codigo = JOptionPane.showInputDialog(
                this,
                "Escribe el código de inventario:"
        );

        if (codigo == null || codigo.isBlank()) {
            return;
        }

        ejecutar(() -> controller.buscarPorCodigo(codigo.trim()), eq -> {
            if (eq == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No existe un equipo con ese código."
                );
            } else {
                tabla.clearSelection();
                mostrarEquipo(eq);
            }
        });
    }

    private void mostrarEquipo(Equipo eq) {
        idSeleccionado = eq.getId();

        txtId.setText(String.valueOf(eq.getId()));
        txtCodigo.setText(eq.getCodigoInventario());
        txtNombre.setText(eq.getNombre());
        txtMarca.setText(eq.getMarca());
        txtModelo.setText(eq.getModelo());
        txtSerie.setText(eq.getSerie());
        txtObservaciones.setText(eq.getObservaciones());

        cmbEstado.setSelectedItem(eq.getEstado());

        if (eq.getTipoEquipo() != null) {
            seleccionarTipo(eq.getTipoEquipo());
        } else {
            cmbTipo.setSelectedIndex(-1);
        }

        if (eq.getLaboratorio() != null) {
            seleccionarLaboratorio(eq.getLaboratorio());
        } else {
            cmbLaboratorio.setSelectedIndex(-1);
        }

        actualizarBotones();
    }

    private void seleccionarTipo(TipoEquipo tipo) {
        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            TipoEquipo actual = cmbTipo.getItemAt(i);

            if (actual.getId() == tipo.getId()) {
                cmbTipo.setSelectedIndex(i);
                return;
            }
        }

        cmbTipo.addItem(tipo);
        cmbTipo.setSelectedIndex(cmbTipo.getItemCount() - 1);
    }

    private void seleccionarLaboratorio(Laboratorio lab) {
        for (int i = 0; i < cmbLaboratorio.getItemCount(); i++) {
            Laboratorio actual = cmbLaboratorio.getItemAt(i);

            if (actual.getId() == lab.getId()) {
                cmbLaboratorio.setSelectedIndex(i);
                return;
            }
        }

        cmbLaboratorio.addItem(lab);
        cmbLaboratorio.setSelectedIndex(cmbLaboratorio.getItemCount() - 1);
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
    }

    private <T> void ejecutar(
            Callable<T> tarea,
            Consumer<T> alTerminar
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
                        mostrarError(
                                causa != null
                                        ? causa.getMessage()
                                        : ex.getMessage()
                        );
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
                mensaje == null ? "Ocurrió un error." : mensaje,
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
