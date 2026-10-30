package com.sigelab.view;

import com.sigelab.controller.EquipoQRController;
import com.sigelab.model.Usuario;
import com.sigelab.repository.EquipoQRRepository.DatosEquipoQR;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ConsultaEquipoQRForm extends JFrame {

    private final EquipoQRController controller =
            new EquipoQRController();

    private final Usuario usuarioLogueado;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtTipo;
    private JTextField txtEstado;

    private JLabel lblMensaje;

    private JButton btnEscanear;
    private JButton btnEnviarPrestamo;
    private JButton btnLimpiar;
    private JButton btnVolver;

    private DatosEquipoQR equipoConsultado;

    public ConsultaEquipoQRForm() {
        this(null);
    }

    public ConsultaEquipoQRForm(Usuario usuario) {
        this.usuarioLogueado = usuario;
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Consulta de Equipos por QR");
        setSize(720, 560);
        setMinimumSize(new Dimension(680, 540));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(new Color(245, 247, 250));
        principal.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel titulo = new JLabel("Consulta de equipos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(new Color(35, 49, 66));

        JLabel subtitulo = new JLabel(
                "Escanea un código QR para consultar un equipo registrado."
        );
        subtitulo.setForeground(new Color(105, 115, 125));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        principal.add(encabezado, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(new EmptyBorder(25, 25, 25, 25));

        txtCodigo = crearCampo();
        txtNombre = crearCampo();
        txtTipo = crearCampo();
        txtEstado = crearCampo();

        txtCodigo.setEditable(false);
        txtNombre.setEditable(false);
        txtTipo.setEditable(false);
        txtEstado.setEditable(false);

        agregarFila(formulario, "Código de inventario:", txtCodigo, 0);
        agregarFila(formulario, "Nombre del equipo:", txtNombre, 1);
        agregarFila(formulario, "Tipo de equipo:", txtTipo, 2);
        agregarFila(formulario, "Estado:", txtEstado, 3);

        principal.add(formulario, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(10, 15));
        inferior.setOpaque(false);

        lblMensaje = new JLabel(
                "Presiona el botón para iniciar el escáner.",
                SwingConstants.CENTER
        );
        lblMensaje.setForeground(new Color(100, 110, 120));

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 0)
        );
        botones.setOpaque(false);

        btnEscanear = crearBoton(
                "Escanear QR",
                new Color(38, 91, 160)
        );

        btnEnviarPrestamo = crearBoton(
                "Enviar a préstamo",
                new Color(39, 174, 96)
        );

        btnLimpiar = crearBoton(
                "Limpiar",
                new Color(100, 110, 120)
        );

        btnEnviarPrestamo.setEnabled(false);

        botones.add(btnEscanear);
        botones.add(btnEnviarPrestamo);
        botones.add(btnLimpiar);

        btnVolver = crearBoton(
                "← Volver al menú principal",
                new Color(30, 60, 114)
        );

        JPanel panelVolver = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        panelVolver.setOpaque(false);
        panelVolver.add(btnVolver);

        JPanel panelAcciones = new JPanel(new BorderLayout(0, 15));
        panelAcciones.setOpaque(false);
        panelAcciones.add(botones, BorderLayout.NORTH);
        panelAcciones.add(panelVolver, BorderLayout.SOUTH);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(panelAcciones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);

        btnEscanear.addActionListener(e -> abrirEscaner());

        btnEnviarPrestamo.addActionListener(
                e -> enviarAPrestamo()
        );

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnVolver.addActionListener(e -> dispose());
    }

    private JTextField crearCampo() {

        JTextField campo = new JTextField(22);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setBackground(new Color(248, 249, 251));

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 230)
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        return campo;
    }

    private JButton crearBoton(String texto, Color color) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);

        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

    private void agregarFila(
            JPanel panel,
            String etiqueta,
            JTextField campo,
            int fila
    ) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(campo, gbc);
    }

    private void abrirEscaner() {

        ScannerQRDialog scanner = new ScannerQRDialog(
                this,
                this::consultarEquipo
        );

        scanner.setLocationRelativeTo(this);
        scanner.setVisible(true);
    }

    private void consultarEquipo(String contenidoQR) {

        limpiarCampos();

        btnEscanear.setEnabled(false);
        lblMensaje.setText("Consultando equipo en SQL Server...");

        SwingWorker<DatosEquipoQR, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected DatosEquipoQR doInBackground()
                            throws SQLException {

                        return controller.buscarEquipoPorQR(
                                contenidoQR
                        );
                    }

                    @Override
                    protected void done() {

                        btnEscanear.setEnabled(true);

                        try {

                            DatosEquipoQR equipo = get();

                            if (equipo == null) {

                                lblMensaje.setText(
                                        "Equipo no encontrado."
                                );

                                lblMensaje.setForeground(
                                        new Color(180, 70, 50)
                                );

                                JOptionPane.showMessageDialog(
                                        ConsultaEquipoQRForm.this,
                                        "No se encontró un equipo activo "
                                                + "con ese código QR.",
                                        "Equipo no encontrado",
                                        JOptionPane.WARNING_MESSAGE
                                );

                                return;
                            }

                            equipoConsultado = equipo;

                            txtCodigo.setText(
                                    equipo.getCodigoInventario()
                            );

                            txtNombre.setText(equipo.getNombre());
                            txtTipo.setText(equipo.getTipo());
                            txtEstado.setText(equipo.getEstado());

                            boolean disponible =
                                    equipo.getEstado() != null
                                            && "DISPONIBLE".equalsIgnoreCase(
                                            equipo.getEstado().trim()
                                    );

                            boolean usuarioValido =
                                    usuarioLogueado != null
                                            && usuarioLogueado.getIdUsuario() > 0
                                            && usuarioLogueado.isActivo();

                            btnEnviarPrestamo.setEnabled(
                                    disponible && usuarioValido
                            );

                            if (disponible) {

                                if (usuarioValido) {

                                    lblMensaje.setText(
                                            "Equipo disponible. "
                                                    + "Puedes enviarlo a préstamo."
                                    );

                                } else {

                                    lblMensaje.setText(
                                            "Equipo disponible. "
                                                    + "Abre esta ventana desde "
                                                    + "una sesión autenticada "
                                                    + "para prestarlo."
                                    );
                                }

                                lblMensaje.setForeground(
                                        new Color(34, 139, 85)
                                );

                            } else {

                                lblMensaje.setText(
                                        "Equipo no disponible para préstamo."
                                );

                                lblMensaje.setForeground(
                                        new Color(180, 70, 50)
                                );
                            }

                        } catch (InterruptedException ex) {

                            Thread.currentThread().interrupt();

                            lblMensaje.setText(
                                    "Consulta interrumpida."
                            );

                        } catch (ExecutionException ex) {

                            Throwable causa = ex.getCause() != null
                                    ? ex.getCause()
                                    : ex;

                            lblMensaje.setText(
                                    "No se pudo consultar el equipo."
                            );

                            lblMensaje.setForeground(
                                    new Color(180, 70, 50)
                            );

                            JOptionPane.showMessageDialog(
                                    ConsultaEquipoQRForm.this,
                                    "Error al consultar el equipo:\n"
                                            + causa.getMessage(),
                                    "Error de consulta",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        trabajador.execute();
    }

    private void enviarAPrestamo() {

        if (equipoConsultado == null) {
            return;
        }

        if (usuarioLogueado == null
                || usuarioLogueado.getIdUsuario() <= 0
                || !usuarioLogueado.isActivo()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes iniciar sesión para registrar préstamos.",
                    "Sesión requerida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!"DISPONIBLE".equalsIgnoreCase(
                equipoConsultado.getEstado().trim()
        )) {

            JOptionPane.showMessageDialog(
                    this,
                    "El equipo no está disponible.",
                    "Equipo no disponible",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String codigo =
                equipoConsultado.getCodigoInventario();

        try {

            PrestamoView ventanaPrestamo =
                    new PrestamoView(usuarioLogueado);

            ventanaPrestamo.setVisible(true);

            ventanaPrestamo.agregarEquipoDesdeQR(codigo);

            // Cerrar consulta después de abrir préstamos
            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir la ventana de préstamos:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {

        equipoConsultado = null;

        txtCodigo.setText("");
        txtNombre.setText("");
        txtTipo.setText("");
        txtEstado.setText("");

        if (btnEnviarPrestamo != null) {
            btnEnviarPrestamo.setEnabled(false);
        }

        lblMensaje.setText(
                "Presiona el botón para iniciar el escáner."
        );

        lblMensaje.setForeground(
                new Color(100, 110, 120)
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new ConsultaEquipoQRForm().setVisible(true)
        );
    }
}
