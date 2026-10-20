
package org.example.repository;

import com.sigelab.repository.ConexionBD;
import org.example.enums.EstadoEquipo;
import org.example.model.Equipo;
import org.example.model.Laboratorio;
import org.example.model.TipoEquipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoRepository {

    // Une los datos del equipo con su tipo y laboratorio.
    private static final String CONSULTA_BASE = """
            SELECT e.id, e.codigo_inventario, e.nombre,
                   e.marca, e.modelo, e.serie,
                   e.estado, e.observaciones,
                   t.id AS tipo_id, t.nombre AS tipo_nombre,
                   l.id AS laboratorio_id,
                   l.codigo AS laboratorio_codigo,
                   l.nombre AS laboratorio_nombre,
                   l.ubicacion AS laboratorio_ubicacion,
                   l.capacidad AS laboratorio_capacidad,
                   l.estado AS laboratorio_estado
            FROM dbo.Equipo e
            INNER JOIN dbo.TipoEquipo t
                ON e.id_tipo_equipo = t.id
            INNER JOIN dbo.Laboratorio l
                ON e.id_laboratorio = l.id
            """;

    public void guardar(Equipo equipo) throws SQLException {
        String sql = """
                INSERT INTO dbo.Equipo
                    (codigo_inventario, nombre, id_tipo_equipo,
                     marca, modelo, serie, id_laboratorio,
                     estado, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            colocarDatos(sentencia, equipo);

            if (sentencia.executeUpdate() != 1) {
                throw new SQLException("No se guardó el equipo.");
            }

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    equipo.setId(claves.getInt(1));
                }
            }
        }
    }

    public List<Equipo> listar() throws SQLException {
        List<Equipo> equipos = new ArrayList<>();
        String sql = CONSULTA_BASE + " ORDER BY e.id";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                equipos.add(convertirEquipo(resultado));
            }
        }

        return equipos;
    }

    public Equipo buscarPorCodigo(String codigo) throws SQLException {
        String sql = CONSULTA_BASE + " WHERE e.codigo_inventario = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigo);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return convertirEquipo(resultado);
                }
            }
        }

        return null;
    }

    public boolean actualizar(Equipo equipo) throws SQLException {
        String sql = """
                UPDATE dbo.Equipo
                SET codigo_inventario = ?,
                    nombre = ?,
                    id_tipo_equipo = ?,
                    marca = ?,
                    modelo = ?,
                    serie = ?,
                    id_laboratorio = ?,
                    estado = ?,
                    observaciones = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            colocarDatos(sentencia, equipo);
            sentencia.setInt(10, equipo.getId());

            return sentencia.executeUpdate() == 1;
        }
    }

    // Coloca cada dato en el signo ? correspondiente.
    private void colocarDatos(
            PreparedStatement sentencia,
            Equipo equipo
    ) throws SQLException {

        sentencia.setString(1, equipo.getCodigoInventario());
        sentencia.setString(2, equipo.getNombre());
        sentencia.setInt(3, equipo.getTipoEquipo().getId());
        sentencia.setString(4, equipo.getMarca());
        sentencia.setString(5, equipo.getModelo());
        sentencia.setString(6, equipo.getSerie());
        sentencia.setInt(7, equipo.getLaboratorio().getId());
        sentencia.setString(8, equipo.getEstado().name());
        sentencia.setString(9, equipo.getObservaciones());
    }

    // Convierte una fila de SQL Server en objetos Java.
    private Equipo convertirEquipo(ResultSet resultado)
            throws SQLException {

        TipoEquipo tipo = new TipoEquipo(
                resultado.getInt("tipo_id"),
                resultado.getString("tipo_nombre")
        );

        Laboratorio laboratorio = new Laboratorio(
                resultado.getInt("laboratorio_id"),
                resultado.getString("laboratorio_codigo"),
                resultado.getString("laboratorio_nombre"),
                resultado.getString("laboratorio_ubicacion"),
                resultado.getInt("laboratorio_capacidad"),
                resultado.getString("laboratorio_estado")
        );

        Equipo equipo = new Equipo();
        equipo.setId(resultado.getInt("id"));
        equipo.setCodigoInventario(
                resultado.getString("codigo_inventario"));
        equipo.setNombre(resultado.getString("nombre"));
        equipo.setTipoEquipo(tipo);
        equipo.setMarca(resultado.getString("marca"));
        equipo.setModelo(resultado.getString("modelo"));
        equipo.setSerie(resultado.getString("serie"));
        equipo.setLaboratorio(laboratorio);
        equipo.setEstado(EstadoEquipo.valueOf(
                resultado.getString("estado")));
        equipo.setObservaciones(
                resultado.getString("observaciones"));

        return equipo;
    }
}
