package com.sigelab.view;

import com.sigelab.controller.UsuarioController;
import com.sigelab.model.Rol;
import com.sigelab.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioManagementView extends JFrame {

    private JTable tblUsuarios;
    private DefaultTableModel modelTabla;
    private JTextField txtUsername, txtNombre, txtApellido, txtCorreo;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JCheckBox chkActivo;
    private JButton btnCrear, btnModificar, btnLimpiar, btnCambiarEstado;

    private UsuarioController usuarioController;
    private int idUsuarioSeleccionado = -1; // -1 indica que no hay selección (modo creación)

    public UsuarioManagementView() {
        this.usuarioController = new UsuarioController();

        setTitle("SIGELAB - Gestión de Usuarios (CRUD)");
        setSize(850, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        // Encabezado
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 850, 50);
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setLayout(null);

        JLabel lblTitulo = new JLabel("Módulo de Gestión de Usuarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(20, 10, 350, 30);
        headerPanel.add(lblTitulo);
        panel.add(headerPanel);

        // Formulario (Izquierda)
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(20, 70, 80, 20);
        panel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 70, 180, 25);
        panel.add(txtUsername);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(20, 105, 80, 20);
        panel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 105, 180, 25);
        panel.add(txtPassword);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setBounds(20, 140, 80, 20);
        panel.add(lblNom);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 140, 180, 25);
        panel.add(txtNombre);

        JLabel lblApe = new JLabel("Apellido:");
        lblApe.setBounds(20, 175, 80, 20);
        panel.add(lblApe);

        txtApellido = new JTextField();
        txtApellido.setBounds(100, 175, 180, 25);
        panel.add(txtApellido);

        JLabel lblMail = new JLabel("Correo:");
        lblMail.setBounds(20, 210, 80, 20);
        panel.add(lblMail);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(100, 210, 180, 25);
        panel.add(txtCorreo);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(20, 245, 80, 20);
        panel.add(lblRol);

        cbRol = new JComboBox<>(new String[]{"ADMIN", "ENCARGADO", "OPERADOR"});
        cbRol.setBounds(100, 245, 180, 25);
        panel.add(cbRol);

        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setSelected(true);
        chkActivo.setBounds(100, 280, 180, 25);
        chkActivo.setBackground(new Color(245, 247, 250));
        panel.add(chkActivo);

        // -------------------------------------------------------------------------
        // BOTONES ORGANIZADOS (Crear, Modificar, Limpiar, Cambiar Estado)
        // -------------------------------------------------------------------------
        btnCrear = new JButton("Crear");
        btnCrear.setBounds(20, 325, 125, 32);
        btnCrear.setBackground(new Color(39, 174, 96)); // Verde
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnCrear);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(155, 325, 125, 32);
        btnModificar.setBackground(new Color(41, 128, 185)); // Azul
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnModificar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(20, 370, 125, 32);
        btnLimpiar.setBackground(new Color(127, 140, 141)); // Gris
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnLimpiar);

        btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.setBounds(155, 370, 125, 32);
        btnCambiarEstado.setBackground(new Color(192, 57, 43)); // Rojo
        btnCambiarEstado.setForeground(Color.WHITE);
        btnCambiarEstado.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(btnCambiarEstado);

        // Tabla de Usuarios (Derecha)
        String[] columnas = {"ID", "Usuario", "Nombre", "Apellido", "Rol", "Estado"};
        modelTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsuarios = new JTable(modelTabla);
        JScrollPane scrollTabla = new JScrollPane(tblUsuarios);
        scrollTabla.setBounds(310, 70, 510, 430);
        panel.add(scrollTabla);

        // Cargar los datos iniciales desde la base de datos
        cargarTablaUsuarios();

        // -------------------------------------------------------------------------
        // EVENTOS DE LOS BOTONES
        // -------------------------------------------------------------------------
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnCrear.addActionListener(e -> {
            if (validarCampos()) {
                Usuario nuevo = construirUsuarioDesdeFormulario();
                boolean exito = usuarioController.registrarUsuario(nuevo);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                    cargarTablaUsuarios();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnModificar.addActionListener(e -> {
            if (idUsuarioSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un usuario de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (validarCamposModificacion()) {
                Usuario modificado = construirUsuarioDesdeFormulario();
                modificado.setIdUsuario(idUsuarioSeleccionado);
                boolean exito = usuarioController.actualizarUsuario(modificado);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Usuario modificado exitosamente.");
                    cargarTablaUsuarios();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCambiarEstado.addActionListener(e -> {
            if (idUsuarioSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean nuevoEstado = chkActivo.isSelected();
            boolean exito = usuarioController.cambiarEstadoUsuario(idUsuarioSeleccionado, nuevoEstado);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Estado del usuario actualizado.");
                cargarTablaUsuarios();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Evento de selección en la tabla
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int row = tblUsuarios.getSelectedRow();
            if (row != -1) {
                idUsuarioSeleccionado = Integer.parseInt(modelTabla.getValueAt(row, 0).toString());
                txtUsername.setText(modelTabla.getValueAt(row, 1).toString());
                txtNombre.setText(modelTabla.getValueAt(row, 2).toString());
                txtApellido.setText(modelTabla.getValueAt(row, 3).toString());
                cbRol.setSelectedItem(modelTabla.getValueAt(row, 4).toString());
                chkActivo.setSelected("Activo".equals(modelTabla.getValueAt(row, 5).toString()));
                txtPassword.setText(""); // Se deja vacío por seguridad
            }
        });

        add(panel);
    }

    // Método para cargar usuarios de la BD a la JTable
    private void cargarTablaUsuarios() {
        modelTabla.setRowCount(0); // Limpiar tabla
        List<Usuario> lista = usuarioController.obtenerListaUsuarios();
        for (Usuario u : lista) {
            String nombreRol = (u.getRol() != null) ? u.getRol().getNombre() : "OPERADOR";
            String estadoStr = u.isActivo() ? "Activo" : "Inactivo";
            Object[] fila = {
                    u.getIdUsuario(),
                    u.getUsername(),
                    u.getNombre(),
                    u.getApellido(),
                    nombreRol,
                    estadoStr
            };
            modelTabla.addRow(fila);
        }
    }

    private Usuario construirUsuarioDesdeFormulario() {
        Usuario u = new Usuario();
        u.setUsername(txtUsername.getText().trim());
        u.setPassword(new String(txtPassword.getPassword()).trim());
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setCorreo(txtCorreo.getText().trim().isEmpty() ? "sin_correo@sigelab.com" : txtCorreo.getText().trim());
        u.setActivo(chkActivo.isSelected());

        // Asignar rol según el ComboBox
        String rolSeleccionado = cbRol.getSelectedItem().toString();
        Rol rol = new Rol();
        if ("ADMIN".equals(rolSeleccionado)) {
            rol.setId(1);
            rol.setNombre("ADMIN");
        } else if ("ENCARGADO".equals(rolSeleccionado)) {
            rol.setId(2);
            rol.setNombre("ENCARGADO");
        } else {
            rol.setId(3);
            rol.setNombre("OPERADOR");
        }
        u.setRol(rol);

        return u;
    }

    private boolean validarCampos() {
        if (txtUsername.getText().trim().isEmpty() || new String(txtPassword.getPassword()).trim().isEmpty() ||
                txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios (Usuario, Contraseña, Nombre, Apellido).", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validarCamposModificacion() {
        if (txtUsername.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        idUsuarioSeleccionado = -1;
        txtUsername.setText("");
        txtPassword.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        cbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tblUsuarios.clearSelection();
    }
}