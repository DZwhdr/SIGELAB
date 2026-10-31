package com.sigelab.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConsultaIds {

    public static void main(String[] args) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement statement = conexion.createStatement()) {

            try (ResultSet resultado =
                         statement.executeQuery("SELECT TOP 5 id_equipo FROM dbo.Equipo")) {

                System.out.println("ID DE EQUIPOS:");

                while (resultado.next()) {
                    System.out.println(resultado.getInt("id_equipo"));
                }
            }

            try (ResultSet resultado =
                         statement.executeQuery("SELECT TOP 5 id FROM dbo.Usuario")) {

                System.out.println("ID DE USUARIOS:");

                while (resultado.next()) {
                    System.out.println(resultado.getInt("id"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
