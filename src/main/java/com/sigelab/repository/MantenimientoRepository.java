package com.sigelab.repository;

import com.sigelab.model.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoRepository {

    public boolean guardar(Mantenimiento mantenimiento) throws SQLException {
        String sql = """
                INSERT INTO dbo.Mantenimiento
                (id_equipo, tipo, descripcion, responsable,
                 fecha_salida, resultado, estado, observaciones)
                VALUES (?, 'CORRECTIVO', ?, 'SIGELAB',
                        CASE WHEN ? = 'FINALIZADO' THEN SYSDATETIME() ELSE NULL END,
                        CASE WHEN ? = 'FINALIZADO' THEN 'REPARADO' ELSE NULL END,
                        ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, mantenimiento.getEquipoId());
            statement.setString(2, mantenimiento.getMotivo());
            statement.setString(3, mantenimiento.getEstado());
            statement.setString(4, mantenimiento.getEstado());
            statement.setString(5, mantenimiento.getEstado());
            statement.setString(6, mantenimiento.getObservaciones());

            return statement.executeUpdate() > 0;
        }
    }

    public List<Mantenimiento> listar() throws SQLException {
        List<Mantenimiento> mantenimientos = new ArrayList<>();

        String sql = """
                SELECT id_mantenimiento AS id,
                       id_equipo AS equipo_id,
                       fecha_ingreso AS fecha_inicio,
                       fecha_salida AS fecha_fin,
                       descripcion AS motivo,
                       estado,
                       observaciones
                FROM dbo.Mantenimiento
                ORDER BY id_mantenimiento DESC
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                Timestamp fechaInicio = resultado.getTimestamp("fecha_inicio");
                Timestamp fechaFin = resultado.getTimestamp("fecha_fin");

                Mantenimiento mantenimiento = new Mantenimiento(
                        resultado.getInt("id"),
                        resultado.getInt("equipo_id"),
                        fechaInicio != null
                                ? fechaInicio.toLocalDateTime()
                                : null,
                        fechaFin != null
                                ? fechaFin.toLocalDateTime()
                                : null,
                        resultado.getString("motivo"),
                        resultado.getString("estado"),
                        resultado.getString("observaciones")
                );

                mantenimientos.add(mantenimiento);
            }
        }

        return mantenimientos;
    }

    public boolean actualizar(Mantenimiento mantenimiento) throws SQLException {
        String sql = """
                UPDATE dbo.Mantenimiento
                SET id_equipo = ?,
                    descripcion = ?,
                    estado = ?,
                    observaciones = ?,
                    fecha_salida = CASE
                        WHEN ? = 'FINALIZADO'
                        THEN COALESCE(fecha_salida, SYSDATETIME())
                        ELSE NULL
                    END,
                    resultado = CASE
                        WHEN ? = 'FINALIZADO'
                        THEN COALESCE(resultado, 'REPARADO')
                        ELSE NULL
                    END
                WHERE id_mantenimiento = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, mantenimiento.getEquipoId());
            statement.setString(2, mantenimiento.getMotivo());
            statement.setString(3, mantenimiento.getEstado());
            statement.setString(4, mantenimiento.getObservaciones());
            statement.setString(5, mantenimiento.getEstado());
            statement.setString(6, mantenimiento.getEstado());
            statement.setInt(7, mantenimiento.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Mantenimiento WHERE id_mantenimiento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
