package com.sigelab.controller;

import com.sigelab.model.Usuario;
import com.sigelab.service.UsuarioService;

import java.util.List;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    public Usuario procesarLogin(String username, String password) {
        return usuarioService.autenticar(username, password);
    }

    // Métodos para el CRUD de Usuarios
    public List<Usuario> obtenerListaUsuarios() {
        return usuarioService.listarUsuarios();
    }

    public boolean registrarUsuario(Usuario u) {
        return usuarioService.crearUsuario(u);
    }

    public boolean actualizarUsuario(Usuario u) {
        return usuarioService.actualizarUsuario(u);
    }

    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) {
        return usuarioService.cambiarEstadoUsuario(idUsuario, activo);
    }
}