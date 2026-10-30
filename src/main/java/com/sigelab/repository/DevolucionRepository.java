package com.sigelab.repository;

import com.sigelab.model.Devolucion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionRepository {

    public boolean guardar(Devolucion devolucion) throws SQLException {
        String sql = """
                INSERT INTO dbo.Devolucion
                (equipo_id, usuario_id, estado_equipo, observaciones)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, devolucion.getEquipoId());
            statement.setInt(2, devolucion.getUsuarioId());
            statement.setString(3, devolucion.getEstadoEquipo());
            statement.setString(4, devolucion.getObservaciones());

            return statement.executeUpdate() > 0;
        }
    }

    public List<Devolucion> listar() throws SQLException {
        List<Devolucion> devoluciones = new ArrayList<>();

        String sql = """
                SELECT id, equipo_id, usuario_id, fecha_devolucion,
                       estado_equipo, observaciones
                FROM dbo.Devolucion
                ORDER BY id DESC
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                Timestamp fecha = resultado.getTimestamp("fecha_devolucion");

                Devolucion devolucion = new Devolucion(
                        resultado.getInt("id"),
                        resultado.getInt("equipo_id"),
                        resultado.getInt("usuario_id"),
                        fecha != null ? fecha.toLocalDateTime() : null,
                        resultado.getString("estado_equipo"),
                        resultado.getString("observaciones")
                );

                devoluciones.add(devolucion);
            }
        }

        return devoluciones;
    }

    public boolean actualizar(Devolucion devolucion) throws SQLException {
        String sql = """
                UPDATE dbo.Devolucion
                SET equipo_id = ?,
                    usuario_id = ?,
                    estado_equipo = ?,
                    observaciones = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, devolucion.getEquipoId());
            statement.setInt(2, devolucion.getUsuarioId());
            statement.setString(3, devolucion.getEstadoEquipo());
            statement.setString(4, devolucion.getObservaciones());
            statement.setInt(5, devolucion.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Devolucion WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}