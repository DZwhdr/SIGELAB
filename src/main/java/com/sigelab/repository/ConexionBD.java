package com.sigelab.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST = obtenerValor(
            "SIGELAB_DB_HOST",
            "localhost"
    );

    private static final String PORT = obtenerValor(
            "SIGELAB_DB_PORT",
            "1433"
    );

    private static final String DATABASE = obtenerValor(
            "SIGELAB_DB_NAME",
            "SIGELAB"
    );

    private static final String USER = obtenerValor(
            "SIGELAB_DB_USER",
            "sa"
    );

    private static final String PASSWORD = obtenerValor(
            "SIGELAB_DB_PASSWORD",
            ""
    );

    private static final String URL =
            "jdbc:sqlserver://" + HOST + ":" + PORT + ";" +
                    "databaseName=" + DATABASE + ";" +
                    "encrypt=true;" +
                    "trustServerCertificate=true;" +
                    "loginTimeout=10;";

    private static String obtenerValor(
            String nombre,
            String valorPredeterminado
    ) {
        String valor = System.getenv(nombre);

        if (valor == null || valor.isBlank()) {
            return valorPredeterminado;
        }

        return valor;
    }

    public static Connection obtenerConexion() {
        try {
            Connection conexion = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println(
                    "¡Conexión a SQL Server exitosa!"
            );

            return conexion;
        } catch (SQLException e) {
            System.err.println(
                    "Error al conectar con la base de datos: "
                            + e.getMessage()
            );

            return null;
        }
    }
}