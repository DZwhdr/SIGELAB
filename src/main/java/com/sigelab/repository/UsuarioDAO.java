package com.sigelab.repository;

import com.sigelab.model.Rol;
import com.sigelab.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Método para obtener todos los usuarios de la base de datos
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.username, u.password, u.nombre, u.apellido, u.correo, u.activo, " +
                "r.id_rol, r.nombre_rol " +
                "FROM usuarios u JOIN roles r ON u.id_rol = r.id_rol";

        try (Connection con = ConexionBD.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setActivo(rs.getBoolean("activo"));

                Rol r = new Rol();
                r.setId(rs.getInt("id_rol"));
                r.setNombre(rs.getString("nombre_rol"));
                u.setRol(r);

                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para insertar un nuevo usuario
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, nombre, apellido, correo, activo, id_rol) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellido());
            ps.setString(5, u.getCorreo());
            ps.setBoolean(6, u.isActivo());
            ps.setInt(7, u.getRol() != null ? u.getRol().getId() : 3); // Por defecto rol 3 (Operador)

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para actualizar los datos de un usuario
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET username = ?, nombre = ?, apellido = ?, correo = ?, id_rol = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellido());
            ps.setString(4, u.getCorreo());
            ps.setInt(5, u.getRol() != null ? u.getRol().getId() : 3);
            ps.setInt(6, u.getIdUsuario());

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para cambiar el estado (Activo / Inactivo) de un usuario
    public boolean cambiarEstado(int idUsuario, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}