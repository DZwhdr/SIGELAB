package com.sigelab.repository;

import com.sigelab.model.TipoEquipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoRepository {

    // Detecta el nombre de la columna ID.
    private String obtenerColumnaId(Connection con)
            throws SQLException {

        DatabaseMetaData meta = con.getMetaData();

        try (ResultSet rs = meta.getColumns(
                con.getCatalog(),
                "dbo",
                "TipoEquipo",
                null
        )) {

            while (rs.next()) {
                String columna = rs.getString("COLUMN_NAME");

                if ("id_tipo".equalsIgnoreCase(columna)) {
                    return "id_tipo";
                }

                if ("id".equalsIgnoreCase(columna)) {
                    return "id";
                }
            }
        }

        throw new SQLException(
                "No se encontró la columna ID en dbo.TipoEquipo."
        );
    }

    // Comprueba que las columnas necesarias existan.
    private void verificarColumnas(Connection con)
            throws SQLException {

        boolean descripcion = false;
        boolean activo = false;

        DatabaseMetaData meta = con.getMetaData();

        try (ResultSet rs = meta.getColumns(
                con.getCatalog(),
                "dbo",
                "TipoEquipo",
                null
        )) {

            while (rs.next()) {

                String columna = rs.getString("COLUMN_NAME");

                if ("descripcion".equalsIgnoreCase(columna)) {
                    descripcion = true;
                }

                if ("activo".equalsIgnoreCase(columna)) {
                    activo = true;
                }
            }
        }

        if (!descripcion || !activo) {
            throw new SQLException(
                    "La tabla TipoEquipo necesita las columnas "
                            + "'descripcion' y 'activo' para conservar "
                            + "todas las funciones del módulo."
            );
        }
    }

    // Registrar un tipo de equipo.
    public boolean guardar(TipoEquipo tipo)
            throws SQLException {

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String sql = """
                    INSERT INTO dbo.TipoEquipo
                        (nombre, descripcion, activo)
                    VALUES (?, ?, ?)
                    """;

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 sql,
                                 Statement.RETURN_GENERATED_KEYS
                         )) {

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
        }

        return false;
    }

    // Actualizar nombre, descripción y estado.
    public boolean actualizar(TipoEquipo tipo)
            throws SQLException {

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String columnaId = obtenerColumnaId(con);

            String sql = """
                    UPDATE dbo.TipoEquipo
                    SET nombre = ?,
                        descripcion = ?,
                        activo = ?
                    WHERE
                    """ + columnaId + " = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, tipo.getNombre());
                ps.setString(2, tipo.getDescripcion());
                ps.setBoolean(3, tipo.isActivo());
                ps.setInt(4, tipo.getId());

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Dar de baja sin borrar el registro.
    public boolean eliminarLogico(int idTipo)
            throws SQLException {

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String columnaId = obtenerColumnaId(con);

            String sql = """
                    UPDATE dbo.TipoEquipo
                    SET activo = 0
                    WHERE
                    """ + columnaId + " = ? AND activo = 1";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, idTipo);

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Listar todos los tipos, activos e inactivos.
    public List<TipoEquipo> listar()
            throws SQLException {

        List<TipoEquipo> lista = new ArrayList<>();

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String columnaId = obtenerColumnaId(con);

            String sql = "SELECT "
                    + columnaId
                    + " AS id_tipo, nombre, descripcion, activo "
                    + "FROM dbo.TipoEquipo "
                    + "ORDER BY nombre";

            try (PreparedStatement ps =
                         con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(convertirTipo(rs));
                }
            }
        }

        return lista;
    }

    // Listar únicamente los tipos activos.
    public List<TipoEquipo> listarTodos()
            throws SQLException {

        List<TipoEquipo> lista = new ArrayList<>();

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String columnaId = obtenerColumnaId(con);

            String sql = "SELECT "
                    + columnaId
                    + " AS id_tipo, nombre, descripcion, activo "
                    + "FROM dbo.TipoEquipo "
                    + "WHERE activo = 1 "
                    + "ORDER BY nombre";

            try (PreparedStatement ps =
                         con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(convertirTipo(rs));
                }
            }
        }

        return lista;
    }

    // Buscar un tipo por su identificador.
    public TipoEquipo buscarPorId(int id)
            throws SQLException {

        try (Connection con = conectar()) {

            verificarColumnas(con);

            String columnaId = obtenerColumnaId(con);

            String sql = "SELECT "
                    + columnaId
                    + " AS id_tipo, nombre, descripcion, activo "
                    + "FROM dbo.TipoEquipo "
                    + "WHERE " + columnaId + " = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return convertirTipo(rs);
                    }
                }
            }
        }

        return null;
    }

    // Comprobar si ya existe un nombre.
    public boolean existeNombre(
            String nombre,
            Integer idExcluir
    ) throws SQLException {

        try (Connection con = conectar()) {

            String columnaId = obtenerColumnaId(con);

            String sql = """
                    SELECT COUNT(*)
                    FROM dbo.TipoEquipo
                    WHERE nombre = ?
                    """;

            if (idExcluir != null) {
                sql += " AND " + columnaId + " <> ?";
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, nombre);

                if (idExcluir != null) {
                    ps.setInt(2, idExcluir);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }
    }

    // Convertir los resultados SQL a TipoEquipo.
    private TipoEquipo convertirTipo(ResultSet rs)
            throws SQLException {

        return new TipoEquipo(
                rs.getInt("id_tipo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo")
        );
    }

    // Obtener conexión a SQL Server.
    private Connection conectar()
            throws SQLException {

        Connection conexion =
                ConexionBD.obtenerConexion();

        if (conexion == null) {
            throw new SQLException(
                    "No se pudo establecer conexión con SQL Server."
            );
        }

        return conexion;
    }
}
