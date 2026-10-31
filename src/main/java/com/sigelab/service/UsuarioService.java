package com.sigelab.service;

import com.sigelab.model.Usuario;
import com.sigelab.repository.UsuarioDAO;

import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // Autentico el usuario revisando la lista y el estado activo
    public Usuario autenticar(String username, String password) {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password) && u.isActivo()) {
                System.out.println("Servicio (Lógica): Login exitoso para el usuario -> " + username);
                return u;
            }
        }
        return null;
    }

    // Traigo todos los usuarios registrados
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    // Creo un usuario nuevo en la BD
    public boolean crearUsuario(Usuario u) {
        return usuarioDAO.insertar(u);
    }

    // Actualizo la info del usuario
    public boolean actualizarUsuario(Usuario u) {
        return usuarioDAO.actualizar(u);
    }

    // Cambio el estado (activo/inactivo) del usuario
    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) {
        return usuarioDAO.cambiarEstado(idUsuario, activo);
    }

    // Actualizo la contraseña del usuario directo en la BD
    public boolean actualizarPassword(String username, String nuevaPassword) {
        return usuarioDAO.actualizarPassword(username, nuevaPassword);
    }
}