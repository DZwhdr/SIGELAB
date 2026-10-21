
package com.sigelab.repository;

import com.sigelab.model.Rol;
import com.sigelab.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Obtiene todos los usuarios de la base de datos
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
                SELECT
                    id AS id_usuario,
                    username,
                    password,
                    nombre,
                    apellido,
                    correo,
                    CASE
                        WHEN estado = 'ACTIVO' THEN 1
                        ELSE 0
                    END AS activo,
                    rol_id AS id_rol,
                    CASE
                        WHEN rol_id = 1 THEN 'Administrador'
                        ELSE 'Operador'
                    END AS nombre_rol
                FROM dbo.usuario
                """;

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
            System.err.println(
                    "Error al obtener usuarios: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return lista;
    }

    // Inserta un nuevo usuario
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
            ps.setInt(7, u.getRol() != null ? u.getRol().getId() : 3);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualiza los datos de un usuario
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

    // Cambia el estado de un usuario
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
