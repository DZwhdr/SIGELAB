package com.sigelab.repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    // Comprueba si existe una columna en SQL Server.
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

    // Permite trabajar con las diferentes estructuras.
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

    // Construye la consulta de equipos.
    private String construirConsulta(
            Connection con
    ) throws SQLException {

        String idEquipo = obtenerColumna(
                con, "Equipo", "id_equipo", "id"
        );

        String idTipoEquipo = obtenerColumna(
                con, "Equipo", "id_tipo", "id_tipo_equipo"
        );

        String idTipo = obtenerColumna(
                con, "TipoEquipo", "id_tipo", "id"
        );

        return """
                SELECT
                    e.%s AS id_equipo,
                    e.codigo_inventario,
                    e.nombre,
                    t.nombre AS tipo_nombre,
                    e.estado
                FROM dbo.Equipo e
                INNER JOIN dbo.TipoEquipo t
                    ON e.%s = t.%s
                """.formatted(
                idEquipo,
                idTipoEquipo,
                idTipo
        );
    }

    // Convierte un registro SQL en un objeto.
    private DatosEquipoQR convertirEquipo(
            ResultSet rs
    ) throws SQLException {

        return new DatosEquipoQR(
                rs.getInt("id_equipo"),
                rs.getString("codigo_inventario"),
                rs.getString("nombre"),
                rs.getString("tipo_nombre"),
                rs.getString("estado")
        );
    }

    // Busca un equipo por código de inventario.
    public DatosEquipoQR buscarPorCodigo(
            String codigo
    ) throws SQLException {

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
                    + " WHERE e.codigo_inventario = ?";

            if (existeColumna(con, "Equipo", "activo")) {
                sql += " AND e.activo = 1";
            }

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

    // Lista los equipos activos para el selector.
    public List<DatosEquipoQR> listarEquipos()
            throws SQLException {

        List<DatosEquipoQR> equipos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            String sql = construirConsulta(con);

            if (existeColumna(con, "Equipo", "activo")) {
                sql += " WHERE e.activo = 1";
            }

            sql += " ORDER BY e.nombre, e.codigo_inventario";

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

    // Guarda la imagen QR directamente en SQL Server.
    public void guardarImagenQR(
            String codigoInventario,
            BufferedImage imagen
    ) throws SQLException {

        if (codigoInventario == null
                || codigoInventario.isBlank()
                || imagen == null) {

            throw new IllegalArgumentException(
                    "El código y la imagen QR son obligatorios."
            );
        }

        try (ByteArrayOutputStream salida =
                     new ByteArrayOutputStream()) {

            boolean convertido = ImageIO.write(
                    imagen,
                    "PNG",
                    salida
            );

            if (!convertido) {
                throw new SQLException(
                        "No se pudo convertir el QR a PNG."
                );
            }

            byte[] datos = salida.toByteArray();

            try (Connection con = ConexionBD.obtenerConexion()) {

                if (con == null) {
                    throw new SQLException(
                            "No se pudo conectar con SQL Server."
                    );
                }

                String sql = """
                        UPDATE dbo.Equipo
                        SET imagen_qr = ?
                        WHERE codigo_inventario = ?
                        """;

                if (existeColumna(con, "Equipo", "activo")) {
                    sql += " AND activo = 1";
                }

                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {

                    ps.setBytes(1, datos);
                    ps.setString(
                            2,
                            codigoInventario.trim()
                    );

                    int filas = ps.executeUpdate();

                    if (filas != 1) {
                        throw new SQLException(
                                "No se pudo guardar el QR. "
                                        + "Equipos actualizados: "
                                        + filas
                        );
                    }
                }
            }

        } catch (IOException e) {

            throw new SQLException(
                    "Error al preparar la imagen QR.",
                    e
            );
        }
    }

    // Recupera la imagen QR almacenada en SQL Server.
    public BufferedImage obtenerImagenQR(
            String codigoInventario
    ) throws SQLException {

        if (codigoInventario == null
                || codigoInventario.isBlank()) {

            throw new IllegalArgumentException(
                    "El código de inventario es obligatorio."
            );
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            String sql = """
                    SELECT imagen_qr
                    FROM dbo.Equipo
                    WHERE codigo_inventario = ?
                    """;

            if (existeColumna(con, "Equipo", "activo")) {
                sql += " AND activo = 1";
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(
                        1,
                        codigoInventario.trim()
                );

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        byte[] datos = rs.getBytes(
                                "imagen_qr"
                        );

                        if (datos == null
                                || datos.length == 0) {
                            return null;
                        }

                        try (ByteArrayInputStream entrada =
                                     new ByteArrayInputStream(datos)) {

                            BufferedImage imagen =
                                    ImageIO.read(entrada);

                            if (imagen == null) {
                                throw new SQLException(
                                        "La imagen QR guardada no es válida."
                                );
                            }

                            return imagen;

                        } catch (IOException e) {

                            throw new SQLException(
                                    "Error al leer la imagen QR.",
                                    e
                            );
                        }
                    }
                }
            }
        }

        return null;
    }
}
