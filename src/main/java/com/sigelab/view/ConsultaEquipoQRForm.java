
package com.sigelab.view;

import com.sigelab.controller.EquipoQRController;
import com.sigelab.model.Usuario;
import com.sigelab.repository.EquipoQRRepository.DatosEquipoQR;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ConsultaEquipoQRForm extends JFrame {

    private final EquipoQRController controller;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtTipo;
    private JTextField txtEstado;

    private JLabel lblMensaje;

    private JButton btnEscanear;
    private JButton btnBuscar;
    private JButton btnEnviarPrestamo;
    private JButton btnVolver;

    private Usuario usuarioLogueado;

    public ConsultaEquipoQRForm() {
        controller = new EquipoQRController();

        configurarVentana();
        crearComponentes();
    }

    // Constructor que recibe al usuario autenticado
    public ConsultaEquipoQRForm(Usuario usuario) {
        this();
        this.usuarioLogueado = usuario;
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Consulta de Equipos por QR");
        setSize(700, 540);
        setMinimumSize(new Dimension(700, 540));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(new Color(245, 247, 250));
        principal.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Encabezado
        JLabel titulo = new JLabel("Consulta de equipos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(new Color(35, 49, 66));

        JLabel subtitulo = new JLabel(
                "Escanea el código QR para consultar un equipo registrado."
        );
        subtitulo.setForeground(new Color(105, 115, 125));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        principal.add(encabezado, BorderLayout.NORTH);

        // Formulario de consulta
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

        // Panel inferior
        JPanel inferior = new JPanel(new BorderLayout(10, 15));
        inferior.setOpaque(false);

        lblMensaje = new JLabel(
                "Presiona el botón para iniciar el escáner.",
                SwingConstants.CENTER
        );
        lblMensaje.setForeground(new Color(100, 110, 120));

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 0)
        );
        botones.setOpaque(false);

        btnEscanear = crearBoton(
                "Escanear QR",
                new Color(38, 91, 160)
        );

        btnBuscar = crearBoton(
                "Limpiar",
                new Color(100, 110, 120)
        );

        btnEnviarPrestamo = crearBoton(
                "Enviar a préstamo",
                new Color(39, 174, 96)
        );

        // Solo se activa cuando el equipo está disponible
        // y existe una sesión autenticada.
        btnEnviarPrestamo.setEnabled(false);

        botones.add(btnEscanear);
        botones.add(btnBuscar);
        botones.add(btnEnviarPrestamo);

        btnVolver = crearBoton(
                "← Volver al menú principal",
                new Color(30, 60, 114)
        );

        JPanel panelVolver = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        panelVolver.setOpaque(false);
        panelVolver.add(btnVolver);

        JPanel panelAcciones = new JPanel(
                new BorderLayout(0, 15)
        );
        panelAcciones.setOpaque(false);

        panelAcciones.add(botones, BorderLayout.NORTH);
        panelAcciones.add(panelVolver, BorderLayout.SOUTH);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(panelAcciones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);

        // Eventos
        btnEscanear.addActionListener(e -> abrirEscaner());
        btnBuscar.addActionListener(e -> limpiarCampos());
        btnEnviarPrestamo.addActionListener(e -> enviarAPrestamo());
        btnVolver.addActionListener(e -> dispose());
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField(22);

        campo.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

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

        boton.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

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

        label.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

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

                                JOptionPane.showMessageDialog(
                                        ConsultaEquipoQRForm.this,
                                        "No se encontró un equipo activo "
                                                + "con ese código de inventario.",
                                        "Equipo no encontrado",
                                        JOptionPane.WARNING_MESSAGE
                                );

                                return;
                            }

                            txtCodigo.setText(
                                    equipo.getCodigoInventario()
                            );

                            txtNombre.setText(
                                    equipo.getNombre()
                            );

                            txtTipo.setText(
                                    equipo.getTipo()
                            );

                            txtEstado.setText(
                                    equipo.getEstado()
                            );

                            lblMensaje.setText(
                                    "Equipo encontrado correctamente."
                            );

                            // Activar el envío únicamente si el equipo
                            // está disponible y el usuario inició sesión.
                            boolean disponible =
                                    "DISPONIBLE".equalsIgnoreCase(
                                            txtEstado.getText().trim()
                                    );

                            btnEnviarPrestamo.setEnabled(
                                    disponible && usuarioLogueado != null
                            );

                            if (!disponible) {
                                lblMensaje.setText(
                                        "El equipo no está disponible para préstamo."
                                );
                            }

                        } catch (Exception e) {

                            lblMensaje.setText(
                                    "No se pudo consultar el equipo."
                            );

                            btnEnviarPrestamo.setEnabled(false);

                            Throwable causa = e.getCause() != null
                                    ? e.getCause()
                                    : e;

                            JOptionPane.showMessageDialog(
                                    ConsultaEquipoQRForm.this,
                                    causa.getMessage(),
                                    "Error de consulta",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        trabajador.execute();
    }

    // Abre la ventana de préstamos y agrega el equipo consultado.
    private void enviarAPrestamo() {

        String codigo = txtCodigo.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes escanear un equipo.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
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
                txtEstado.getText().trim()
        )) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este equipo no está disponible para préstamo.",
                    "Equipo no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            PrestamoView ventanaPrestamo =
                    new PrestamoView(usuarioLogueado);

            ventanaPrestamo.agregarEquipoDesdeQR(codigo);
            ventanaPrestamo.setLocationRelativeTo(this);
            ventanaPrestamo.setVisible(true);

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
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new ConsultaEquipoQRForm().setVisible(true)
        );
    }
}
