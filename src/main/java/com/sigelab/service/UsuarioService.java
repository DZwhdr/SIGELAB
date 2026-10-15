package com.sigelab.service;

import com.sigelab.model.Usuario;
import com.sigelab.repository.UsuarioDAO;

import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

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

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public boolean crearUsuario(Usuario u) {
        return usuarioDAO.insertar(u);
    }

    public boolean actualizarUsuario(Usuario u) {
        return usuarioDAO.actualizar(u);
    }

    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) {
        return usuarioDAO.cambiarEstado(idUsuario, activo);
    }
}