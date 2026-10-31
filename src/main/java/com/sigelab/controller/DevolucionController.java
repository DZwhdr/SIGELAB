package com.sigelab.controller;

import com.sigelab.model.Devolucion;
import com.sigelab.service.DevolucionService;

import java.sql.SQLException;
import java.util.List;

public class DevolucionController {

    private final DevolucionService service;

    public DevolucionController() {
        this.service = new DevolucionService();
    }

    public boolean guardar(int equipoId, int usuarioId,
                           String estadoEquipo, String observaciones)
            throws SQLException {

        Devolucion devolucion = new Devolucion(
                equipoId,
                usuarioId,
                estadoEquipo,
                observaciones
        );

        return service.guardar(devolucion);
    }

    public List<Devolucion> listar() throws SQLException {
        return service.listar();
    }

    public boolean actualizar(int id, int equipoId, int usuarioId,
                              String estadoEquipo, String observaciones)
            throws SQLException {

        Devolucion devolucion = new Devolucion();
        devolucion.setId(id);
        devolucion.setEquipoId(equipoId);
        devolucion.setUsuarioId(usuarioId);
        devolucion.setEstadoEquipo(estadoEquipo);
        devolucion.setObservaciones(observaciones);

        return service.actualizar(devolucion);
    }

    public boolean eliminar(int id) throws SQLException {
        return service.eliminar(id);
    }
}