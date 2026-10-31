package com.sigelab.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // 1. PRÉSTAMOS ENTRE FECHAS
    public List<Object[]> obtenerPrestamosEntreFechas(Date inicio, Date fin) {

        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    p.id_prestamo,
                    p.solicitante,
                    e.codigo_inventario,
                    e.nombre AS equipo,
                    p.fecha_prestamo,
                    p.fecha_devolucion_esperada,
                    p.estado,
                    u.nombre + ' ' + u.apellido AS encargado
                FROM Prestamo p
                INNER JOIN usuario u
                    ON p.id_usuario = u.id
                INNER JOIN DetallePrestamo dp
                    ON dp.id_prestamo = p.id_prestamo
                INNER JOIN Equipo e
                    ON dp.id_equipo = e.id_equipo
                WHERE p.fecha_prestamo >= ?
                  AND p.fecha_prestamo < DATEADD(DAY, 1, ?)
                ORDER BY p.fecha_prestamo DESC
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, inicio);
            ps.setDate(2, fin);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt("id_prestamo"),
                            rs.getString("solicitante"),
                            rs.getString("codigo_inventario"),
                            rs.getString("equipo"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp("fecha_devolucion_esperada"),
                            rs.getString("estado"),
                            rs.getString("encargado")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 2. HISTORIAL DE EQUIPO POR CÓDIGO DE INVENTARIO
    public List<Object[]> obtenerHistorialEquipo(String codigoInventario) {

        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    e.codigo_inventario,
                    e.nombre AS equipo,
                    p.id_prestamo,
                    p.solicitante,
                    p.fecha_prestamo,
                    d.fecha_devolucion,
                    d.condicion
                FROM Equipo e
                INNER JOIN DetallePrestamo dp
                    ON dp.id_equipo = e.id_equipo
                INNER JOIN Prestamo p
                    ON dp.id_prestamo = p.id_prestamo
                LEFT JOIN Devolucion d
                    ON dp.id_detalle = d.id_detalle
                WHERE e.codigo_inventario = ?
                ORDER BY p.fecha_prestamo DESC
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoInventario);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getString("codigo_inventario"),
                            rs.getString("equipo"),
                            rs.getInt("id_prestamo"),
                            rs.getString("solicitante"),
                            rs.getTimestamp("fecha_prestamo"),
                            rs.getTimestamp("fecha_devolucion"),
                            rs.getString("condicion")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 3. HISTORIAL DE MANTENIMIENTO
    public List<Object[]> obtenerHistorialMantenimiento() {

        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    m.id_mantenimiento,
                    e.codigo_inventario,
                    e.nombre AS equipo,
                    m.fecha_ingreso,
                    m.tipo,
                    m.estado,
                    m.resultado
                FROM Mantenimiento m
                INNER JOIN Equipo e
                    ON m.id_equipo = e.id_equipo
                ORDER BY m.fecha_ingreso DESC
                """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_mantenimiento"),
                        rs.getString("codigo_inventario"),
                        rs.getString("equipo"),
                        rs.getTimestamp("fecha_ingreso"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("resultado")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}