package com.sigelab.controller;

import com.sigelab.model.Mantenimiento;
import com.sigelab.service.MantenimientoService;

import java.sql.SQLException;
import java.util.List;

public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController() {
        service = new MantenimientoService();
    }

    public boolean guardar(int equipoId, String motivo,
                           String estado, String observaciones)
            throws SQLException {

        Mantenimiento mantenimiento = new Mantenimiento(
                equipoId,
                motivo,
                estado,
                observaciones
        );

        return service.guardar(mantenimiento);
    }

    public List<Mantenimiento> listar() throws SQLException {
        return service.listar();
    }

    public boolean actualizar(int id, int equipoId,
                              String motivo, String estado,
                              String observaciones)
            throws SQLException {

        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setId(id);
        mantenimiento.setEquipoId(equipoId);
        mantenimiento.setMotivo(motivo);
        mantenimiento.setEstado(estado);
        mantenimiento.setObservaciones(observaciones);

        return service.actualizar(mantenimiento);
    }

    public boolean eliminar(int id) throws SQLException {
        return service.eliminar(id);
    }
}