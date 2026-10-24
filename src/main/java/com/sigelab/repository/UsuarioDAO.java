
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

        // Se corrigió el nombre de la tabla a 'usuario' y las columnas reales (id, estado, rol_id)
        String sql = "SELECT u.id, u.username, u.password, u.nombre, u.apellido, u.correo, u.estado, u.rol_id " +
                "FROM usuario u";

        try (Connection con = ConexionBD.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));

                // Mapeo del estado VARCHAR ("ACTIVO" / "INACTIVO") a boolean
                String estadoStr = rs.getString("estado");
                u.setActivo(estadoStr != null && estadoStr.equalsIgnoreCase("ACTIVO"));

                Rol r = new Rol();
                int idRol = rs.getInt("rol_id");
                r.setId(idRol);

                // Asignar nombre del rol según el rol_id
                if (idRol == 1) {
                    r.setNombre("ADMIN");
                } else if (idRol == 2) {
                    r.setNombre("ENCARGADO");
                } else {
                    r.setNombre("OPERADOR");
                }
                u.setRol(r);

                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // Método para insertar un nuevo usuario
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (username, password, nombre, apellido, correo, estado, rol_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername().trim());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellido());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.isActivo() ? "ACTIVO" : "INACTIVO");
            ps.setInt(7, (u.getRol() != null && u.getRol().getId() > 0) ? u.getRol().getId() : 3);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para actualizar los datos de un usuario
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET username = ?, nombre = ?, apellido = ?, correo = ?, estado = ?, rol_id = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername().trim());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellido());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.isActivo() ? "ACTIVO" : "INACTIVO");
            ps.setInt(6, (u.getRol() != null && u.getRol().getId() > 0) ? u.getRol().getId() : 3);
            ps.setInt(7, u.getIdUsuario());

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para cambiar el estado (ACTIVO / INACTIVO) de un usuario
    public boolean cambiarEstado(int idUsuario, boolean activo) {
        String sql = "UPDATE usuario SET estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, activo ? "ACTIVO" : "INACTIVO");
            ps.setInt(2, idUsuario);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
