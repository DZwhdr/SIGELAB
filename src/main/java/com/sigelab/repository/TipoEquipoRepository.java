package org.example.repository;

import org.example.config.ConexionBD;
import org.example.model.TipoEquipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoRepository {

    public List<TipoEquipo> listar() throws SQLException {
        List<TipoEquipo> tipos = new ArrayList<>();

        String sql = """
                SELECT id, nombre
                FROM dbo.TipoEquipo
                ORDER BY nombre
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                TipoEquipo tipo = new TipoEquipo(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                );

                tipos.add(tipo);
            }
        }

        return tipos;
    }

    public TipoEquipo buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, nombre
                FROM dbo.TipoEquipo
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return new TipoEquipo(
                            resultado.getInt("id"),
                            resultado.getString("nombre")
                    );
                }
            }
        }

        return null;
    }
}