package com.sigelab.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // 1. Préstamos entre fechas (Usa BETWEEN, JOIN, ORDER BY)
    public List<Object[]> obtenerPrestamosEntreFechas(Date inicio, Date fin) {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_prestamo, p.solicitante, p.fecha_prestamo, p.fecha_devolucion_esperada, p.estado, u.nombre + ' ' + u.apellido
            FROM Prestamo p
            INNER JOIN usuario u ON p.id_usuario = u.id
            WHERE p.fecha_prestamo BETWEEN ? AND ?
            ORDER BY p.fecha_prestamo DESC
            """;
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, inicio);
            ps.setDate(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getString(2), rs.getTimestamp(3),
                            rs.getTimestamp(4), rs.getString(5), rs.getString(6)
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 2. Historial de un equipo (Usa JOIN, WHERE, ORDER BY)
    public List<Object[]> obtenerHistorialEquipo(int idEquipo) {
        List<Object[]> lista = new ArrayList<>();
        // Historial de préstamos y devoluciones del equipo
        String sql = """
            SELECT p.id_prestamo, p.solicitante, p.fecha_prestamo, d.fecha_devolucion, d.condicion
            FROM DetallePrestamo dp
            INNER JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo
            LEFT JOIN Devolucion d ON dp.id_detalle = d.id_detalle
            WHERE dp.id_equipo = ?
            ORDER BY p.fecha_prestamo DESC
            """;
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getString(2), rs.getTimestamp(3),
                            rs.getTimestamp(4), rs.getString(5)
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 3. Historial de mantenimiento (Usa JOIN, ORDER BY)
    public List<Object[]> obtenerHistorialMantenimiento() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT m.id_mantenimiento, e.nombre, m.fecha_ingreso, m.tipo, m.estado, m.resultado
            FROM Mantenimiento m
            INNER JOIN Equipo e ON m.id_equipo = e.id_equipo
            ORDER BY m.fecha_ingreso DESC
            """;
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getTimestamp(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}