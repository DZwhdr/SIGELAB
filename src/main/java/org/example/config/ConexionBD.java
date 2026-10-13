package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:sqlserver://127.0.0.1:1435;"
                    + "databaseName=SIGELAB_KarlaDB;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=10;";

    public static Connection conectar() throws SQLException {
        String usuario = "sa";
        String clave = System.getenv("SIGELAB_DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new SQLException(
                    "Falta configurar SIGELAB_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, usuario, clave);
    }
}