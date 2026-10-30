package com.sigelab.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializadorBD {

    public static void crearTablas() {
        String tablaDevolucion =
                "IF OBJECT_ID('dbo.Devolucion', 'U') IS NULL " +
                        "BEGIN " +
                        "CREATE TABLE dbo.Devolucion (" +
                        "id INT IDENTITY(1,1) PRIMARY KEY, " +
                        "equipo_id INT NOT NULL, " +
                        "usuario_id INT NOT NULL, " +
                        "fecha_devolucion DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                        "estado_equipo NVARCHAR(30) NOT NULL, " +
                        "observaciones NVARCHAR(500) NULL, " +
                        "CONSTRAINT FK_Devolucion_Equipo FOREIGN KEY (equipo_id) REFERENCES dbo.Equipo(id), " +
                        "CONSTRAINT FK_Devolucion_Usuario FOREIGN KEY (usuario_id) REFERENCES dbo.Usuario(id)" +
                        "); END;";

        String tablaMantenimiento =
                "IF OBJECT_ID('dbo.Mantenimiento', 'U') IS NULL " +
                        "BEGIN " +
                        "CREATE TABLE dbo.Mantenimiento (" +
                        "id INT IDENTITY(1,1) PRIMARY KEY, " +
                        "equipo_id INT NOT NULL, " +
                        "fecha_inicio DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                        "fecha_fin DATETIME2 NULL, " +
                        "motivo NVARCHAR(250) NOT NULL, " +
                        "estado NVARCHAR(30) NOT NULL DEFAULT 'EN_PROCESO', " +
                        "observaciones NVARCHAR(500) NULL, " +
                        "CONSTRAINT FK_Mantenimiento_Equipo FOREIGN KEY (equipo_id) REFERENCES dbo.Equipo(id)" +
                        "); END;";

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) {
                System.err.println("No se pudo inicializar la base de datos.");
                return;
            }

            try (Statement statement = conexion.createStatement()) {
                statement.executeUpdate(tablaDevolucion);
                statement.executeUpdate(tablaMantenimiento);
                System.out.println("Tablas de Devolución y Mantenimiento verificadas.");
            }
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}