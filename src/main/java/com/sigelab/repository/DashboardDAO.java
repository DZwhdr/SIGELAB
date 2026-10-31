package com.sigelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int obtenerCantidadEquiposPorEstado(String estado) {
        String sql = "SELECT COUNT(*) AS total FROM Equipo WHERE estado = ? AND activo = 1";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar equipos por estado: " + e.getMessage());
        }
        return 0;
    }

    public int obtenerTotalEquipos() {
        String sql = "SELECT COUNT(*) AS total FROM Equipo WHERE activo = 1";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("Error al contar total equipos: " + e.getMessage());
        }
        return 0;
    }

    public int obtenerPrestamosActivos() {
        String sql = "SELECT COUNT(*) AS total FROM Prestamo WHERE estado = 'ACTIVO'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("Error al contar préstamos activos: " + e.getMessage());
        }
        return 0;
    }

    public int obtenerPrestamosVencidos() {
        String sql = "SELECT COUNT(*) AS total FROM Prestamo WHERE estado = 'ACTIVO' AND fecha_devolucion_esperada < GETDATE()";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("Error al contar préstamos vencidos: " + e.getMessage());
        }
        return 0;
    }
}