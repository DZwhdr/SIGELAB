
package com.sigelab.repository;

import com.sigelab.repository.ConexionBD;
import com.sigelab.model.Laboratorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LaboratorioRepository {

    // Guardar un laboratorio nuevo.
    public void guardar(Laboratorio laboratorio) throws SQLException {

        String sql = """
                INSERT INTO dbo.Laboratorio
                    (codigo, nombre, ubicacion, capacidad, estado)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            colocarDatos(consulta, laboratorio);

            if (consulta.executeUpdate() != 1) {
                throw new SQLException("No se guardó el laboratorio.");
            }

            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    laboratorio.setId(claves.getInt(1));
                }
            }
        }
    }

    // Listar todos los laboratorios.
    public List<Laboratorio> listar() throws SQLException {

        List<Laboratorio> laboratorios = new ArrayList<>();

        String sql = """
                SELECT id, codigo, nombre, ubicacion, capacidad, estado
                FROM dbo.Laboratorio
                ORDER BY id
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                laboratorios.add(convertirLaboratorio(resultado));
            }
        }

        return laboratorios;
    }

    // Buscar un laboratorio por código.
    public Laboratorio buscarPorCodigo(String codigo)
            throws SQLException {

        String sql = """
                SELECT id, codigo, nombre, ubicacion, capacidad, estado
                FROM dbo.Laboratorio
                WHERE codigo = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, codigo);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return convertirLaboratorio(resultado);
                }
            }
        }

        return null;
    }

    // Actualizar un laboratorio existente.
    public boolean actualizar(Laboratorio laboratorio)
            throws SQLException {

        String sql = """
                UPDATE dbo.Laboratorio
                SET codigo = ?,
                    nombre = ?,
                    ubicacion = ?,
                    capacidad = ?,
                    estado = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            colocarDatos(consulta, laboratorio);
            consulta.setInt(6, laboratorio.getId());

            return consulta.executeUpdate() == 1;
        }
    }

    // Asignar los datos a los parámetros SQL.
    private void colocarDatos(
            PreparedStatement consulta,
            Laboratorio laboratorio
    ) throws SQLException {

        consulta.setString(1, laboratorio.getCodigo());
        consulta.setString(2, laboratorio.getNombre());
        consulta.setString(3, laboratorio.getUbicacion());
        consulta.setInt(4, laboratorio.getCapacidad());
        consulta.setString(5, laboratorio.getEstado());
    }

    // Convertir una fila de SQL Server en un objeto Laboratorio.
    private Laboratorio convertirLaboratorio(ResultSet resultado)
            throws SQLException {

        return new Laboratorio(
                resultado.getInt("id"),
                resultado.getString("codigo"),
                resultado.getString("nombre"),
                resultado.getString("ubicacion"),
                resultado.getInt("capacidad"),
                resultado.getString("estado")
        );
    }

    // Eliminar un laboratorio por su ID
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Laboratorio WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        }
    }
}
