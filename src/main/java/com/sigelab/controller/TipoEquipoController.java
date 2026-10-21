package com.sigelab.controller;

import com.sigelab.model.TipoEquipo;
import com.sigelab.service.TipoEquipoService;

import java.sql.SQLException;
import java.util.List;

public class TipoEquipoController {

    private final TipoEquipoService service;

    public TipoEquipoController() {
        this.service = new TipoEquipoService();
    }

    public void registrar(
            String nombre,
            String descripcion
    ) throws SQLException {

        TipoEquipo tipo = new TipoEquipo(
                nombre,
                descripcion
        );

        service.registrar(tipo);
    }

    public void actualizar(
            int id,
            String nombre,
            String descripcion
    ) throws SQLException {

        TipoEquipo existente = service.buscarPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException(
                    "No se encontró el tipo de equipo."
            );
        }

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);

        service.actualizar(existente);
    }

    public void darDeBaja(int id) throws SQLException {
        service.darDeBaja(id);
    }

    public List<TipoEquipo> listarActivos()
            throws SQLException {

        return service.obtenerActivos();
    }

    public List<TipoEquipo> listarTodos()
            throws SQLException {

        return service.obtenerTodos();
    }

    public TipoEquipo buscarPorId(int id)
            throws SQLException {

        return service.buscarPorId(id);
    }
}
