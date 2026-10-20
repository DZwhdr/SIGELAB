
package org.example.view;

import org.example.controller.EquipoQRController;
import org.example.repository.EquipoQRRepository.DatosEquipoQR;

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

    public ConsultaEquipoQRForm() {
        controller = new EquipoQRController();

        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Consulta de Equipos por QR");
        setSize(560, 470);
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
                "Escanea el código QR para consultar un equipo registrado."
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

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);

        btnEscanear = crearBoton(
                "Escanear QR",
                new Color(38, 91, 160)
        );

        btnBuscar = crearBoton(
                "Limpiar",
                new Color(100, 110, 120)
        );

        btnEscanear.addActionListener(e -> abrirEscaner());
        btnBuscar.addActionListener(e -> limpiarCampos());

        botones.add(btnEscanear);
        botones.add(btnBuscar);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);
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
                            txtNombre.setText(equipo.getNombre());
                            txtTipo.setText(equipo.getTipo());
                            txtEstado.setText(equipo.getEstado());

                            lblMensaje.setText(
                                    "Equipo encontrado correctamente."
                            );

                        } catch (Exception e) {
                            lblMensaje.setText(
                                    "No se pudo consultar el equipo."
                            );

                            Throwable causa = e.getCause() != null
                                    ? e.getCause() : e;

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

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtTipo.setText("");
        txtEstado.setText("");

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
