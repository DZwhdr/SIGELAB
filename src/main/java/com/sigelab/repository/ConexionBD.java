package com.sigelab.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clase encargada de gestionar y proveer la conexión centralizada a la base de datos SQL Server mediante JDBC
public class ConexionBD {

    // Parámetros de conexión a la base de datos (puedes ajustar el nombre de tu base de datos, usuario y contraseña según tu entorno local)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=SIGELAB;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; // Usuario por defecto de SQL Server (cámbialo si usas otro)
    private static final String PASSWORD = "Sigelab2026!"; // Tu contraseña de SQL Server

    // Método estático para obtener una nueva conexión activa hacia la base de datos
    public static Connection obtenerConexion() {
        try {
            // Intentar establecer la conexión utilizando el driver JDBC de SQL Server
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión a SQL Server exitosa!");
            return conexion;
        } catch (SQLException e) {
            // Manejo de errores en caso de fallo de conexión (credenciales incorrectas, servidor apagado, etc.)
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return null;
        }
    }
}