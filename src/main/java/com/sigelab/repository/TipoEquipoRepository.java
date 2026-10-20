
package org.example.repository;

import com.sigelab.repository.ConexionBD;
import org.example.model.TipoEquipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoRepository {

    public boolean guardar(TipoEquipo tipo) throws SQLException {
        String sql = """
                INSERT INTO dbo.TipoEquipo (nombre, descripcion, activo)
                VALUES (?, ?, ?)
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.setBoolean(3, tipo.isActivo());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        tipo.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    public boolean actualizar(TipoEquipo tipo) throws SQLException {
        String sql = """
                UPDATE dbo.TipoEquipo
                SET nombre = ?, descripcion = ?, activo = ?
                WHERE id_tipo = ?
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.setBoolean(3, tipo.isActivo());
            ps.setInt(4, tipo.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarLogico(int idTipo) throws SQLException {
        String sql = """
                UPDATE dbo.TipoEquipo
                SET activo = 0
                WHERE id_tipo = ?
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            return ps.executeUpdate() > 0;
        }
    }

    // Método original del proyecto compartido
    public List<TipoEquipo> listar() throws SQLException {
        List<TipoEquipo> lista = new ArrayList<>();

        String sql = """
                SELECT id_tipo, nombre, descripcion, activo
                FROM dbo.TipoEquipo
                ORDER BY nombre
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirTipo(rs));
            }
        }

        return lista;
    }

    // Lista únicamente los tipos activos
    public List<TipoEquipo> listarTodos() throws SQLException {
        List<TipoEquipo> lista = new ArrayList<>();

        String sql = """
                SELECT id_tipo, nombre, descripcion, activo
                FROM dbo.TipoEquipo
                WHERE activo = 1
                ORDER BY nombre
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirTipo(rs));
            }
        }

        return lista;
    }

    // Método original del proyecto compartido
    public TipoEquipo buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id_tipo, nombre, descripcion, activo
                FROM dbo.TipoEquipo
                WHERE id_tipo = ?
                """;

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirTipo(rs);
                }
            }
        }

        return null;
    }

    public boolean existeNombre(String nombre, Integer idExcluir)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM dbo.TipoEquipo
                WHERE nombre = ?
                """ + (idExcluir != null ? " AND id_tipo <> ?" : "");

        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private TipoEquipo convertirTipo(ResultSet rs)
            throws SQLException {

        return new TipoEquipo(
                rs.getInt("id_tipo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo")
        );
    }

    private Connection conectar() throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        if (conexion == null) {
            throw new SQLException(
                    "No se pudo establecer conexión con SQL Server."
            );
        }

        return conexion;
    }
}
