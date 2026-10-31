
package com.sigelab.repository;

import com.sigelab.model.Devolucion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionRepository {

    public boolean guardar(Devolucion devolucion) throws SQLException {
        String sql = """
                INSERT INTO dbo.Devolucion
                (id_detalle, id_usuario, condicion, observaciones)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {
                int idDetalle = buscarDetalleActivo(
                        conexion,
                        devolucion.getEquipoId(),
                        null
                );

                try (PreparedStatement statement =
                             conexion.prepareStatement(sql)) {

                    statement.setInt(1, idDetalle);
                    statement.setInt(2, devolucion.getUsuarioId());
                    statement.setString(3, devolucion.getEstadoEquipo());
                    statement.setString(4, devolucion.getObservaciones());

                    if (statement.executeUpdate() != 1) {
                        conexion.rollback();
                        return false;
                    }
                }

                actualizarEstadoEquipo(
                        conexion,
                        devolucion.getEquipoId(),
                        devolucion.getEstadoEquipo()
                );

                finalizarPrestamoSiCorresponde(conexion, idDetalle);

                conexion.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    public List<Devolucion> listar() throws SQLException {
        List<Devolucion> devoluciones = new ArrayList<>();

        String sql = """
                SELECT d.id_devolucion AS id,
                       dp.id_equipo AS equipo_id,
                       d.id_usuario AS usuario_id,
                       d.fecha_devolucion,
                       d.condicion AS estado_equipo,
                       d.observaciones
                FROM dbo.Devolucion d
                INNER JOIN dbo.DetallePrestamo dp
                    ON d.id_detalle = dp.id_detalle
                ORDER BY d.id_devolucion DESC
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement =
                     conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                Timestamp fecha =
                        resultado.getTimestamp("fecha_devolucion");

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

    public boolean actualizar(Devolucion devolucion)
            throws SQLException {

        String sql = """
                UPDATE dbo.Devolucion
                SET id_detalle = ?,
                    id_usuario = ?,
                    condicion = ?,
                    observaciones = ?
                WHERE id_devolucion = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {
                int idDetalle = buscarDetalleActivo(
                        conexion,
                        devolucion.getEquipoId(),
                        devolucion.getId()
                );

                try (PreparedStatement statement =
                             conexion.prepareStatement(sql)) {

                    statement.setInt(1, idDetalle);
                    statement.setInt(2, devolucion.getUsuarioId());
                    statement.setString(3, devolucion.getEstadoEquipo());
                    statement.setString(4, devolucion.getObservaciones());
                    statement.setInt(5, devolucion.getId());

                    if (statement.executeUpdate() != 1) {
                        conexion.rollback();
                        return false;
                    }
                }

                actualizarEstadoEquipo(
                        conexion,
                        devolucion.getEquipoId(),
                        devolucion.getEstadoEquipo()
                );

                finalizarPrestamoSiCorresponde(conexion, idDetalle);

                conexion.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = """
                DELETE FROM dbo.Devolucion
                WHERE id_devolucion = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void actualizarEstadoEquipo(
            Connection conexion,
            int idEquipo,
            String condicion
    ) throws SQLException {

        String nuevoEstado;

        if ("BUENO".equals(condicion)) {
            nuevoEstado = "DISPONIBLE";
        } else if ("DAÑADO".equals(condicion)) {
            nuevoEstado = "MANTENIMIENTO";
        } else {
            throw new SQLException(
                    "Condición de devolución no válida: " + condicion
            );
        }

        String sql = """
                UPDATE dbo.Equipo
                SET estado = ?
                WHERE id_equipo = ?
                """;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, nuevoEstado);
            statement.setInt(2, idEquipo);

            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "No se pudo actualizar el estado del equipo "
                                + idEquipo
                );
            }
        }
    }

    private int buscarDetalleActivo(
            Connection conexion,
            int idEquipo,
            Integer idDevolucionActual
    ) throws SQLException {

        String sql = """
                SELECT TOP 1 dp.id_detalle
                FROM dbo.DetallePrestamo dp
                INNER JOIN dbo.Prestamo p
                    ON dp.id_prestamo = p.id_prestamo
                LEFT JOIN dbo.Devolucion d
                    ON dp.id_detalle = d.id_detalle
                WHERE dp.id_equipo = ?
                  AND (p.estado = 'ACTIVO'
                       OR d.id_devolucion = ?)
                  AND (d.id_devolucion IS NULL
                       OR d.id_devolucion = ?)
                ORDER BY p.id_prestamo DESC
                """;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, idEquipo);

            if (idDevolucionActual == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, idDevolucionActual);
                statement.setInt(3, idDevolucionActual);
            }

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id_detalle");
                }
            }
        }

        throw new SQLException(
                "No existe un préstamo activo sin devolución "
                        + "para el equipo " + idEquipo + "."
        );
    }

    private void finalizarPrestamoSiCorresponde(
            Connection conexion,
            int idDetalle
    ) throws SQLException {

        String sqlPrestamo = """
                SELECT id_prestamo
                FROM dbo.DetallePrestamo
                WHERE id_detalle = ?
                """;

        int idPrestamo;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sqlPrestamo)) {

            statement.setInt(1, idDetalle);

            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException(
                            "No se encontró el préstamo correspondiente."
                    );
                }

                idPrestamo = resultado.getInt("id_prestamo");
            }
        }

        String sqlPendientes = """
                SELECT COUNT(*) AS pendientes
                FROM dbo.DetallePrestamo dp
                WHERE dp.id_prestamo = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM dbo.Devolucion d
                      WHERE d.id_detalle = dp.id_detalle
                  )
                """;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sqlPendientes)) {

            statement.setInt(1, idPrestamo);

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()
                        && resultado.getInt("pendientes") == 0) {

                    String sqlActualizar = """
                            UPDATE dbo.Prestamo
                            SET estado = 'FINALIZADO'
                            WHERE id_prestamo = ?
                              AND estado = 'ACTIVO'
                            """;

                    try (PreparedStatement actualizar =
                                 conexion.prepareStatement(sqlActualizar)) {

                        actualizar.setInt(1, idPrestamo);
                        actualizar.executeUpdate();
                    }
                }
            }
        }
    }
}
