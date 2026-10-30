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
                a.descripcion
            FROM Auditoria a
            INNER JOIN usuario u ON a.id_usuario = u.id
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
                mov.setDetalle("Entidad: " + rs.getString("tabla_afectada") +
                        " | ID Afectado: " + rs.getInt("id_registro") +
                        " | " + rs.getString("descripcion"));
                lista.add(mov);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la auditoría: " + e.getMessage());
        }
        return lista;
    }
}