package com.sigelab.service;

import com.sigelab.model.Mantenimiento;
import com.sigelab.repository.MantenimientoRepository;

import java.sql.SQLException;
import java.util.List;

public class MantenimientoService {

    private final MantenimientoRepository repository;

    public MantenimientoService() {
        repository = new MantenimientoRepository();
    }

    public boolean guardar(Mantenimiento mantenimiento) throws SQLException {
        validar(mantenimiento);
        return repository.guardar(mantenimiento);
    }

    public List<Mantenimiento> listar() throws SQLException {
        return repository.listar();
    }

    public boolean actualizar(Mantenimiento mantenimiento) throws SQLException {
        if (mantenimiento.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un mantenimiento."
            );
        }

        validar(mantenimiento);
        return repository.actualizar(mantenimiento);
    }

    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del mantenimiento no es válido."
            );
        }

        return repository.eliminar(id);
    }

    private void validar(Mantenimiento mantenimiento) {
        if (mantenimiento == null) {
            throw new IllegalArgumentException(
                    "El mantenimiento no puede estar vacío."
            );
        }

        if (mantenimiento.getEquipoId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe ingresar un ID de equipo válido."
            );
        }

        if (mantenimiento.getMotivo() == null
                || mantenimiento.getMotivo().isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el motivo del mantenimiento."
            );
        }

        if (mantenimiento.getMotivo().length() > 250) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 250 caracteres."
            );
        }

        if (mantenimiento.getEstado() == null
                || mantenimiento.getEstado().isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un estado."
            );
        }

        if (mantenimiento.getObservaciones() != null
                && mantenimiento.getObservaciones().length() > 500) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los 500 caracteres."
            );
        }
    }
}