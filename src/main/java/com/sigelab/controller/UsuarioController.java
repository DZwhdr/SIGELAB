package com.sigelab.controller;

import com.sigelab.model.Usuario;
import com.sigelab.service.UsuarioService;

import java.util.List;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    // Valido el login contra el servicio y devuelvo el usuario completo con sus flags de seguridad
    public Usuario procesarLogin(String username, String password) {
        return usuarioService.autenticar(username, password);
    }

    // Listar todos los usuarios para la tabla de administración
    public List<Usuario> obtenerListaUsuarios() {
        return usuarioService.listarUsuarios();
    }

    // Registrar un nuevo usuario desde el panel admin con su primer login obligatorio
    public boolean registrarUsuario(Usuario u) {
        return usuarioService.crearUsuario(u);
    }

    // Actualizar datos generales del usuario
    public boolean actualizarUsuario(Usuario u) {
        return usuarioService.actualizarUsuario(u);
    }

    // Activar o bloquear usuarios en la BD
    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) {
        return usuarioService.cambiarEstadoUsuario(idUsuario, activo);
    }

    // Método para cambiar la contraseña y quitar el flag de primer login directo en la BD
    public boolean actualizarPassword(String username, String nuevaPassword) {
        return usuarioService.actualizarPassword(username, nuevaPassword);
    }
}