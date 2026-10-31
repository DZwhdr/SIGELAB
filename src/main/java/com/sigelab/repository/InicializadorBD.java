package com.sigelab.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InicializadorBD {

    private static final String[] TABLAS_REQUERIDAS = {
            "Equipo",
            "Laboratorio",
            "TipoEquipo",
            "Prestamo",
            "DetallePrestamo",
            "Devolucion",
            "Mantenimiento",
            "usuario",
            "usuario_biometria"
    };

    /**
     * Conserva el nombre usado por Main, pero en Azure solo verifica la
     * estructura. La aplicación no debe crear ni alterar tablas compartidas.
     */
    public static void crearTablas() {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion == null) {
                System.err.println("No se pudo verificar la base de datos.");
                return;
            }

            for (String tabla : TABLAS_REQUERIDAS) {
                if (!existeTabla(conexion, tabla)) {
                    System.err.println("Falta la tabla requerida dbo." + tabla);
                    return;
                }
            }

            System.out.println("Estructura de SIGELAB verificada.");
        } catch (SQLException e) {
            System.err.println(
                    "Error al verificar la estructura: " + e.getMessage()
            );
        }
    }

    private static boolean existeTabla(Connection conexion, String tabla)
            throws SQLException {

        DatabaseMetaData metadata = conexion.getMetaData();

        try (ResultSet resultado = metadata.getTables(
                conexion.getCatalog(),
                "dbo",
                tabla,
                new String[]{"TABLE"}
        )) {
            return resultado.next();
        }
    }
}
