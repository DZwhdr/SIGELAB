package com.sigelab.view;

import com.sigelab.controller.PrestamoController;
import com.sigelab.model.DetallePrestamo;
import com.sigelab.model.Equipo;
import com.sigelab.model.Prestamo;
import com.sigelab.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoView extends JFrame {

    private final PrestamoController controller =
            new PrestamoController();

    private final Usuario usuarioLogueado;

    private final JTextField txtSolicitante = new JTextField();
    private final JTextField txtFechaDevolucion = new JTextField();
    private final JTextField txtCodigoEquipo = new JTextField();
    private final JTextArea txtObservaciones =
            new JTextArea(3, 20);

    private final List<Equipo> equiposSeleccionados =
            new ArrayList<>();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new String[]{
                            "Código de inventario",
                            "Equipo",
                            "Estado"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int fila,
                        int columna
                ) {
                    return false;
                }
            };

    private final JTable tablaEquipos = new JTable(modeloTabla);

    private final JButton btnAgregar =
            new JButton("Agregar equipo");

    private final JButton btnEscanear =
            new JButton("Escanear QR");

    private final JButton btnQuitar =
            new JButton("Quitar equipo");

    private final JButton btnConfirmar =
            new JButton("Confirmar préstamo");

    private final JButton btnLimpiar =
            new JButton("Limpiar");

    private final JButton btnVolver =
            new JButton("Volver");

    public PrestamoView(Usuario usuario) {

        if (usuario == null
                || usuario.getIdUsuario() <= 0
                || !usuario.isActivo()) {

            throw new IllegalArgumentException(
                    "Se necesita un usuario activo y autenticado."
            );
        }

        this.usuarioLogueado = usuario;

        setTitle("SIGELAB - Gestión de Préstamos");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearInterfaz();
        configurarEventos();
    }

    private void crearInterfaz() {

        JPanel principal = new JPanel(
                new BorderLayout(15, 15)
        );

        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        principal.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel(
                "GESTIÓN DE PRÉSTAMOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 60, 114));

        JLabel lblUsuario = new JLabel(
                "Registrado por: "
                        + usuarioLogueado.getNombre()
                        + " "
                        + usuarioLogueado.getApellido(),
                SwingConstants.RIGHT
        );

        lblUsuario.setForeground(new Color(90, 100, 110));

        JPanel encabezado = new JPanel(
                new BorderLayout(5, 8)
        );

        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(lblUsuario, BorderLayout.SOUTH);

        principal.add(encabezado, BorderLayout.NORTH);

        // Datos del préstamo
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBackground(Color.WHITE);

        panelDatos.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos del préstamo"
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        panelDatos.add(new JLabel("Solicitante:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panelDatos.add(txtSolicitante, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        panelDatos.add(
                new JLabel("Fecha de devolución:"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        txtFechaDevolucion.setToolTipText(
                "Formato: AAAA-MM-DD"
        );

        panelDatos.add(txtFechaDevolucion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        panelDatos.add(
                new JLabel("Observaciones:"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);

        panelDatos.add(
                new JScrollPane(txtObservaciones),
                gbc
        );

        // Equipos del préstamo
        JPanel panelEquipos = new JPanel(
                new BorderLayout(10, 10)
        );

        panelEquipos.setBackground(Color.WHITE);

        panelEquipos.setBorder(
                BorderFactory.createTitledBorder(
                        "Equipos del préstamo"
                )
        );

        JPanel panelBusqueda = new JPanel(
                new BorderLayout(10, 0)
        );

        panelBusqueda.setOpaque(false);

        panelBusqueda.add(
                new JLabel("Código de inventario:"),
                BorderLayout.WEST
        );

        panelBusqueda.add(
                txtCodigoEquipo,
                BorderLayout.CENTER
        );

        JPanel botonesBusqueda = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        botonesBusqueda.setOpaque(false);
        botonesBusqueda.add(btnAgregar);
        botonesBusqueda.add(btnEscanear);

        panelBusqueda.add(
                botonesBusqueda,
                BorderLayout.EAST
        );

        panelEquipos.add(
                panelBusqueda,
                BorderLayout.NORTH
        );

        tablaEquipos.setRowHeight(28);

        tablaEquipos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaEquipos.getTableHeader().setReorderingAllowed(false);

        panelEquipos.add(
                new JScrollPane(tablaEquipos),
                BorderLayout.CENTER
        );

        JPanel panelQuitar = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        panelQuitar.setOpaque(false);
        panelQuitar.add(btnQuitar);

        panelEquipos.add(
                panelQuitar,
                BorderLayout.SOUTH
        );

        JPanel centro = new JPanel(
                new BorderLayout(10, 15)
        );

        centro.setOpaque(false);

        centro.add(panelDatos, BorderLayout.NORTH);
        centro.add(panelEquipos, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);

        // Botones inferiores
        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );

        acciones.setOpaque(false);

        configurarBoton(
                btnAgregar,
                new Color(41, 128, 185)
        );

        configurarBoton(
                btnEscanear,
                new Color(142, 68, 173)
        );

        configurarBoton(
                btnQuitar,
                new Color(192, 57, 43)
        );

        configurarBoton(
                btnConfirmar,
                new Color(39, 174, 96)
        );

        configurarBoton(
                btnLimpiar,
                new Color(52, 73, 94)
        );

        configurarBoton(
                btnVolver,
                new Color(192, 57, 43)
        );

        acciones.add(btnLimpiar);
        acciones.add(btnVolver);
        acciones.add(btnConfirmar);

        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void configurarBoton(
            JButton boton,
            Color color
    ) {

        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);

        boton.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    private void configurarEventos() {

        btnAgregar.addActionListener(
                e -> agregarEquipo()
        );

        txtCodigoEquipo.addActionListener(
                e -> agregarEquipo()
        );

        btnEscanear.addActionListener(
                e -> escanearQR()
        );

        btnQuitar.addActionListener(
                e -> quitarEquipo()
        );

        btnLimpiar.addActionListener(
                e -> limpiarFormulario()
        );

        btnVolver.addActionListener(
                e -> dispose()
        );

        btnConfirmar.addActionListener(
                e -> confirmarPrestamo()
        );
    }

    // NUEVO: recibir equipo desde ConsultaEquipoQRForm
    public void agregarEquipoDesdeQR(String codigoInventario) {

        if (codigoInventario == null
                || codigoInventario.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El código del equipo está vacío.",
                    "Código incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String codigo = codigoInventario.trim();

        if (codigo.startsWith("SIGELAB-EQ:")) {
            codigo = codigo.substring(
                    "SIGELAB-EQ:".length()
            ).trim();
        }

        txtCodigoEquipo.setText(codigo);

        // Reutilizar el método existente
        agregarEquipo();

        txtSolicitante.requestFocusInWindow();
    }

    // Escanear QR desde la ventana de préstamos
    private void escanearQR() {

        ScannerQRDialog scanner = new ScannerQRDialog(
                this,
                codigoDetectado -> {

                    if (codigoDetectado == null
                            || codigoDetectado.isBlank()) {
                        return;
                    }

                    agregarEquipoDesdeQR(codigoDetectado);
                }
        );

        scanner.setLocationRelativeTo(this);
        scanner.setVisible(true);
    }

    // Buscar y agregar equipo por código de inventario
    private void agregarEquipo() {

        String codigo = txtCodigoEquipo.getText().trim();

        if (codigo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un código de inventario.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Equipo equipo =
                    controller.buscarEquipoDisponible(codigo);

            // Evitar equipos duplicados
            for (Equipo seleccionado : equiposSeleccionados) {

                if (seleccionado.getId() == equipo.getId()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Este equipo ya está agregado al préstamo.",
                            "Equipo duplicado",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
            }

            equiposSeleccionados.add(equipo);

            modeloTabla.addRow(new Object[]{
                    equipo.getCodigoInventario(),
                    equipo.getNombre(),
                    equipo.getEstado().name()
            });

            txtCodigoEquipo.setText("");

            JOptionPane.showMessageDialog(
                    this,
                    "Equipo agregado correctamente.",
                    "SIGELAB",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se puede agregar el equipo",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar SQL Server:\n"
                            + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ocurrió un error inesperado:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Confirmar y registrar préstamo
    private void confirmarPrestamo() {

        try {

            String solicitante =
                    txtSolicitante.getText().trim();

            if (solicitante.isEmpty()) {

                throw new IllegalArgumentException(
                        "Ingrese el nombre del solicitante."
                );
            }

            if (equiposSeleccionados.isEmpty()) {

                throw new IllegalArgumentException(
                        "Debe agregar al menos un equipo."
                );
            }

            LocalDate fecha = obtenerFechaDevolucion();

            LocalDateTime fechaDevolucion =
                    fecha.atTime(23, 59, 59);

            Prestamo prestamo = new Prestamo(
                    solicitante,
                    fechaDevolucion,
                    usuarioLogueado.getIdUsuario(),
                    txtObservaciones.getText().trim()
            );

            for (Equipo equipo : equiposSeleccionados) {

                prestamo.agregarDetalle(
                        new DetallePrestamo(equipo.getId())
                );
            }

            controller.validarPrestamo(prestamo);

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea confirmar este préstamo con "
                            + equiposSeleccionados.size()
                            + " equipo(s)?",
                    "Confirmar préstamo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            int idPrestamo =
                    controller.registrarPrestamo(prestamo);

            JOptionPane.showMessageDialog(
                    this,
                    "¡Préstamo registrado correctamente!\n"
                            + "Número de préstamo: "
                            + idPrestamo,
                    "SIGELAB",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Datos incorrectos",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar el préstamo:\n"
                            + ex.getMessage(),
                    "Error de SQL Server",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Quitar equipo seleccionado
    private void quitarEquipo() {

        int filaVista = tablaEquipos.getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un equipo de la tabla.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int filaModelo =
                tablaEquipos.convertRowIndexToModel(filaVista);

        equiposSeleccionados.remove(filaModelo);
        modeloTabla.removeRow(filaModelo);
    }

    // Limpiar formulario
    private void limpiarFormulario() {

        txtSolicitante.setText("");
        txtFechaDevolucion.setText("");
        txtCodigoEquipo.setText("");
        txtObservaciones.setText("");

        modeloTabla.setRowCount(0);
        equiposSeleccionados.clear();

        txtSolicitante.requestFocusInWindow();
    }

    // Validar fecha de devolución
    private LocalDate obtenerFechaDevolucion() {

        try {

            return LocalDate.parse(
                    txtFechaDevolucion.getText().trim()
            );

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "La fecha debe tener el formato AAAA-MM-DD."
            );
        }
    }
}
