package org.example.controller;

import org.example.model.Laboratorio;
import org.example.service.LaboratorioService;

import java.sql.SQLException;
import java.util.List;

public class LaboratorioController {

    private final LaboratorioService service =
            new LaboratorioService();

    public Laboratorio guardar(
            String codigo,
            String nombre,
            String ubicacion,
            int capacidad,
            String estado
    ) throws SQLException {
        Laboratorio laboratorio = new Laboratorio(
                0, codigo, nombre, ubicacion, capacidad, estado
        );

        service.guardar(laboratorio);
        return laboratorio;
    }

    public void actualizar(
            int id,
            String codigo,
            String nombre,
            String ubicacion,
            int capacidad,
            String estado
    ) throws SQLException {
        Laboratorio laboratorio = new Laboratorio(
                id, codigo, nombre, ubicacion, capacidad, estado
        );

        service.actualizar(laboratorio);
    }

    public List<Laboratorio> listar() throws SQLException {
        return service.listar();
    }

    public Laboratorio buscarPorCodigo(
            String codigo
    ) throws SQLException {
        return service.buscarPorCodigo(codigo);
    }
}