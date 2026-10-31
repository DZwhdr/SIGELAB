package com.sigelab.view;

import com.sigelab.controller.EquipoQRController;
import com.sigelab.repository.EquipoQRRepository;
import com.sigelab.repository.EquipoQRRepository.DatosEquipoQR;
import com.sigelab.util.QRUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

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
    private JButton btnVerEquipos;
    private JButton btnGenerar;
    private JButton btnGuardar;
    private JButton btnVolver;

    private BufferedImage imagenQR;
    private DatosEquipoQR equipoActual;

    private boolean ocupado = false;
    private int versionConsulta = 0;

    private final Color AZUL = new Color(38, 91, 160);
    private final Color VERDE = new Color(68, 125, 90);
    private final Color GRIS = new Color(110, 120, 130);

    public GenerarEquipoQRForm() {
        repository = new EquipoQRRepository();
        controller = new EquipoQRController();

        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("SIGELAB - Generador de Códigos QR");
        setSize(720, 750);
        setMinimumSize(new Dimension(680, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));
        return boton;
    }

    private JLabel crearEtiquetaDato(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        etiqueta.setForeground(new Color(45, 55, 65));
        return etiqueta;
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(
                new BorderLayout(15, 15)
        );
        principal.setBackground(new Color(245, 247, 250));
        principal.setBorder(
                new EmptyBorder(22, 30, 22, 30)
        );

        // ENCABEZADO

        JPanel encabezado = new JPanel(
                new GridLayout(2, 1, 0, 5)
        );
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel(
                "Generación de códigos QR"
        );
        titulo.setFont(
                new Font("SansSerif", Font.BOLD, 24)
        );
        titulo.setForeground(new Color(35, 49, 66));

        JLabel subtitulo = new JLabel(
                "Busca o selecciona un equipo para generar su QR."
        );
        subtitulo.setForeground(new Color(105, 115, 125));

        encabezado.add(titulo);
        encabezado.add(subtitulo);

        principal.add(encabezado, BorderLayout.NORTH);

        // CONTENIDO

        JPanel contenido = new JPanel();
        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(
                new EmptyBorder(20, 22, 20, 22)
        );

        // BUSCADOR

        JPanel busqueda = new JPanel(
                new BorderLayout(10, 8)
        );
        busqueda.setOpaque(false);
        busqueda.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 85)
        );

        JLabel lblBuscar = new JLabel(
                "Código de inventario"
        );
        lblBuscar.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        txtCodigo = new JTextField();
        txtCodigo.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        btnBuscar = crearBoton("Buscar equipo", AZUL);
        btnVerEquipos = crearBoton("Ver equipos", VERDE);

        JPanel botonesBusqueda = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        botonesBusqueda.setOpaque(false);
        botonesBusqueda.add(btnBuscar);
        botonesBusqueda.add(btnVerEquipos);

        JPanel filaBusqueda = new JPanel(
                new BorderLayout(8, 0)
        );
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(txtCodigo, BorderLayout.CENTER);
        filaBusqueda.add(
                botonesBusqueda,
                BorderLayout.EAST
        );

        busqueda.add(lblBuscar, BorderLayout.NORTH);
        busqueda.add(filaBusqueda, BorderLayout.CENTER);

        contenido.add(busqueda);
        contenido.add(Box.createVerticalStrut(18));

        // DATOS DEL EQUIPO

        JPanel datos = new JPanel(
                new GridLayout(3, 1, 0, 10)
        );
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

        // VISTA PREVIA QR

        JPanel vistaQR = new JPanel(new BorderLayout());
        vistaQR.setOpaque(false);
        vistaQR.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 280)
        );

        lblQR = new JLabel(
                "Aquí aparecerá el código QR",
                SwingConstants.CENTER
        );
        lblQR.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
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

        // BOTONES INFERIORES

        JPanel inferior = new JPanel(
                new BorderLayout(10, 12)
        );
        inferior.setOpaque(false);

        lblMensaje = new JLabel(
                "Busca un equipo o utiliza 'Ver equipos'.",
                SwingConstants.CENTER
        );
        lblMensaje.setForeground(new Color(95, 105, 115));

        btnGenerar = crearBoton("Generar QR", AZUL);
        btnGuardar = crearBoton("Guardar PNG", VERDE);

        btnGenerar.setEnabled(false);
        btnGuardar.setEnabled(false);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 0)
        );
        botones.setOpaque(false);
        botones.add(btnGenerar);
        botones.add(btnGuardar);

        btnVolver = crearBoton(
                "← Volver al menú principal",
                new Color(30, 60, 114)
        );

        JPanel panelVolver = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        panelVolver.setOpaque(false);
        panelVolver.add(btnVolver);

        JPanel acciones = new JPanel(
                new BorderLayout(0, 12)
        );
        acciones.setOpaque(false);
        acciones.add(botones, BorderLayout.NORTH);
        acciones.add(panelVolver, BorderLayout.SOUTH);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(acciones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);
        setContentPane(principal);

        // EVENTOS

        btnBuscar.addActionListener(e -> buscarEquipo());
        btnVerEquipos.addActionListener(
                e -> mostrarSelectorEquipos()
        );
        btnGenerar.addActionListener(e -> generarQR());
        btnGuardar.addActionListener(e -> guardarQR());
        btnVolver.addActionListener(e -> dispose());
        txtCodigo.addActionListener(e -> buscarEquipo());
    }

    // CONTROLA LOS BOTONES DURANTE LAS CONSULTAS

    private void establecerOcupado(boolean valor) {
        ocupado = valor;

        btnBuscar.setEnabled(!valor);
        btnVerEquipos.setEnabled(!valor);
        btnGenerar.setEnabled(
                !valor && equipoActual != null
        );
        btnGuardar.setEnabled(
                !valor && imagenQR != null
        );
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

    private void mostrarImagenQR(BufferedImage imagen) {
        imagenQR = imagen;

        if (imagen == null) {
            lblQR.setIcon(null);
            lblQR.setText("Aquí aparecerá el código QR");
            btnGuardar.setEnabled(false);
            return;
        }

        lblQR.setIcon(new ImageIcon(imagen));
        lblQR.setText("");
        btnGuardar.setEnabled(!ocupado);
    }

    // BUSCAR EQUIPO Y RECUPERAR QR DE AZURE

    private void buscarEquipo() {

        if (ocupado) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa un código de inventario.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        limpiarResultado();
        establecerOcupado(true);

        int version = ++versionConsulta;

        lblMensaje.setText(
                "Consultando equipo en SQL Server..."
        );

        SwingWorker<DatosEquipoQR, Void> trabajador =
                new SwingWorker<>() {

                    private BufferedImage qrRecuperado;

                    @Override
                    protected DatosEquipoQR doInBackground()
                            throws Exception {

                        DatosEquipoQR equipo =
                                repository.buscarPorCodigo(codigo);

                        if (equipo != null) {
                            qrRecuperado =
                                    repository.obtenerImagenQR(
                                            equipo.getCodigoInventario()
                                    );
                        }

                        return equipo;
                    }

                    @Override
                    protected void done() {

                        if (version != versionConsulta) {
                            return;
                        }

                        try {
                            DatosEquipoQR equipo = get();

                            if (equipo == null) {
                                lblMensaje.setText(
                                        "No se encontró el equipo."
                                );

                                JOptionPane.showMessageDialog(
                                        GenerarEquipoQRForm.this,
                                        "No existe un equipo activo "
                                                + "con ese código.",
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

                            if (qrRecuperado != null) {
                                mostrarImagenQR(qrRecuperado);

                                lblMensaje.setText(
                                        "QR recuperado desde Azure correctamente."
                                );
                            } else {
                                lblMensaje.setText(
                                        "Equipo encontrado. "
                                                + "Puedes generar su QR."
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
                                    "Error al consultar el equipo o su QR."
                            );

                            JOptionPane.showMessageDialog(
                                    GenerarEquipoQRForm.this,
                                    causa.getMessage(),
                                    "Error de consulta",
                                    JOptionPane.ERROR_MESSAGE
                            );

                        } finally {
                            establecerOcupado(false);
                        }
                    }
                };

        trabajador.execute();
    }

    // SELECTOR DE EQUIPOS

    private void mostrarSelectorEquipos() {

        if (ocupado) {
            return;
        }

        JDialog dialogo = new JDialog(
                this,
                "SIGELAB - Seleccionar equipo",
                true
        );

        dialogo.setSize(800, 530);
        dialogo.setMinimumSize(
                new Dimension(650, 420)
        );
        dialogo.setLocationRelativeTo(this);

        JPanel principal = new JPanel(
                new BorderLayout(12, 12)
        );
        principal.setBackground(Color.WHITE);
        principal.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JLabel titulo = new JLabel(
                "Seleccionar equipo para generar QR"
        );
        titulo.setFont(
                new Font("SansSerif", Font.BOLD, 19)
        );

        JLabel descripcion = new JLabel(
                "Busca por nombre, código, tipo o estado."
        );
        descripcion.setForeground(GRIS);

        JPanel encabezado = new JPanel(
                new GridLayout(2, 1, 0, 5)
        );
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(descripcion);

        JTextField txtFiltro = new JTextField();
        txtFiltro.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        JPanel panelFiltro = new JPanel(
                new BorderLayout(10, 0)
        );
        panelFiltro.setOpaque(false);
        panelFiltro.add(
                new JLabel("Buscar:"),
                BorderLayout.WEST
        );
        panelFiltro.add(
                txtFiltro,
                BorderLayout.CENTER
        );

        JPanel superior = new JPanel(
                new BorderLayout(0, 15)
        );
        superior.setOpaque(false);
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(panelFiltro, BorderLayout.SOUTH);

        principal.add(superior, BorderLayout.NORTH);

        // TABLA DE EQUIPOS

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new String[]{
                                "Código",
                                "Equipo",
                                "Tipo",
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

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabla.setFillsViewportHeight(true);
        tabla.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        TableRowSorter<DefaultTableModel> ordenador =
                new TableRowSorter<>(modelo);

        tabla.setRowSorter(ordenador);

        txtFiltro.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void filtrar() {
                        String texto =
                                txtFiltro.getText().trim();

                        if (texto.isEmpty()) {
                            ordenador.setRowFilter(null);
                        } else {
                            ordenador.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + Pattern.quote(texto)
                                    )
                            );
                        }
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filtrar();
                    }
                }
        );

        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        // BOTONES DEL SELECTOR

        JLabel lblCantidad = new JLabel(
                "Cargando equipos..."
        );

        JButton btnSeleccionar = crearBoton(
                "Seleccionar equipo",
                AZUL
        );

        JButton btnCancelar = crearBoton(
                "Cancelar",
                GRIS
        );

        btnSeleccionar.setEnabled(false);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnSeleccionar);

        JPanel inferior = new JPanel(
                new BorderLayout(10, 0)
        );
        inferior.setOpaque(false);
        inferior.add(lblCantidad, BorderLayout.WEST);
        inferior.add(botones, BorderLayout.EAST);

        principal.add(inferior, BorderLayout.SOUTH);
        dialogo.setContentPane(principal);

        Runnable seleccionar = () -> {

            int filaVista = tabla.getSelectedRow();

            if (filaVista == -1) {
                JOptionPane.showMessageDialog(
                        dialogo,
                        "Selecciona un equipo de la tabla.",
                        "Sin selección",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int filaModelo =
                    tabla.convertRowIndexToModel(filaVista);

            Object valor = modelo.getValueAt(
                    filaModelo,
                    0
            );

            if (valor == null) {
                return;
            }

            String codigo = valor.toString();

            txtCodigo.setText(codigo);
            dialogo.dispose();

            // Recupera el QR si ya existe en Azure.
            buscarEquipo();
        };

        btnSeleccionar.addActionListener(
                e -> seleccionar.run()
        );

        tabla.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2
                                && tabla.getSelectedRow() != -1) {
                            seleccionar.run();
                        }
                    }
                }
        );

        btnCancelar.addActionListener(
                e -> dialogo.dispose()
        );

        // CARGAR EQUIPOS DESDE SQL SERVER

        SwingWorker<List<DatosEquipoQR>, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected List<DatosEquipoQR>
                    doInBackground() throws Exception {
                        return repository.listarEquipos();
                    }

                    @Override
                    protected void done() {
                        if (!dialogo.isDisplayable()) {
                            return;
                        }

                        try {
                            List<DatosEquipoQR> equipos = get();

                            for (DatosEquipoQR equipo : equipos) {
                                modelo.addRow(new Object[]{
                                        equipo.getCodigoInventario(),
                                        equipo.getNombre(),
                                        equipo.getTipo(),
                                        equipo.getEstado()
                                });
                            }

                            lblCantidad.setText(
                                    equipos.size()
                                            + " equipos encontrados"
                            );

                            btnSeleccionar.setEnabled(
                                    !equipos.isEmpty()
                            );

                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            lblCantidad.setText(
                                    "Consulta interrumpida."
                            );

                        } catch (ExecutionException ex) {
                            Throwable causa = ex.getCause() != null
                                    ? ex.getCause()
                                    : ex;

                            lblCantidad.setText(
                                    "Error al cargar equipos."
                            );

                            JOptionPane.showMessageDialog(
                                    dialogo,
                                    causa.getMessage(),
                                    "Error de consulta",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        trabajador.execute();
        dialogo.setVisible(true);
    }

    // GENERAR Y GUARDAR QR AUTOMÁTICAMENTE EN AZURE

    private void generarQR() {

        if (ocupado || equipoActual == null) {
            return;
        }

        final String codigo =
                equipoActual.getCodigoInventario();

        final int version = versionConsulta;

        establecerOcupado(true);

        lblMensaje.setText(
                "Generando y guardando QR en Azure..."
        );

        SwingWorker<BufferedImage, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected BufferedImage doInBackground()
                            throws Exception {

                        String contenido =
                                controller.generarContenidoQR(
                                        codigo
                                );

                        BufferedImage imagen =
                                QRUtils.generarQR(
                                        contenido,
                                        250,
                                        250
                                );

                        // GUARDA LA IMAGEN EN SQL SERVER.
                        repository.guardarImagenQR(
                                codigo,
                                imagen
                        );

                        return imagen;
                    }

                    @Override
                    protected void done() {

                        try {
                            BufferedImage imagen = get();

                            if (version != versionConsulta
                                    || equipoActual == null
                                    || !codigo.equals(
                                    equipoActual
                                            .getCodigoInventario()
                            )) {
                                return;
                            }

                            mostrarImagenQR(imagen);

                            lblMensaje.setText(
                                    "Código QR generado y guardado "
                                            + "en Azure correctamente."
                            );

                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();

                            lblMensaje.setText(
                                    "Generación interrumpida."
                            );

                        } catch (ExecutionException ex) {
                            Throwable causa = ex.getCause() != null
                                    ? ex.getCause()
                                    : ex;

                            lblMensaje.setText(
                                    "No se pudo guardar el QR en Azure."
                            );

                            JOptionPane.showMessageDialog(
                                    GenerarEquipoQRForm.this,
                                    causa.getMessage(),
                                    "Error al guardar QR",
                                    JOptionPane.ERROR_MESSAGE
                            );

                        } finally {
                            establecerOcupado(false);
                        }
                    }
                };

        trabajador.execute();
    }

    // GUARDAR COPIA PNG EN LA COMPUTADORA

    private void guardarQR() {

        if (ocupado || imagenQR == null
                || equipoActual == null) {
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar código QR");

        selector.setSelectedFile(
                new File(
                        "QR_"
                                + equipoActual.getCodigoInventario()
                                + ".png"
                )
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName()
                .toLowerCase()
                .endsWith(".png")) {

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
                    "Copia PNG guardada correctamente."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "QR guardado en:\n"
                            + archivo.getAbsolutePath(),
                    "Archivo guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al guardar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new GenerarEquipoQRForm()
                        .setVisible(true)
        );
    }
}
