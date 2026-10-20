
package org.example.repository;

import com.sigelab.repository.ConexionBD;

import java.sql.Connection;
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

    public DatosEquipoQR buscarPorCodigo(String codigo)
            throws SQLException {

        String sql = """
                SELECT
                    e.id_equipo,
                    e.codigo_inventario,
                    e.nombre,
                    t.nombre AS tipo_nombre,
                    e.estado
                FROM dbo.Equipo e
                INNER JOIN dbo.TipoEquipo t
                    ON e.id_tipo = t.id_tipo
                WHERE e.codigo_inventario = ?
                  AND e.activo = 1
                """;

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            if (conexion == null) {
                throw new SQLException(
                        "No se pudo conectar con SQL Server."
                );
            }

            try (PreparedStatement sentencia =
                         conexion.prepareStatement(sql)) {

                sentencia.setString(1, codigo);

                try (ResultSet resultado =
                             sentencia.executeQuery()) {

                    if (resultado.next()) {
                        return new DatosEquipoQR(
                                resultado.getInt("id_equipo"),
                                resultado.getString("codigo_inventario"),
                                resultado.getString("nombre"),
                                resultado.getString("tipo_nombre"),
                                resultado.getString("estado")
                        );
                    }
                }
            }
        }

        return null;
    }
}
