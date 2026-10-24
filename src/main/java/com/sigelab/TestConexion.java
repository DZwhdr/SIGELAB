package com.sigelab; // O el paquete donde prefieras colocar tu prueba

import com.sigelab.repository.ConexionBD;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión...");

        // Llamamos al método que creaste en ConexionBD
        Connection conexion = ConexionBD.obtenerConexion();

        if (conexion != null) {
            System.out.println("¡Prueba exitosa! La base de datos SIGELAB está respondiendo a Java.");
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
package com.sigelab; // O el paquete donde prefieras colocar tu prueba

import com.sigelab.repository.ConexionBD;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión...");

        // Llamamos al método que creaste en ConexionBD
        Connection conexion = ConexionBD.obtenerConexion();

        if (conexion != null) {
            System.out.println("¡Prueba exitosa! La base de datos SIGELAB está respondiendo a Java.");
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
}