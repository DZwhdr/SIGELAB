package com.sigelab.view;

import com.sigelab.controller.UsuarioController;
import com.sigelab.model.Rol;
import com.sigelab.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioManagementView extends JFrame {

    private UsuarioController usuarioController;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;

    private boolean modoOscuro;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel pnlCentro;
    private JPanel pnlForm;
    private JPanel pnlFooter;
    private JScrollPane scrollTabla;
    private JLabel lblTitulo;
    private JLabel lblSub;
    private JLabel[] etiquetasForm;

    public UsuarioManagementView(boolean modoOscuro) {
        this.usuarioController = new UsuarioController();
        this.modoOscuro = modoOscuro;

        setTitle("SIGELAB - Módulo de Gestión de Usuarios y Accesos");
        setSize(920, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        cargarUsuariosEnTabla();
        aplicarTemaVisual();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());

        // Encabezado superior con estilo ejecutivo
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblTitulo = new JLabel("Módulo de Administración de Usuarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        lblSub = new JLabel("Control de accesos, roles y seguridad corporativa");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(lblSub, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel central dividido entre el formulario y la tabla de registros
        pnlCentro = new JPanel(new BorderLayout(15, 0));
        pnlCentro.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Formulario a la izquierda para capturar los datos
        pnlForm = new JPanel(null);
        pnlForm.setPreferredSize(new Dimension(380, 0));

        int xLbl = 15, xTxt = 105, wLbl = 85, wTxt = 250, hElem = 32, yInicio = 25, espacio = 40;

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 11));
        lblUser.setBounds(xLbl, yInicio, wLbl, hElem);
        pnlForm.add(lblUser);
        txtUsername = new JTextField();
        txtUsername.setBounds(xTxt, yInicio, wTxt, hElem);
        pnlForm.add(txtUsername);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 11));
        lblPass.setBounds(xLbl, yInicio + espacio, wLbl, hElem);
        pnlForm.add(lblPass);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(xTxt, yInicio + espacio, wTxt, hElem);
        pnlForm.add(txtPassword);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setFont(new Font("Arial", Font.BOLD, 11));
        lblNom.setBounds(xLbl, yInicio + (espacio * 2), wLbl, hElem);
        pnlForm.add(lblNom);
        txtNombre = new JTextField();
        txtNombre.setBounds(xTxt, yInicio + (espacio * 2), wTxt, hElem);
        pnlForm.add(txtNombre);

        JLabel lblApe = new JLabel("Apellido:");
        lblApe.setFont(new Font("Arial", Font.BOLD, 11));
        lblApe.setBounds(xLbl, yInicio + (espacio * 3), wLbl, hElem);
        pnlForm.add(lblApe);
        txtApellido = new JTextField();
        txtApellido.setBounds(xTxt, yInicio + (espacio * 3), wTxt, hElem);
        pnlForm.add(txtApellido);

        JLabel lblCorr = new JLabel("Correo:");
        lblCorr.setFont(new Font("Arial", Font.BOLD, 11));
        lblCorr.setBounds(xLbl, yInicio + (espacio * 4), wLbl, hElem);
        pnlForm.add(lblCorr);
        txtCorreo = new JTextField();
        txtCorreo.setBounds(xTxt, yInicio + (espacio * 4), wTxt, hElem);
        pnlForm.add(txtCorreo);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Arial", Font.BOLD, 11));
        lblRol.setBounds(xLbl, yInicio + (espacio * 5), wLbl, hElem);
        pnlForm.add(lblRol);
        cmbRol = new JComboBox<>(new String[]{"ADMIN", "ENCARGADO", "OPERADOR"});
        cmbRol.setBounds(xTxt, yInicio + (espacio * 5), wTxt, hElem);
        pnlForm.add(cmbRol);

        chkActivo = new JCheckBox("Usuario Activo en el Sistema", true);
        chkActivo.setFont(new Font("Arial", Font.PLAIN, 12));
        chkActivo.setOpaque(false);
        chkActivo.setBounds(xTxt, yInicio + (espacio * 6) - 5, wTxt, 25);
        pnlForm.add(chkActivo);

        etiquetasForm = new JLabel[]{lblUser, lblPass, lblNom, lblApe, lblCorr, lblRol};

        // Botón para abrir la cámara y capturar las 52 fotos biométricas del usuario
        JButton btnBio = new JButton("📷 Capturar Rostro (52 Fotos)");
        btnBio.setBounds(15, yInicio + (espacio * 6) + 25, 340, 36);
        btnBio.setBackground(new Color(41, 128, 185));
        btnBio.setForeground(Color.WHITE);
        btnBio.setFont(new Font("Arial", Font.BOLD, 11));
        btnBio.setFocusPainted(false);
        btnBio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBio.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese el nombre de usuario antes de capturar la biometría facial.",
                        "Campo Requerido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Le paso el username exacto para que el servicio lo encuentre y guarde las muestras en SQL Server
            CamaraCaptureDialog dialogCamara = new CamaraCaptureDialog(
                    this,
                    "Registro Facial Corporativo - " + username,
                    true,
                    username
            );
            dialogCamara.setVisible(true);
        });
        pnlForm.add(btnBio);

        // Botones de acción del CRUD
        JButton btnCrear = crearBotonEstilizado("Crear", new Color(39, 174, 96));
        btnCrear.setBounds(15, yInicio + (espacio * 8) - 10, 162, 36);
        btnCrear.addActionListener(e -> registrarUsuario());
        pnlForm.add(btnCrear);

        JButton btnModificar = crearBotonEstilizado("Modificar", new Color(41, 128, 185));
        btnModificar.setBounds(193, yInicio + (espacio * 8) - 10, 162, 36);
        btnModificar.addActionListener(e -> modificarUsuario());
        pnlForm.add(btnModificar);

        JButton btnLimpiar = crearBotonEstilizado("Limpiar", new Color(127, 140, 141));
        btnLimpiar.setBounds(15, yInicio + (espacio * 9) - 10, 162, 36);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        pnlForm.add(btnLimpiar);

        JButton btnCambiarEstado = crearBotonEstilizado("Cambiar Estado", new Color(192, 57, 43));
        btnCambiarEstado.setBounds(193, yInicio + (espacio * 9) - 10, 162, 36);
        btnCambiarEstado.addActionListener(e -> cambiarEstadoUsuario());
        pnlForm.add(btnCambiarEstado);

        pnlCentro.add(pnlForm, BorderLayout.WEST);

        // Tabla de usuarios a la derecha
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Usuario", "Nombre", "Apellido", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(26);
        tablaUsuarios.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Al hacer clic en la tabla paso los datos al formulario
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                cargarDatosEnFormulario(tablaUsuarios.getSelectedRow());
            }
        });

        scrollTabla = new JScrollPane(tablaUsuarios);
        pnlCentro.add(scrollTabla, BorderLayout.CENTER);

        mainPanel.add(pnlCentro, BorderLayout.CENTER);

        // Panel inferior con el botón de cerrar idéntico al de configuración
        pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());
        pnlFooter.add(btnCerrar);

        mainPanel.add(pnlFooter, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Aplico colores y estilos dependiendo si está activado el modo oscuro o claro
    private void aplicarTemaVisual() {
        if (modoOscuro) {
            Color bgGeneral = new Color(20, 24, 33);
            Color bgHeader = new Color(30, 36, 48);
            Color bgCard = new Color(30, 36, 48);
            Color txtColor = Color.WHITE;
            Color inputBg = new Color(40, 48, 62);

            mainPanel.setBackground(bgGeneral);
            headerPanel.setBackground(bgHeader);
            pnlCentro.setBackground(bgGeneral);
            pnlForm.setBackground(bgCard);
            pnlFooter.setBackground(bgHeader);

            lblTitulo.setForeground(new Color(220, 230, 242));
            lblSub.setForeground(new Color(180, 195, 215));

            pnlForm.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 95)),
                    " Datos del Usuario ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(100, 180, 255)
            ));

            scrollTabla.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 95)),
                    " Listado de Usuarios Registrados ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(100, 180, 255)
            ));

            for (JLabel lbl : etiquetasForm) {
                lbl.setForeground(txtColor);
            }
            chkActivo.setForeground(txtColor);

            JTextField[] campos = {txtUsername, txtNombre, txtApellido, txtCorreo};
            for (JTextField campo : campos) {
                campo.setBackground(inputBg);
                campo.setForeground(Color.WHITE);
                campo.setCaretColor(Color.WHITE);
            }
            txtPassword.setBackground(inputBg);
            txtPassword.setForeground(Color.WHITE);
            txtPassword.setCaretColor(Color.WHITE);
            cmbRol.setBackground(inputBg);
            cmbRol.setForeground(Color.WHITE);

            tablaUsuarios.setBackground(new Color(30, 36, 48));
            tablaUsuarios.setForeground(Color.WHITE);
            tablaUsuarios.setGridColor(new Color(60, 75, 95));
            tablaUsuarios.getTableHeader().setBackground(new Color(40, 48, 62));
            tablaUsuarios.getTableHeader().setForeground(Color.WHITE);

        } else {
            Color bgGeneral = new Color(245, 247, 250);
            Color bgHeader = new Color(30, 90, 150);
            Color txtColor = new Color(40, 40, 40);

            mainPanel.setBackground(bgGeneral);
            headerPanel.setBackground(bgHeader);
            pnlCentro.setBackground(bgGeneral);
            pnlForm.setBackground(Color.WHITE);
            pnlFooter.setBackground(new Color(235, 240, 245));

            lblTitulo.setForeground(Color.WHITE);
            lblSub.setForeground(new Color(220, 235, 255));

            pnlForm.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(210, 220, 230)),
                    " Datos del Usuario ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(30, 90, 150)
            ));

            scrollTabla.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(210, 220, 230)),
                    " Listado de Usuarios Registrados ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(30, 90, 150)
            ));

            for (JLabel lbl : etiquetasForm) {
                lbl.setForeground(txtColor);
            }
            chkActivo.setForeground(txtColor);

            JTextField[] campos = {txtUsername, txtNombre, txtApellido, txtCorreo};
            for (JTextField campo : campos) {
                campo.setBackground(Color.WHITE);
                campo.setForeground(Color.BLACK);
                campo.setCaretColor(Color.BLACK);
            }
            txtPassword.setBackground(Color.WHITE);
            txtPassword.setForeground(Color.BLACK);
            txtPassword.setCaretColor(Color.BLACK);
            cmbRol.setBackground(Color.WHITE);
            cmbRol.setForeground(Color.BLACK);

            tablaUsuarios.setBackground(Color.WHITE);
            tablaUsuarios.setForeground(Color.BLACK);
            tablaUsuarios.setGridColor(new Color(220, 220, 220));
            tablaUsuarios.getTableHeader().setBackground(new Color(230, 235, 245));
            tablaUsuarios.getTableHeader().setForeground(Color.BLACK);
        }
        repaint();
    }

    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Cargo los usuarios en la tabla desde el controlador
    private void cargarUsuariosEnTabla() {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = usuarioController.obtenerListaUsuarios();
        for (Usuario u : lista) {
            Object[] fila = {
                    u.getIdUsuario(),
                    u.getUsername(),
                    u.getNombre(),
                    u.getApellido(),
                    u.getRol() != null ? u.getRol().getNombre() : "OPERADOR",
                    u.isActivo() ? "Activo" : "Inactivo"
            };
            modeloTabla.addRow(fila);
        }
    }

    // Relleno el formulario con los datos de la fila seleccionada
    private void cargarDatosEnFormulario(int fila) {
        txtUsername.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtPassword.setText("");
        txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtApellido.setText(modeloTabla.getValueAt(fila, 3).toString());
        cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
        chkActivo.setSelected("Activo".equalsIgnoreCase(modeloTabla.getValueAt(fila, 5).toString()));
    }

    // Limpio los campos del formulario
    private void limpiarCampos() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tablaUsuarios.clearSelection();
    }

    // Registro un nuevo usuario en la base de datos
    private void registrarUsuario() {
        if (txtUsername.getText().isEmpty() || new String(txtPassword.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "El usuario y la contraseña son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = new Usuario();
        u.setUsername(txtUsername.getText().trim());
        u.setPassword(new String(txtPassword.getPassword()));
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setCorreo(txtCorreo.getText().trim().isEmpty() ? txtUsername.getText().trim() + "@sigelab.com" : txtCorreo.getText().trim());
        u.setActivo(chkActivo.isSelected());

        Rol r = new Rol();
        String rolSeleccionado = cmbRol.getSelectedItem().toString();
        r.setId(rolSeleccionado.equals("ADMIN") ? 1 : (rolSeleccionado.equals("ENCARGADO") ? 2 : 3));
        r.setNombre(rolSeleccionado);
        u.setRol(r);

        boolean exito = usuarioController.registrarUsuario(u);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuariosEnTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modifico la información del usuario seleccionado
    private void modificarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

        Usuario u = new Usuario();
        u.setIdUsuario(idUsuario);
        u.setUsername(txtUsername.getText().trim());
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setCorreo(txtCorreo.getText().trim());
        u.setActivo(chkActivo.isSelected());

        Rol r = new Rol();
        String rolSeleccionado = cmbRol.getSelectedItem().toString();
        r.setId(rolSeleccionado.equals("ADMIN") ? 1 : (rolSeleccionado.equals("ENCARGADO") ? 2 : 3));
        r.setNombre(rolSeleccionado);
        u.setRol(r);

        boolean exito = usuarioController.actualizarUsuario(u);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Usuario modificado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuariosEnTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cambio el estado activo o inactivo del usuario
    private void cambiarEstadoUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String estadoActual = modeloTabla.getValueAt(fila, 5).toString();
        boolean nuevoEstado = !("Activo".equalsIgnoreCase(estadoActual));

        boolean exito = usuarioController.cambiarEstadoUsuario(idUsuario, nuevoEstado);
        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Estado del usuario actualizado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuariosEnTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}