package com.sigelab.repository;

import com.sigelab.model.DetallePrestamo;
import com.sigelab.model.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrestamoRepository {

    public int guardarPrestamo(Prestamo prestamo)
            throws SQLException {

        Connection conexion = ConexionBD.obtenerConexion();

        if (conexion == null) {
            throw new SQLException(
                    "No se pudo conectar con SQL Server."
            );
        }

        try (conexion) {

            // Todas las operaciones forman una transacción
            conexion.setAutoCommit(false);

            try {

                // 1. Guardar encabezado del préstamo
                String sqlPrestamo = """
                    INSERT INTO dbo.Prestamo (
                        solicitante,
                        fecha_prestamo,
                        fecha_devolucion_esperada,
                        id_usuario,
                        estado,
                        observaciones
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;

                int idPrestamo;

                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     sqlPrestamo,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    ps.setString(
                            1,
                            prestamo.getSolicitante()
                    );

                    ps.setTimestamp(
                            2,
                            Timestamp.valueOf(
                                    prestamo.getFechaPrestamo()
                            )
                    );

                    ps.setTimestamp(
                            3,
                            Timestamp.valueOf(
                                    prestamo.getFechaDevolucionEsperada()
                            )
                    );

                    ps.setInt(
                            4,
                            prestamo.getIdUsuarioRegistro()
                    );

                    ps.setString(
                            5,
                            prestamo.getEstado()
                    );

                    ps.setString(
                            6,
                            prestamo.getObservaciones()
                    );

                    int filas = ps.executeUpdate();

                    if (filas != 1) {
                        throw new SQLException(
                                "No se pudo guardar el préstamo."
                        );
                    }

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del préstamo."
                            );
                        }

                        idPrestamo = rs.getInt(1);
                    }
                }

                // 2. Preparar detalle del préstamo
                String sqlDetalle = """
                    INSERT INTO dbo.DetallePrestamo (
                        id_prestamo,
                        id_equipo
                    )
                    VALUES (?, ?)
                    """;

                // 3. Cambiar estado únicamente si está disponible
                String sqlActualizarEquipo = """
                    UPDATE dbo.Equipo
                    SET estado = 'PRESTADO'
                    WHERE id_equipo = ?
                      AND estado = 'DISPONIBLE'
                      AND activo = 1
                    """;

                for (DetallePrestamo detalle :
                        prestamo.getDetalles()) {

                    // Comprobar disponibilidad real
                    // y actualizar estado
                    try (PreparedStatement ps =
                                 conexion.prepareStatement(
                                         sqlActualizarEquipo
                                 )) {

                        ps.setInt(
                                1,
                                detalle.getIdEquipo()
                        );

                        int filas = ps.executeUpdate();

                        if (filas != 1) {
                            throw new SQLException(
                                    "El equipo con ID "
                                            + detalle.getIdEquipo()
                                            + " no está disponible "
                                            + "o se encuentra inactivo."
                            );
                        }
                    }

                    // Guardar el equipo en el detalle
                    try (PreparedStatement ps =
                                 conexion.prepareStatement(
                                         sqlDetalle
                                 )) {

                        ps.setInt(1, idPrestamo);

                        ps.setInt(
                                2,
                                detalle.getIdEquipo()
                        );

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "No se pudo guardar el equipo "
                                            + detalle.getIdEquipo()
                                            + " en el préstamo."
                            );
                        }
                    }
                }

                // 4. Registrar auditoría
                String sqlAuditoria = """
                    INSERT INTO dbo.Auditoria (
                        id_usuario,
                        accion,
                        tabla_afectada,
                        id_registro,
                        descripcion
                    )
                    VALUES (?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement ps =
                             conexion.prepareStatement(
                                     sqlAuditoria
                             )) {

                    ps.setInt(
                            1,
                            prestamo.getIdUsuarioRegistro()
                    );

                    ps.setString(
                            2,
                            "REGISTRAR_PRESTAMO"
                    );

                    ps.setString(
                            3,
                            "Prestamo"
                    );

                    ps.setInt(
                            4,
                            idPrestamo
                    );

                    ps.setString(
                            5,
                            "Préstamo registrado para "
                                    + prestamo.getSolicitante()
                                    + " con "
                                    + prestamo.getDetalles().size()
                                    + " equipo(s)."
                    );

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo registrar la auditoría."
                        );
                    }
                }

                // Confirmar todos los cambios
                conexion.commit();

                prestamo.setIdPrestamo(idPrestamo);

                return idPrestamo;

            } catch (SQLException | RuntimeException e) {

                // Deshacer todo si ocurre un error
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    // Consultar préstamos activos con equipos pendientes
    public List<Object[]> listarPrestamosActivos()
            throws SQLException {

        List<Object[]> prestamos = new ArrayList<>();

        String sql = """
            SELECT
                p.id_prestamo,
                p.solicitante,
                p.fecha_prestamo,
                p.fecha_devolucion_esperada,
                COUNT(dp.id_detalle) AS equipos_pendientes
            FROM dbo.Prestamo p
            INNER JOIN dbo.DetallePrestamo dp
                ON p.id_prestamo = dp.id_prestamo
            INNER JOIN dbo.Equipo e
                ON dp.id_equipo = e.id_equipo
            WHERE p.estado = 'ACTIVO'
              AND e.estado = 'PRESTADO'
              AND NOT EXISTS (
                  SELECT 1
                  FROM dbo.Devolucion d
                  WHERE d.id_detalle = dp.id_detalle
              )
            GROUP BY
                p.id_prestamo,
                p.solicitante,
                p.fecha_prestamo,
                p.fecha_devolucion_esperada
            ORDER BY p.fecha_prestamo DESC
            """;

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            if (conexion == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    prestamos.add(new Object[]{
                            rs.getInt("id_prestamo"),
                            rs.getString("solicitante"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp("fecha_devolucion_esperada"),
                            rs.getInt("equipos_pendientes")
                    });
                }
            }
        }

        return prestamos;
    }
}
