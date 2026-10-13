package org.example.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {
        try (
                Connection conexion = ConexionBD.conectar();
                Statement consulta = conexion.createStatement();
                ResultSet resultado = consulta.executeQuery(
                        "SELECT DB_NAME() AS base_actual"
                )
        ) {
            if (resultado.next()) {
                System.out.println("Conexión exitosa.");
                System.out.println(
                        "Base de datos: "
                                + resultado.getString("base_actual")
                );
            }
        } catch (SQLException error) {
            System.err.println(
                    "No se pudo conectar: " + error.getMessage()
            );
        }
    }
}