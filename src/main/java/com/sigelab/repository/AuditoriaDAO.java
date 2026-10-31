package com.sigelab.repository;

import com.sigelab.model.AuditoriaMovimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public List<AuditoriaMovimiento> obtenerHistorialCompleto() {

        List<AuditoriaMovimiento> lista = new ArrayList<>();

        String sql = """
                SELECT
                    a.id_auditoria,
                    a.fecha,
                    u.nombre + ' ' + u.apellido AS usuario,
                    a.accion,
                    a.tabla_afectada,
                    a.id_registro,
                    a.descripcion,

                    CASE
                        WHEN a.tabla_afectada = 'Prestamo'
                            THEN (
                                SELECT STRING_AGG(
                                    CAST(e.codigo_inventario AS NVARCHAR(MAX)),
                                    ', '
                                )
                                FROM DetallePrestamo dp
                                INNER JOIN Equipo e
                                    ON dp.id_equipo = e.id_equipo
                                WHERE dp.id_prestamo = a.id_registro
                            )

                        WHEN a.tabla_afectada = 'Devolucion'
                            THEN (
                                SELECT e.codigo_inventario
                                FROM Devolucion d
                                INNER JOIN DetallePrestamo dp
                                    ON d.id_detalle = dp.id_detalle
                                INNER JOIN Equipo e
                                    ON dp.id_equipo = e.id_equipo
                                WHERE d.id_devolucion = a.id_registro
                            )

                        ELSE NULL
                    END AS codigo_inventario,

                    CASE
                        WHEN a.tabla_afectada = 'Prestamo'
                            THEN (
                                SELECT STRING_AGG(
                                    CAST(e.nombre AS NVARCHAR(MAX)),
                                    ', '
                                )
                                FROM DetallePrestamo dp
                                INNER JOIN Equipo e
                                    ON dp.id_equipo = e.id_equipo
                                WHERE dp.id_prestamo = a.id_registro
                            )

                        WHEN a.tabla_afectada = 'Devolucion'
                            THEN (
                                SELECT e.nombre
                                FROM Devolucion d
                                INNER JOIN DetallePrestamo dp
                                    ON d.id_detalle = dp.id_detalle
                                INNER JOIN Equipo e
                                    ON dp.id_equipo = e.id_equipo
                                WHERE d.id_devolucion = a.id_registro
                            )

                        ELSE NULL
                    END AS nombre_equipo

                FROM Auditoria a
                INNER JOIN usuario u
                    ON a.id_usuario = u.id

                ORDER BY a.fecha DESC
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                AuditoriaMovimiento mov = new AuditoriaMovimiento();

                mov.setId(rs.getInt("id_auditoria"));
                mov.setFecha(rs.getTimestamp("fecha"));
                mov.setUsuarioResponsable(rs.getString("usuario"));
                mov.setTipoMovimiento(rs.getString("accion"));

                mov.setCodigoInventario(
                        rs.getString("codigo_inventario")
                );

                mov.setNombreEquipo(
                        rs.getString("nombre_equipo")
                );

                mov.setDetalle(
                        "Entidad: " + rs.getString("tabla_afectada")
                                + " | ID Afectado: " + rs.getInt("id_registro")
                                + " | " + rs.getString("descripcion")
                );

                lista.add(mov);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al obtener la auditoría: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return lista;
    }
}