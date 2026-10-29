
package com.sigelab.controller;

import com.sigelab.model.Laboratorio;
import com.sigelab.service.LaboratorioService;

import java.sql.SQLException;
import java.util.List;

public class LaboratorioController {

    private final LaboratorioService service =
            new LaboratorioService();

    // Registrar un laboratorio nuevo
    public Laboratorio guardar(
            String codigo,
            String nombre,
            String ubicacion,
            int capacidad,
            String estado
    ) throws SQLException {

        Laboratorio laboratorio = new Laboratorio(
                0,
                codigo,
                nombre,
                ubicacion,
                capacidad,
                estado
        );

        service.guardar(laboratorio);

        return laboratorio;
    }

    // Actualizar un laboratorio existente
    public void actualizar(
            int id,
            String codigo,
            String nombre,
            String ubicacion,
            int capacidad,
            String estado
    ) throws SQLException {

        Laboratorio laboratorio = new Laboratorio(
                id,
                codigo,
                nombre,
                ubicacion,
                capacidad,
                estado
        );

        service.actualizar(laboratorio);
    }

    // Listar todos los laboratorios
    public List<Laboratorio> listar() throws SQLException {
        return service.listar();
    }

    // Buscar un laboratorio por su código
    public Laboratorio buscarPorCodigo(
            String codigo
    ) throws SQLException {

        return service.buscarPorCodigo(codigo);
    }

    // Eliminar un laboratorio por su ID
    public boolean eliminar(int id) throws SQLException {
        return service.eliminar(id);
    }
}
