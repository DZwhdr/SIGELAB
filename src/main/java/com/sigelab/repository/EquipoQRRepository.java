
package com.sigelab.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // Buscar un equipo utilizando su código de inventario.
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

            // Compatibilidad con las dos estructuras.
            String idEquipo = obtenerColumna(
                    con, "Equipo", "id_equipo", "id"
            );

            String idTipoEquipo = obtenerColumna(
                    con, "Equipo", "id_tipo", "id_tipo_equipo"
            );

            String idTipo = obtenerColumna(
                    con, "TipoEquipo", "id_tipo", "id"
            );

            // Algunas estructuras incluyen baja lógica.
            boolean tieneActivo = existeColumna(
                    con, "Equipo", "activo"
            );

            String sql = """
                    SELECT
                        e.%s AS id_equipo,
                        e.codigo_inventario,
                        e.nombre,
                        t.nombre AS tipo_nombre,
                        e.estado
                    FROM dbo.Equipo e
                    INNER JOIN dbo.TipoEquipo t
                        ON e.%s = t.%s
                    WHERE e.codigo_inventario = ?
                    """.formatted(
                    idEquipo,
                    idTipoEquipo,
                    idTipo
            );

            if (tieneActivo) {
                sql += " AND e.activo = 1";
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, codigo.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new DatosEquipoQR(
                                rs.getInt("id_equipo"),
                                rs.getString("codigo_inventario"),
                                rs.getString("nombre"),
                                rs.getString("tipo_nombre"),
                                rs.getString("estado")
                        );
                    }
                }
            }
        }

        return null;
    }
}
