package com.sigelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AuditoriaService {

    public static final String LOGIN_EXITOSO = "LOGIN_EXITOSO";
    public static final String LOGIN_FALLIDO = "LOGIN_FALLIDO";
    public static final String PRESTAMO_CREADO = "PRESTAMO_CREADO";
    public static final String EQUIPO_DEVUELTO = "EQUIPO_DEVUELTO";
    public static final String EQUIPO_MANTENIMIENTO = "EQUIPO_ENVIADO_MANTENIMIENTO";
    public static final String MANTENIMIENTO_FINALIZADO = "MANTENIMIENTO_FINALIZADO";
    public static final String USUARIO_DESACTIVADO = "USUARIO_DESACTIVADO";

    public static void registrar(int idUsuario, String accion, String tablaAfectada, int idRegistro, String descripcion) {
        // Validación para evitar error de FK si el idUsuario es 0 o inválido
        int idUserFinal = (idUsuario > 0) ? idUsuario : 1;

        String sql = "INSERT INTO Auditoria (id_usuario, accion, tabla_afectada, id_registro, descripcion, fecha) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUserFinal);
            ps.setString(2, accion);
            ps.setString(3, tablaAfectada);
            ps.setInt(4, idRegistro);
            ps.setString(5, descripcion);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
}