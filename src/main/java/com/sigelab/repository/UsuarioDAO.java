package com.sigelab.repository;

import com.sigelab.model.Rol;
import com.sigelab.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Traigo todos los usuarios de la base de datos incluyendo el flag de primer login
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT u.id, u.username, u.password, u.nombre, u.apellido, u.correo, u.estado, u.rol_id, u.primer_login " +
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

                // Mapeo el estado VARCHAR ("ACTIVO" / "INACTIVO") a boolean
                String estadoStr = rs.getString("estado");
                u.setActivo(estadoStr != null && estadoStr.equalsIgnoreCase("ACTIVO"));

                // Leo si es su primer login obligatorio
                u.setPrimerLogin(rs.getBoolean("primer_login"));

                Rol r = new Rol();
                int idRol = rs.getInt("rol_id");
                r.setId(idRol);

                // Asigno el nombre del rol según el ID
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

    // Inserto un usuario nuevo y le pongo primer_login en true por defecto
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (username, password, nombre, apellido, correo, estado, rol_id, primer_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername().trim());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellido());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.isActivo() ? "ACTIVO" : "INACTIVO");
            ps.setInt(7, (u.getRol() != null && u.getRol().getId() > 0) ? u.getRol().getId() : 3);
            ps.setInt(8, 1); // Nuevo usuario creado por admin inicia con primer_login obligatorio (true)

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizo los datos generales del usuario
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

    // Cambio el estado a ACTIVO o INACTIVO
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

    // Actualizo la contraseña y de una vez pongo primer_login en false para quitar el bloqueo de primer ingreso
    public boolean actualizarPassword(String username, String nuevaPassword) {
        String sql = "UPDATE usuario SET password = ?, primer_login = 0 WHERE username = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaPassword);
            ps.setString(2, username.trim());

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la contraseña en UsuarioDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}