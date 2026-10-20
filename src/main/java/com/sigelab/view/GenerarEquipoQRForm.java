
package org.example.view;

import org.example.controller.EquipoQRController;
import org.example.repository.EquipoQRRepository;
import org.example.repository.EquipoQRRepository.DatosEquipoQR;
import org.example.util.QRUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class GenerarEquipoQRForm extends JFrame {

    private final EquipoQRRepository repository;
    private final EquipoQRController controller;

    private JTextField txtCodigo;
    private JLabel lblNombre;
    private JLabel lblTipo;
    private JLabel lblEstado;
    private JLabel lblQR;
    private JLabel lblMensaje;

    private JButton btnBuscar;
    private JButton btnGenerar;
    private JButton btnGuardar;

    private BufferedImage imagenQR;
    private DatosEquipoQR equipoActual;

    public GenerarEquipoQRForm() {
        repository = new EquipoQRRepository();
        controller = new EquipoQRController();

        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Generador de Códigos QR");
        setSize(650, 690);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void crearComponentes() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBackground(new Color(245, 247, 250));
        principal.setBorder(new EmptyBorder(22, 30, 22, 30));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Generación de códigos QR");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(new Color(35, 49, 66));

        JLabel subtitulo = new JLabel(
                "Genera un código QR para un equipo registrado."
        );
        subtitulo.setForeground(new Color(105, 115, 125));

        encabezado.add(titulo);
        encabezado.add(subtitulo);

        principal.add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel busqueda = new JPanel(new BorderLayout(10, 8));
        busqueda.setOpaque(false);
        busqueda.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 75)
        );

        JLabel lblBuscar = new JLabel("Código de inventario");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));

        txtCodigo = new JTextField();
        txtCodigo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        btnBuscar = crearBoton(
                "Buscar equipo",
                new Color(38, 91, 160)
        );

        JPanel filaBusqueda = new JPanel(new BorderLayout(10, 0));
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(txtCodigo, BorderLayout.CENTER);
        filaBusqueda.add(btnBuscar, BorderLayout.EAST);

        busqueda.add(lblBuscar, BorderLayout.NORTH);
        busqueda.add(filaBusqueda, BorderLayout.CENTER);

        contenido.add(busqueda);
        contenido.add(Box.createVerticalStrut(18));

        JPanel datos = new JPanel(new GridLayout(3, 1, 0, 10));
        datos.setOpaque(false);
        datos.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 105)
        );

        lblNombre = crearEtiquetaDato("Equipo: —");
        lblTipo = crearEtiquetaDato("Tipo: —");
        lblEstado = crearEtiquetaDato("Estado: —");

        datos.add(lblNombre);
        datos.add(lblTipo);
        datos.add(lblEstado);

        contenido.add(datos);
        contenido.add(Box.createVerticalStrut(18));

        JPanel vistaQR = new JPanel(new BorderLayout());
        vistaQR.setOpaque(false);
        vistaQR.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 280)
        );

        lblQR = new JLabel(
                "Aquí aparecerá el código QR",
                SwingConstants.CENTER
        );
        lblQR.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblQR.setForeground(new Color(120, 130, 140));
        lblQR.setPreferredSize(new Dimension(260, 260));
        lblQR.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 230)
                )
        );

        vistaQR.add(lblQR, BorderLayout.CENTER);
        contenido.add(vistaQR);

        principal.add(contenido, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(10, 12));
        inferior.setOpaque(false);

        lblMensaje = new JLabel(
                "Busca un equipo para comenzar.",
                SwingConstants.CENTER
        );
        lblMensaje.setForeground(new Color(95, 105, 115));

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 0)
        );
        botones.setOpaque(false);

        btnGenerar = crearBoton(
                "Generar QR",
                new Color(38, 91, 160)
        );

        btnGuardar = crearBoton(
                "Guardar PNG",
                new Color(68, 125, 90)
        );

        btnGenerar.setEnabled(false);
        btnGuardar.setEnabled(false);

        botones.add(btnGenerar);
        botones.add(btnGuardar);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);

        btnBuscar.addActionListener(e -> buscarEquipo());
        btnGenerar.addActionListener(e -> generarQR());
        btnGuardar.addActionListener(e -> guardarQR());

        txtCodigo.addActionListener(e -> buscarEquipo());
    }

    private JLabel crearEtiquetaDato(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        etiqueta.setForeground(new Color(45, 55, 65));
        return etiqueta;
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

    private void limpiarResultado() {
        equipoActual = null;
        imagenQR = null;

        lblNombre.setText("Equipo: —");
        lblTipo.setText("Tipo: —");
        lblEstado.setText("Estado: —");

        lblQR.setIcon(null);
        lblQR.setText("Aquí aparecerá el código QR");

        btnGenerar.setEnabled(false);
        btnGuardar.setEnabled(false);
    }

    private void buscarEquipo() {
        String codigo = txtCodigo.getText().trim();

        limpiarResultado();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa un código de inventario.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        btnBuscar.setEnabled(false);
        lblMensaje.setText("Buscando equipo en SQL Server...");

        SwingWorker<DatosEquipoQR, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected DatosEquipoQR doInBackground()
                            throws Exception {
                        return repository.buscarPorCodigo(codigo);
                    }

                    @Override
                    protected void done() {
                        btnBuscar.setEnabled(true);

                        try {
                            DatosEquipoQR equipo = get();

                            if (equipo == null) {
                                lblMensaje.setText(
                                        "No se encontró el equipo."
                                );

                                JOptionPane.showMessageDialog(
                                        GenerarEquipoQRForm.this,
                                        "No existe un equipo activo "
                                                + "con ese código de inventario.",
                                        "Equipo no encontrado",
                                        JOptionPane.WARNING_MESSAGE
                                );
                                return;
                            }

                            equipoActual = equipo;

                            lblNombre.setText(
                                    "Equipo: " + equipo.getNombre()
                            );

                            lblTipo.setText(
                                    "Tipo: " + equipo.getTipo()
                            );

                            lblEstado.setText(
                                    "Estado: " + equipo.getEstado()
                            );

                            btnGenerar.setEnabled(true);

                            lblMensaje.setText(
                                    "Equipo encontrado. Puedes generar su QR."
                            );

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            lblMensaje.setText("Consulta interrumpida.");

                        } catch (ExecutionException e) {
                            Throwable causa = e.getCause() != null
                                    ? e.getCause() : e;

                            lblMensaje.setText(
                                    "Error al consultar el equipo."
                            );

                            JOptionPane.showMessageDialog(
                                    GenerarEquipoQRForm.this,
                                    causa.getMessage(),
                                    "Error de conexión",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        trabajador.execute();
    }

    private void generarQR() {
        if (equipoActual == null) {
            return;
        }

        try {
            String contenido = controller.generarContenidoQR(
                    equipoActual.getCodigoInventario()
            );

            imagenQR = QRUtils.generarQR(
                    contenido,
                    250,
                    250
            );

            lblQR.setIcon(new ImageIcon(imagenQR));
            lblQR.setText("");

            btnGuardar.setEnabled(true);

            lblMensaje.setText(
                    "Código QR generado correctamente."
            );

        } catch (Exception e) {
            lblMensaje.setText("No se pudo generar el QR.");

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al generar QR",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void guardarQR() {
        if (imagenQR == null || equipoActual == null) {
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar código QR");

        selector.setSelectedFile(
                new File(
                        "QR_" + equipoActual.getCodigoInventario() + ".png"
                )
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".png")) {
            archivo = new File(
                    archivo.getParentFile(),
                    archivo.getName() + ".png"
            );
        }

        if (archivo.exists()) {
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            boolean guardado = ImageIO.write(
                    imagenQR,
                    "PNG",
                    archivo
            );

            if (!guardado) {
                throw new IllegalStateException(
                        "No se encontró un codificador PNG."
                );
            }

            lblMensaje.setText(
                    "Código QR guardado correctamente."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "QR guardado en:\n"
                            + archivo.getAbsolutePath(),
                    "Archivo guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al guardar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new GenerarEquipoQRForm().setVisible(true)
        );
    }
}
