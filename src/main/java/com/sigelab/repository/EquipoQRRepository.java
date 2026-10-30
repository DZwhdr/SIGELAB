package com.sigelab.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipoQRRepository {

    public static class DatosEquipoQR {

        private final int id;
        private final String codigoInventario;
        private final String nombre;
        private final String tipo;
        private final String estado;

        public DatosEquipoQR(
                int id,
                String codigoInventario,
                String nombre,
                String tipo,
                String estado
        ) {
            this.id = id;
            this.codigoInventario = codigoInventario;
            this.nombre = nombre;
            this.tipo = tipo;
            this.estado = estado;
        }

        public int getId() {
            return id;
        }

        public String getCodigoInventario() {
            return codigoInventario;
        }

        public String getNombre() {
            return nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public String getEstado() {
            return estado;
        }
    }

    // Verifica si existe una columna en una tabla.
    private boolean existeColumna(
            Connection con,
            String tabla,
            String columna
    ) throws SQLException {

        DatabaseMetaData meta = con.getMetaData();

        try (ResultSet rs = meta.getColumns(
                con.getCatalog(),
                "dbo",
                tabla,
                null
        )) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(
                        rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    // Identifica una columna entre dos nombres posibles.
    private String obtenerColumna(
            Connection con,
            String tabla,
            String opcionOriginal,
            String opcionKarla
    ) throws SQLException {

        if (existeColumna(con, tabla, opcionOriginal)) {
            return opcionOriginal;
        }

        if (existeColumna(con, tabla, opcionKarla)) {
            return opcionKarla;
        }

        throw new SQLException(
                "No se encontró una columna compatible en dbo."
                        + tabla + ": " + opcionOriginal
                        + " o " + opcionKarla
        );
    }

    // Consulta reutilizable para buscar equipos.
    private String construirConsulta(Connection con)
            throws SQLException {

        String idEquipo = obtenerColumna(
                con, "Equipo", "id_equipo", "id"
        );

        String idTipoEquipo = obtenerColumna(
                con, "Equipo", "id_tipo", "id_tipo_equipo"
        );

        String idTipo = obtenerColumna(
                con, "TipoEquipo", "id_tipo", "id"
        );

        boolean tieneActivo = existeColumna(
                con, "Equipo", "activo"
        );

        String sql =
                "SELECT " +
                        "e." + idEquipo + " AS id_equipo, " +
                        "e.codigo_inventario, " +
                        "e.nombre, " +
                        "t.nombre AS tipo_nombre, " +
                        "e.estado " +
                        "FROM dbo.Equipo e " +
                        "INNER JOIN dbo.TipoEquipo t " +
                        "ON e." + idTipoEquipo + " = t." + idTipo +
                        " WHERE 1 = 1";

        if (tieneActivo) {
            sql += " AND e.activo = 1";
        }

        return sql;
    }

    // Convierte un resultado SQL en un objeto DatosEquipoQR.
    private DatosEquipoQR convertirEquipo(ResultSet rs)
            throws SQLException {

        return new DatosEquipoQR(
                rs.getInt("id_equipo"),
                rs.getString("codigo_inventario"),
                rs.getString("nombre"),
                rs.getString("tipo_nombre"),
                rs.getString("estado")
        );
    }

    // Buscar un equipo por su código exacto.
    public DatosEquipoQR buscarPorCodigo(String codigo)
            throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de inventario está vacío."
            );
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            String sql = construirConsulta(con)
                    + " AND e.codigo_inventario = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, codigo.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return convertirEquipo(rs);
                    }
                }
            }
        }

        return null;
    }

    // Buscar equipos por parte del nombre o código.
    public List<DatosEquipoQR> buscarEquipos(String texto)
            throws SQLException {

        List<DatosEquipoQR> equipos = new ArrayList<>();

        if (texto == null || texto.isBlank()) {
            return equipos;
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            String sql = construirConsulta(con)
                    + " AND (e.nombre LIKE ? "
                    + "OR e.codigo_inventario LIKE ?) "
                    + "ORDER BY e.nombre, e.codigo_inventario";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                String busqueda = "%"
                        + texto.trim()
                        .replace("[", "[[]")
                        .replace("%", "[%]")
                        .replace("_", "[_]")
                        + "%";

                ps.setString(1, busqueda);
                ps.setString(2, busqueda);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        equipos.add(convertirEquipo(rs));
                    }
                }
            }
        }

        return equipos;
    }

    // Mostrar todos los equipos activos registrados.
    public List<DatosEquipoQR> listarEquipos()
            throws SQLException {

        List<DatosEquipoQR> equipos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            String sql = construirConsulta(con)
                    + " ORDER BY e.nombre, e.codigo_inventario";

            try (PreparedStatement ps =
                         con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    equipos.add(convertirEquipo(rs));
                }
            }
        }

        return equipos;
    }

    // Guardar el QR de un equipo sin duplicar registros.
    public void guardarQR(int idEquipo, String contenidoQR)
            throws SQLException {

        if (idEquipo <= 0) {
            throw new IllegalArgumentException(
                    "El ID del equipo no es válido."
            );
        }

        if (contenidoQR == null || contenidoQR.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del QR está vacío."
            );
        }

        if (contenidoQR.length() > 255) {
            throw new IllegalArgumentException(
                    "El contenido del QR supera los 255 caracteres."
            );
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            // Insertar si no existe; actualizar si ya está registrado.
            String sql =
                    "UPDATE dbo.EquipoQR WITH (UPDLOCK, SERIALIZABLE) " +
                            "SET contenido_qr = ? " +
                            "WHERE id_equipo = ?; " +
                            "IF @@ROWCOUNT = 0 " +
                            "INSERT INTO dbo.EquipoQR " +
                            "(id_equipo, contenido_qr) " +
                            "VALUES (?, ?);";

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {

                    ps.setString(1, contenidoQR);
                    ps.setInt(2, idEquipo);
                    ps.setInt(3, idEquipo);
                    ps.setString(4, contenidoQR);

                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    // Consultar el contenido QR registrado de un equipo.
    public String obtenerQRRegistrado(int idEquipo)
            throws SQLException {

        String sql =
                "SELECT contenido_qr " +
                        "FROM dbo.EquipoQR " +
                        "WHERE id_equipo = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, idEquipo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return rs.getString("contenido_qr");
                    }
                }
            }
        }

        return null;
    }
}
