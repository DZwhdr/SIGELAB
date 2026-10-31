
package com.sigelab.view;

import com.sigelab.controller.PrestamoController;
import com.sigelab.model.DetallePrestamo;
import com.sigelab.model.Equipo;
import com.sigelab.model.Prestamo;
import com.sigelab.model.Usuario;
import com.sigelab.repository.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrestamoView extends JFrame {

    private final PrestamoController controller = new PrestamoController();
    private final Usuario usuarioLogueado;

    private final JTextField txtSolicitante = new JTextField();
    private final JTextField txtFechaDevolucion = new JTextField();
    private final JTextField txtCodigoEquipo = new JTextField();
    private final JTextArea txtObservaciones = new JTextArea(3, 20);

    private final JButton btnCalendario = new JButton("Elegir fecha");

    private final JTextArea txtDetalleEquipo = new JTextArea();
    private final JTextArea txtResumenPrestamo = new JTextArea();

    private final List<Equipo> equiposSeleccionados = new ArrayList<>();

    // CONTADORES DE DISPONIBILIDAD
    private final JLabel lblTotal = new JLabel("Total: ...");
    private final JLabel lblDisponibles = new JLabel("Disponibles: ...");
    private final JLabel lblPrestados = new JLabel("Prestados: ...");
    private final JLabel lblMantenimiento = new JLabel("Mantenimiento: ...");

    private final JButton btnActualizarDisponibilidad =
            new JButton("Actualizar disponibilidad");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{
                    "Código", "Nombre", "Tipo", "Marca",
                    "Modelo", "Serie", "Laboratorio", "Estado"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tablaEquipos = new JTable(modeloTabla);

    private final JButton btnAgregar = new JButton("Agregar equipo");
    private final JButton btnEscanear = new JButton("Escanear QR");
    private final JButton btnQuitar = new JButton("Quitar equipo");
    private final JButton btnConfirmar = new JButton("Confirmar préstamo");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnVolver = new JButton("Volver");

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
        setSize(1200, 900);
        setMinimumSize(new Dimension(950, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearInterfaz();
        configurarEventos();
        actualizarResumen();
        actualizarDisponibilidad();
    }

    private void crearInterfaz() {

        JPanel principal = new JPanel(new BorderLayout(15, 15));

        principal.setBorder(
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        );

        principal.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel(
                "GESTIÓN DE PRÉSTAMOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 60, 114));

        JLabel lblUsuario = new JLabel(
                "Registrado por: " + nombreUsuario(),
                SwingConstants.RIGHT
        );

        lblUsuario.setForeground(new Color(90, 100, 110));

        JPanel encabezado = new JPanel(new BorderLayout(5, 8));
        encabezado.setOpaque(false);

        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(lblUsuario, BorderLayout.SOUTH);

        // PANEL DE DISPONIBILIDAD

        JPanel panelDisponibilidad = new JPanel(
                new GridLayout(1, 4, 10, 0)
        );

        panelDisponibilidad.setOpaque(false);

        JLabel[] indicadores = {
                lblTotal,
                lblDisponibles,
                lblPrestados,
                lblMantenimiento
        };

        Color[] colores = {
                new Color(30, 60, 114),
                new Color(39, 174, 96),
                new Color(211, 84, 0),
                new Color(100, 110, 120)
        };

        for (int i = 0; i < indicadores.length; i++) {

            JLabel indicador = indicadores[i];

            indicador.setOpaque(true);
            indicador.setBackground(Color.WHITE);
            indicador.setForeground(colores[i]);

            indicador.setHorizontalAlignment(SwingConstants.CENTER);

            indicador.setFont(
                    new Font("Arial", Font.BOLD, 15)
            );

            indicador.setBorder(
                    BorderFactory.createEmptyBorder(15, 8, 15, 8)
            );

            panelDisponibilidad.add(indicador);
        }

        JPanel panelActualizar = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        panelActualizar.setOpaque(false);
        panelActualizar.add(btnActualizarDisponibilidad);

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 12));
        panelSuperior.setOpaque(false);

        panelSuperior.add(encabezado, BorderLayout.NORTH);
        panelSuperior.add(panelDisponibilidad, BorderLayout.CENTER);
        panelSuperior.add(panelActualizar, BorderLayout.SOUTH);

        principal.add(panelSuperior, BorderLayout.NORTH);

        // DATOS DEL PRÉSTAMO

        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBackground(Color.WHITE);

        panelDatos.setBorder(
                BorderFactory.createTitledBorder("Datos del préstamo")
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        panelDatos.add(new JLabel("Solicitante:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panelDatos.add(txtSolicitante, gbc);

        // FECHA DE DEVOLUCIÓN

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

        JPanel panelFecha = new JPanel(new BorderLayout(8, 0));
        panelFecha.setOpaque(false);

        panelFecha.add(
                txtFechaDevolucion,
                BorderLayout.CENTER
        );

        btnCalendario.setBackground(new Color(41, 128, 185));
        btnCalendario.setForeground(Color.WHITE);
        btnCalendario.setFocusPainted(false);

        btnCalendario.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        btnCalendario.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        panelFecha.add(btnCalendario, BorderLayout.EAST);
        panelDatos.add(panelFecha, gbc);

        // OBSERVACIONES

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

        // EQUIPOS DEL PRÉSTAMO

        JPanel panelEquipos = new JPanel(
                new BorderLayout(10, 10)
        );

        panelEquipos.setBackground(Color.WHITE);

        panelEquipos.setBorder(
                BorderFactory.createTitledBorder(
                        "Equipos incluidos en el préstamo"
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

        tablaEquipos.getTableHeader()
                .setReorderingAllowed(false);

        tablaEquipos.setAutoCreateRowSorter(true);

        tablaEquipos.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        JScrollPane scrollTabla = new JScrollPane(tablaEquipos);

        scrollTabla.setPreferredSize(
                new Dimension(900, 210)
        );

        panelEquipos.add(
                scrollTabla,
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

        // DETALLES Y RESUMEN

        JPanel panelDetalle = new JPanel(
                new GridLayout(1, 2, 12, 0)
        );

        panelDetalle.setOpaque(false);

        txtDetalleEquipo.setEditable(false);

        txtDetalleEquipo.setFont(
                new Font("Monospaced", Font.PLAIN, 12)
        );

        txtDetalleEquipo.setBackground(
                new Color(250, 251, 253)
        );

        txtDetalleEquipo.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        txtResumenPrestamo.setEditable(false);

        txtResumenPrestamo.setFont(
                new Font("Monospaced", Font.PLAIN, 12)
        );

        txtResumenPrestamo.setBackground(
                new Color(250, 251, 253)
        );

        txtResumenPrestamo.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        JPanel panelInfoEquipo = new JPanel(
                new BorderLayout()
        );

        panelInfoEquipo.setBorder(
                BorderFactory.createTitledBorder(
                        "Detalles del equipo seleccionado"
                )
        );

        panelInfoEquipo.add(
                new JScrollPane(txtDetalleEquipo),
                BorderLayout.CENTER
        );

        JPanel panelInfoPrestamo = new JPanel(
                new BorderLayout()
        );

        panelInfoPrestamo.setBorder(
                BorderFactory.createTitledBorder(
                        "Resumen del préstamo actual"
                )
        );

        panelInfoPrestamo.add(
                new JScrollPane(txtResumenPrestamo),
                BorderLayout.CENTER
        );

        panelDetalle.add(panelInfoEquipo);
        panelDetalle.add(panelInfoPrestamo);

        panelDetalle.setPreferredSize(
                new Dimension(900, 190)
        );

        JPanel centro = new JPanel(
                new BorderLayout(10, 12)
        );

        centro.setOpaque(false);

        centro.add(panelDatos, BorderLayout.NORTH);
        centro.add(panelEquipos, BorderLayout.CENTER);
        centro.add(panelDetalle, BorderLayout.SOUTH);

        principal.add(centro, BorderLayout.CENTER);

        // BOTONES INFERIORES

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

        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );

        acciones.setOpaque(false);

        acciones.add(btnLimpiar);
        acciones.add(btnVolver);
        acciones.add(btnConfirmar);

        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);

        txtDetalleEquipo.setText(
                "Selecciona un equipo de la tabla\n" +
                        "para consultar sus detalles."
        );
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
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
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

        btnCalendario.addActionListener(
                e -> mostrarCalendario()
        );

        btnActualizarDisponibilidad.addActionListener(
                e -> actualizarDisponibilidad()
        );

        tablaEquipos.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        mostrarDetalleEquipo();
                    }
                });

        txtSolicitante.getDocument().addDocumentListener(
                crearListenerResumen()
        );

        txtFechaDevolucion.getDocument().addDocumentListener(
                crearListenerResumen()
        );

        txtObservaciones.getDocument().addDocumentListener(
                crearListenerResumen()
        );
    }

    private javax.swing.event.DocumentListener crearListenerResumen() {

        return new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(
                    javax.swing.event.DocumentEvent e
            ) {
                actualizarResumen();
            }

            @Override
            public void removeUpdate(
                    javax.swing.event.DocumentEvent e
            ) {
                actualizarResumen();
            }

            @Override
            public void changedUpdate(
                    javax.swing.event.DocumentEvent e
            ) {
                actualizarResumen();
            }
        };
    }

    private String nombreUsuario() {

        return usuarioLogueado.getNombre()
                + " "
                + usuarioLogueado.getApellido();
    }

    private String texto(String valor) {

        return valor == null || valor.isBlank()
                ? "No especificado"
                : valor;
    }

    private void actualizarResumen() {

        String fechaActual = LocalDate.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE
        );

        String resumen =
                "Solicitante: "
                        + texto(txtSolicitante.getText()) + "\n\n" +

                        "Registrado por: "
                        + nombreUsuario() + "\n\n" +

                        "Fecha actual: "
                        + fechaActual + "\n\n" +

                        "Devolución prevista: "
                        + texto(txtFechaDevolucion.getText()) + "\n\n" +

                        "Equipos agregados: "
                        + equiposSeleccionados.size() + "\n\n" +

                        "Observaciones: "
                        + texto(txtObservaciones.getText());

        txtResumenPrestamo.setText(resumen);
        txtResumenPrestamo.setCaretPosition(0);
    }

    private void mostrarDetalleEquipo() {

        int filaVista = tablaEquipos.getSelectedRow();

        if (filaVista == -1) {

            txtDetalleEquipo.setText(
                    "Selecciona un equipo de la tabla\n" +
                            "para consultar sus detalles."
            );

            return;
        }

        int filaModelo = tablaEquipos.convertRowIndexToModel(
                filaVista
        );

        if (filaModelo < 0
                || filaModelo >= equiposSeleccionados.size()) {
            return;
        }

        Equipo equipo = equiposSeleccionados.get(filaModelo);

        String tipo = equipo.getTipoEquipo() == null
                ? "No especificado"
                : texto(equipo.getTipoEquipo().getNombre());

        String laboratorio = equipo.getLaboratorio() == null
                ? "No especificado"
                : texto(equipo.getLaboratorio().getNombre());

        String estado = equipo.getEstado() == null
                ? "No especificado"
                : equipo.getEstado().name();

        txtDetalleEquipo.setText(
                "ID: " + equipo.getId() + "\n" +
                        "Código: " + texto(equipo.getCodigoInventario()) + "\n" +
                        "Nombre: " + texto(equipo.getNombre()) + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "Marca: " + texto(equipo.getMarca()) + "\n" +
                        "Modelo: " + texto(equipo.getModelo()) + "\n" +
                        "Serie: " + texto(equipo.getSerie()) + "\n" +
                        "Laboratorio: " + laboratorio + "\n" +
                        "Estado: " + estado + "\n" +
                        "Observaciones: " + texto(equipo.getObservaciones())
        );

        txtDetalleEquipo.setCaretPosition(0);
    }

    // CONSULTAR DISPONIBILIDAD EN AZURE

    private void actualizarDisponibilidad() {

        String sql = """
                SELECT
                    COUNT(*) AS total,

                    COALESCE(SUM(
                        CASE
                            WHEN estado = 'DISPONIBLE'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS disponibles,

                    COALESCE(SUM(
                        CASE
                            WHEN estado = 'PRESTADO'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS prestados,

                    COALESCE(SUM(
                        CASE
                            WHEN estado = 'MANTENIMIENTO'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS mantenimiento

                FROM Equipo
                """;

        try (
                Connection con = ConexionBD.obtenerConexion();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                lblTotal.setText(
                        "Total: " + rs.getInt("total")
                );

                lblDisponibles.setText(
                        "Disponibles: "
                                + rs.getInt("disponibles")
                );

                lblPrestados.setText(
                        "Prestados: "
                                + rs.getInt("prestados")
                );

                lblMantenimiento.setText(
                        "Mantenimiento: "
                                + rs.getInt("mantenimiento")
                );
            }

        } catch (SQLException ex) {

            lblTotal.setText("Total: Error");
            lblDisponibles.setText("Disponibles: —");
            lblPrestados.setText("Prestados: —");
            lblMantenimiento.setText("Mantenimiento: —");

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo consultar la disponibilidad:\n"
                            + ex.getMessage(),
                    "Error de SQL Server",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // INTEGRACIÓN CON QR

    public void agregarEquipoDesdeQR(
            String codigoInventario
    ) {

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

        agregarEquipo();

        txtSolicitante.requestFocusInWindow();
    }

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

    // AGREGAR EQUIPO AL PRÉSTAMO

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

            if (equipo == null) {

                throw new IllegalArgumentException(
                        "No se encontró un equipo disponible con ese código."
                );
            }

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

            String tipo = equipo.getTipoEquipo() == null
                    ? ""
                    : equipo.getTipoEquipo().getNombre();

            String laboratorio = equipo.getLaboratorio() == null
                    ? ""
                    : equipo.getLaboratorio().getNombre();

            modeloTabla.addRow(new Object[]{
                    equipo.getCodigoInventario(),
                    equipo.getNombre(),
                    tipo,
                    equipo.getMarca(),
                    equipo.getModelo(),
                    equipo.getSerie(),
                    laboratorio,
                    equipo.getEstado() == null
                            ? ""
                            : equipo.getEstado().name()
            });

            int ultimaFila = modeloTabla.getRowCount() - 1;

            int filaVista = tablaEquipos.convertRowIndexToView(
                    ultimaFila
            );

            if (filaVista >= 0) {

                tablaEquipos.setRowSelectionInterval(
                        filaVista,
                        filaVista
                );
            }

            txtCodigoEquipo.setText("");

            actualizarResumen();

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

    // CONFIRMAR PRÉSTAMO

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

            LocalDateTime fechaDevolucion = fecha.atTime(
                    23, 59, 59
            );

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

                    "Solicitante: " + solicitante + "\n" +
                            "Registrado por: " + nombreUsuario() + "\n" +
                            "Fecha de devolución: " + fecha + "\n" +
                            "Cantidad de equipos: "
                            + equiposSeleccionados.size() + "\n\n" +
                            "¿Desea confirmar este préstamo?",

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

                    "¡Préstamo registrado correctamente!\n" +
                            "Número de préstamo: " + idPrestamo + "\n" +
                            "Solicitante: " + solicitante + "\n" +
                            "Equipos: " + equiposSeleccionados.size() + "\n" +
                            "Devolución prevista: " + fecha,

                    "SIGELAB",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();

            // ACTUALIZAR CONTADORES DESPUÉS DEL PRÉSTAMO
            actualizarDisponibilidad();

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

    // QUITAR EQUIPO

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

        int filaModelo = tablaEquipos.convertRowIndexToModel(
                filaVista
        );

        equiposSeleccionados.remove(filaModelo);
        modeloTabla.removeRow(filaModelo);

        mostrarDetalleEquipo();
        actualizarResumen();
    }

    // LIMPIAR FORMULARIO

    private void limpiarFormulario() {

        txtSolicitante.setText("");
        txtFechaDevolucion.setText("");
        txtCodigoEquipo.setText("");
        txtObservaciones.setText("");

        modeloTabla.setRowCount(0);
        equiposSeleccionados.clear();

        txtDetalleEquipo.setText(
                "Selecciona un equipo de la tabla\n" +
                        "para consultar sus detalles."
        );

        actualizarResumen();

        txtSolicitante.requestFocusInWindow();
    }

    // VALIDAR FECHA

    private LocalDate obtenerFechaDevolucion() {

        try {

            LocalDate fecha = LocalDate.parse(
                    txtFechaDevolucion.getText().trim()
            );

            if (fecha.isBefore(LocalDate.now())) {

                throw new IllegalArgumentException(
                        "La fecha de devolución no puede ser anterior a hoy."
                );
            }

            return fecha;

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "La fecha debe tener el formato AAAA-MM-DD."
            );
        }
    }

    // CALENDARIO INTERACTIVO

    private void mostrarCalendario() {

        JDialog dialogo = new JDialog(
                this,
                "SIGELAB - Seleccionar fecha de devolución",
                true
        );

        dialogo.setSize(420, 420);
        dialogo.setMinimumSize(new Dimension(380, 380));
        dialogo.setLocationRelativeTo(this);

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        LocalDate fechaInicial = LocalDate.now();

        try {

            String fechaTexto =
                    txtFechaDevolucion.getText().trim();

            if (!fechaTexto.isEmpty()) {
                fechaInicial = LocalDate.parse(fechaTexto);
            }

        } catch (DateTimeParseException ex) {

            fechaInicial = LocalDate.now();
        }

        if (fechaInicial.isBefore(LocalDate.now())) {
            fechaInicial = LocalDate.now();
        }

        final YearMonth[] mesActual = {
                YearMonth.from(fechaInicial)
        };

        JPanel principal = new JPanel(
                new BorderLayout(10, 12)
        );

        principal.setBackground(Color.WHITE);

        principal.setBorder(
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );

        JPanel encabezado = new JPanel(
                new BorderLayout(10, 0)
        );

        encabezado.setOpaque(false);

        JButton btnAnterior = new JButton("◀");
        JButton btnSiguiente = new JButton("▶");

        JLabel lblMes = new JLabel(
                "",
                SwingConstants.CENTER
        );

        lblMes.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        lblMes.setForeground(
                new Color(30, 60, 114)
        );

        encabezado.add(
                btnAnterior,
                BorderLayout.WEST
        );

        encabezado.add(
                lblMes,
                BorderLayout.CENTER
        );

        encabezado.add(
                btnSiguiente,
                BorderLayout.EAST
        );

        principal.add(
                encabezado,
                BorderLayout.NORTH
        );

        JPanel panelDias = new JPanel(
                new GridLayout(0, 7, 5, 5)
        );

        panelDias.setOpaque(false);

        principal.add(
                panelDias,
                BorderLayout.CENTER
        );

        JLabel indicacion = new JLabel(
                "Selecciona la fecha prevista de devolución",
                SwingConstants.CENTER
        );

        indicacion.setForeground(
                new Color(100, 110, 120)
        );

        principal.add(
                indicacion,
                BorderLayout.SOUTH
        );

        Runnable actualizarCalendario = () -> {

            panelDias.removeAll();

            YearMonth mes = mesActual[0];

            DateTimeFormatter formatoMes =
                    DateTimeFormatter.ofPattern(
                            "MMMM yyyy",
                            new Locale("es", "ES")
                    );

            String nombreMes = mes.format(formatoMes);

            lblMes.setText(
                    nombreMes.substring(0, 1).toUpperCase()
                            + nombreMes.substring(1)
            );

            String[] nombresDias = {
                    "Lun", "Mar", "Mié", "Jue",
                    "Vie", "Sáb", "Dom"
            };

            for (String nombreDia : nombresDias) {

                JLabel etiqueta = new JLabel(
                        nombreDia,
                        SwingConstants.CENTER
                );

                etiqueta.setFont(
                        new Font("Arial", Font.BOLD, 12)
                );

                etiqueta.setForeground(
                        new Color(90, 100, 110)
                );

                panelDias.add(etiqueta);
            }

            LocalDate primerDia = mes.atDay(1);

            int espacios =
                    primerDia.getDayOfWeek().getValue() - 1;

            for (int i = 0; i < espacios; i++) {
                panelDias.add(new JLabel(""));
            }

            for (int dia = 1;
                 dia <= mes.lengthOfMonth();
                 dia++) {

                LocalDate fecha = mes.atDay(dia);

                JButton botonDia = new JButton(
                        String.valueOf(dia)
                );

                botonDia.setFocusPainted(false);

                botonDia.setFont(
                        new Font("Arial", Font.BOLD, 13)
                );

                botonDia.setCursor(
                        Cursor.getPredefinedCursor(
                                Cursor.HAND_CURSOR
                        )
                );

                botonDia.setBackground(
                        new Color(240, 244, 248)
                );

                botonDia.setForeground(
                        new Color(35, 55, 75)
                );

                if (fecha.equals(LocalDate.now())) {

                    botonDia.setBackground(
                            new Color(41, 128, 185)
                    );

                    botonDia.setForeground(Color.WHITE);
                }

                if (fecha.isBefore(LocalDate.now())) {

                    botonDia.setEnabled(false);

                } else {

                    botonDia.addActionListener(e -> {

                        txtFechaDevolucion.setText(
                                fecha.format(
                                        DateTimeFormatter.ISO_LOCAL_DATE
                                )
                        );

                        actualizarResumen();
                        dialogo.dispose();
                    });
                }

                panelDias.add(botonDia);
            }

            panelDias.revalidate();
            panelDias.repaint();
        };

        btnAnterior.addActionListener(e -> {

            YearMonth mesAnterior =
                    mesActual[0].minusMonths(1);

            if (!mesAnterior.isBefore(
                    YearMonth.from(LocalDate.now())
            )) {

                mesActual[0] = mesAnterior;
                actualizarCalendario.run();
            }
        });

        btnSiguiente.addActionListener(e -> {

            mesActual[0] = mesActual[0].plusMonths(1);
            actualizarCalendario.run();
        });

        actualizarCalendario.run();

        dialogo.setContentPane(principal);
        dialogo.setVisible(true);
    }
}
