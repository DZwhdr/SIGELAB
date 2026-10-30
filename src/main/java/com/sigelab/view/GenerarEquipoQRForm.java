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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private JButton btnExplorar;
    private JButton btnGenerar;
    private JButton btnGuardar;
    private JButton btnVolver;

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
        setSize(750, 730);
        setMinimumSize(new Dimension(700, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
                "Genera y registra códigos QR de equipos en SIGELAB."
        );

        subtitulo.setForeground(new Color(105, 115, 125));

        encabezado.add(titulo);
        encabezado.add(subtitulo);

        principal.add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(20, 22, 20, 22));

        // Búsqueda por código de inventario
        JPanel busqueda = new JPanel(new BorderLayout(10, 8));
        busqueda.setOpaque(false);
        busqueda.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 85)
        );

        JLabel lblBuscar = new JLabel("Código de inventario");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));

        txtCodigo = new JTextField();
        txtCodigo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        btnBuscar = crearBoton(
                "Buscar equipo",
                new Color(38, 91, 160)
        );

        btnExplorar = crearBoton(
                "Buscar por nombre",
                new Color(52, 73, 94)
        );

        JPanel botonesBusqueda = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        botonesBusqueda.setOpaque(false);
        botonesBusqueda.add(btnBuscar);
        botonesBusqueda.add(btnExplorar);

        JPanel filaBusqueda = new JPanel(new BorderLayout(10, 0));
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(txtCodigo, BorderLayout.CENTER);
        filaBusqueda.add(botonesBusqueda, BorderLayout.EAST);

        busqueda.add(lblBuscar, BorderLayout.NORTH);
        busqueda.add(filaBusqueda, BorderLayout.CENTER);

        contenido.add(busqueda);
        contenido.add(Box.createVerticalStrut(18));

        // Datos del equipo
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

        // Vista previa del QR
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

        // Panel inferior
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

        btnVolver = crearBoton(
                "← Volver al menú principal",
                new Color(30, 60, 114)
        );

        JPanel panelVolver = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );

        panelVolver.setOpaque(false);
        panelVolver.add(btnVolver);

        JPanel acciones = new JPanel(new BorderLayout(0, 12));
        acciones.setOpaque(false);
        acciones.add(botones, BorderLayout.NORTH);
        acciones.add(panelVolver, BorderLayout.SOUTH);

        inferior.add(lblMensaje, BorderLayout.NORTH);
        inferior.add(acciones, BorderLayout.SOUTH);

        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);

        // Eventos
        btnBuscar.addActionListener(e -> buscarEquipo());
        btnExplorar.addActionListener(e -> abrirBuscador());
        btnGenerar.addActionListener(e -> generarQR());
        btnGuardar.addActionListener(e -> guardarQR());
        btnVolver.addActionListener(e -> dispose());

        txtCodigo.addActionListener(e -> buscarEquipo());

        txtCodigo.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        invalidarEquipo();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        invalidarEquipo();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        invalidarEquipo();
                    }
                }
        );
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

    private void invalidarEquipo() {

        if (lblNombre != null) {
            limpiarResultado();
            lblMensaje.setText("Busca un equipo para comenzar.");
        }
    }

    // Buscar equipo por código exacto
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
        btnExplorar.setEnabled(false);

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
                        btnExplorar.setEnabled(true);

                        if (!txtCodigo.getText().trim().equals(codigo)) {
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
                                                + "con ese código de inventario.",
                                        "Equipo no encontrado",
                                        JOptionPane.WARNING_MESSAGE
                                );

                                return;
                            }

                            mostrarEquipo(equipo);

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();
                            lblMensaje.setText("Consulta interrumpida.");

                        } catch (ExecutionException e) {

                            Throwable causa = e.getCause() != null
                                    ? e.getCause()
                                    : e;

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

    // Mostrar equipo seleccionado
    private void mostrarEquipo(DatosEquipoQR equipo) {

        limpiarResultado();

        equipoActual = equipo;

        lblNombre.setText("Equipo: " + equipo.getNombre());
        lblTipo.setText("Tipo: " + equipo.getTipo());
        lblEstado.setText("Estado: " + equipo.getEstado());

        btnGenerar.setEnabled(true);

        lblMensaje.setText(
                "Equipo encontrado. Puedes generar su QR."
        );
    }

    // Buscar equipos por nombre o código
    private void abrirBuscador() {

        JDialog dialogo = new JDialog(
                this,
                "Buscar equipos registrados",
                true
        );

        dialogo.setSize(780, 510);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(12, 12));

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel lblInstruccion = new JLabel(
                "Escribe parte del nombre o código del equipo:"
        );

        JTextField txtFiltro = new JTextField();

        JButton btnFiltrar = crearBoton(
                "Buscar",
                new Color(38, 91, 160)
        );

        JButton btnTodos = crearBoton(
                "Ver todos",
                new Color(52, 73, 94)
        );

        JPanel filaFiltro = new JPanel(new BorderLayout(8, 0));
        filaFiltro.add(txtFiltro, BorderLayout.CENTER);

        JPanel accionesFiltro = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        accionesFiltro.add(btnFiltrar);
        accionesFiltro.add(btnTodos);

        filaFiltro.add(accionesFiltro, BorderLayout.EAST);

        panelSuperior.add(lblInstruccion, BorderLayout.NORTH);
        panelSuperior.add(filaFiltro, BorderLayout.CENTER);

        dialogo.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de resultados
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{
                        "Código",
                        "Equipo",
                        "Tipo",
                        "Estado"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);

        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoCreateRowSorter(true);

        List<DatosEquipoQR> resultados = new ArrayList<>();

        JLabel lblResultados = new JLabel(
                "Escribe para buscar o presiona Ver todos."
        );

        JPanel panelCentral = new JPanel(new BorderLayout(8, 8));
        panelCentral.setBorder(new EmptyBorder(5, 15, 5, 15));

        panelCentral.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        panelCentral.add(
                lblResultados,
                BorderLayout.SOUTH
        );

        dialogo.add(panelCentral, BorderLayout.CENTER);

        JButton btnSeleccionar = crearBoton(
                "Seleccionar equipo",
                new Color(39, 174, 96)
        );

        JButton btnCancelar = crearBoton(
                "Cancelar",
                new Color(192, 57, 43)
        );

        btnSeleccionar.setEnabled(false);

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );

        panelInferior.add(btnCancelar);
        panelInferior.add(btnSeleccionar);

        dialogo.add(panelInferior, BorderLayout.SOUTH);

        tabla.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                btnSeleccionar.setEnabled(
                        tabla.getSelectedRow() >= 0
                );
            }
        });

        // Seleccionar un equipo de la tabla
        Runnable seleccionarEquipo = () -> {

            int filaVista = tabla.getSelectedRow();

            if (filaVista < 0) {
                return;
            }

            int filaModelo = tabla.convertRowIndexToModel(filaVista);

            if (filaModelo < 0 || filaModelo >= resultados.size()) {
                return;
            }

            DatosEquipoQR seleccionado = resultados.get(filaModelo);

            txtCodigo.setText(
                    seleccionado.getCodigoInventario()
            );

            mostrarEquipo(seleccionado);

            dialogo.dispose();
        };

        btnSeleccionar.addActionListener(
                e -> seleccionarEquipo.run()
        );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2) {
                            seleccionarEquipo.run();
                        }
                    }
                }
        );

        btnCancelar.addActionListener(e -> dialogo.dispose());

        // Control de búsquedas
        final long[] versionBusqueda = {0};

        java.util.function.Consumer<Boolean> cargar = verTodos -> {

            String filtro = txtFiltro.getText().trim();

            long versionActual = ++versionBusqueda[0];

            if (!verTodos && filtro.isEmpty()) {

                modelo.setRowCount(0);
                resultados.clear();

                lblResultados.setText(
                        "Escribe al menos una letra para buscar."
                );

                btnSeleccionar.setEnabled(false);

                return;
            }

            btnFiltrar.setEnabled(false);
            btnTodos.setEnabled(false);
            btnSeleccionar.setEnabled(false);

            modelo.setRowCount(0);
            resultados.clear();

            lblResultados.setText("Consultando SQL Server...");

            SwingWorker<List<DatosEquipoQR>, Void> trabajador =
                    new SwingWorker<>() {

                        @Override
                        protected List<DatosEquipoQR> doInBackground()
                                throws Exception {

                            if (verTodos) {
                                return repository.listarEquipos();
                            }

                            return repository.buscarEquipos(filtro);
                        }

                        @Override
                        protected void done() {

                            if (!dialogo.isDisplayable()
                                    || versionActual != versionBusqueda[0]) {
                                return;
                            }

                            btnFiltrar.setEnabled(true);
                            btnTodos.setEnabled(true);

                            try {

                                List<DatosEquipoQR> encontrados = get();

                                resultados.clear();
                                resultados.addAll(encontrados);

                                modelo.setRowCount(0);

                                for (DatosEquipoQR equipo : encontrados) {

                                    modelo.addRow(new Object[]{
                                            equipo.getCodigoInventario(),
                                            equipo.getNombre(),
                                            equipo.getTipo(),
                                            equipo.getEstado()
                                    });
                                }

                                lblResultados.setText(
                                        "Equipos encontrados: "
                                                + encontrados.size()
                                );

                            } catch (InterruptedException ex) {

                                Thread.currentThread().interrupt();

                                lblResultados.setText(
                                        "Consulta interrumpida."
                                );

                            } catch (ExecutionException ex) {

                                Throwable causa = ex.getCause() != null
                                        ? ex.getCause()
                                        : ex;

                                lblResultados.setText(
                                        "Error al consultar equipos."
                                );

                                JOptionPane.showMessageDialog(
                                        dialogo,
                                        causa.getMessage(),
                                        "Error de SQL Server",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    };

            trabajador.execute();
        };

        btnFiltrar.addActionListener(
                e -> cargar.accept(false)
        );

        btnTodos.addActionListener(
                e -> cargar.accept(true)
        );

        txtFiltro.addActionListener(
                e -> cargar.accept(false)
        );

        // Búsqueda automática mientras escribes
        Timer temporizador = new Timer(
                350,
                e -> cargar.accept(false)
        );

        temporizador.setRepeats(false);

        txtFiltro.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void actualizar() {
                        versionBusqueda[0]++;
                        temporizador.restart();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        actualizar();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        actualizar();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        actualizar();
                    }
                }
        );

        dialogo.setVisible(true);
        temporizador.stop();
    }

    // Generar QR y registrarlo en SQL Server
    private void generarQR() {

        if (equipoActual == null) {
            return;
        }

        DatosEquipoQR equipo = equipoActual;

        btnGenerar.setEnabled(false);
        btnGuardar.setEnabled(false);

        lblMensaje.setText(
                "Generando y registrando código QR..."
        );

        SwingWorker<BufferedImage, Void> trabajador =
                new SwingWorker<>() {

                    @Override
                    protected BufferedImage doInBackground()
                            throws Exception {

                        // Generar el contenido QR
                        String contenido =
                                controller.generarContenidoQR(
                                        equipo.getCodigoInventario()
                                );

                        // Generar la imagen QR
                        BufferedImage imagen = QRUtils.generarQR(
                                contenido,
                                250,
                                250
                        );

                        // Registrar el QR en SQL Server
                        repository.guardarQR(
                                equipo.getId(),
                                contenido
                        );

                        return imagen;
                    }

                    @Override
                    protected void done() {

                        try {

                            BufferedImage imagen = get();

                            // Evitar mostrar QR de otro equipo
                            if (equipoActual != equipo) {
                                return;
                            }

                            imagenQR = imagen;

                            lblQR.setIcon(
                                    new ImageIcon(imagenQR)
                            );

                            lblQR.setText("");

                            btnGuardar.setEnabled(true);

                            lblMensaje.setText(
                                    "QR generado y registrado en SQL Server."
                            );

                            JOptionPane.showMessageDialog(
                                    GenerarEquipoQRForm.this,
                                    "¡Código QR registrado correctamente!\n\n"
                                            + "Equipo: "
                                            + equipo.getNombre()
                                            + "\nCódigo: "
                                            + equipo.getCodigoInventario(),
                                    "SIGELAB",
                                    JOptionPane.INFORMATION_MESSAGE
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
                                    "No se pudo registrar el QR."
                            );

                            JOptionPane.showMessageDialog(
                                    GenerarEquipoQRForm.this,
                                    "Error al generar o registrar el QR:\n"
                                            + causa.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );

                        } finally {

                            if (equipoActual == equipo) {
                                btnGenerar.setEnabled(true);
                            }
                        }
                    }
                };

        trabajador.execute();
    }

    // Guardar código QR como imagen PNG
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
