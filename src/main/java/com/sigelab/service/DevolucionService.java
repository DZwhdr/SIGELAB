package com.sigelab.service;

import com.sigelab.model.Devolucion;
import com.sigelab.repository.DevolucionRepository;

import java.sql.SQLException;
import java.util.List;

public class DevolucionService {

    private final DevolucionRepository repository;

    public DevolucionService() {
        this.repository = new DevolucionRepository();
    }

    public boolean guardar(Devolucion devolucion) throws SQLException {
        validar(devolucion);
        return repository.guardar(devolucion);
    }

    public List<Devolucion> listar() throws SQLException {
        return repository.listar();
    }

    public boolean actualizar(Devolucion devolucion) throws SQLException {
        if (devolucion.getId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una devolución.");
        }

        validar(devolucion);
        return repository.actualizar(devolucion);
    }

    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la devolución no es válido.");
        }

        return repository.eliminar(id);
    }

    private void validar(Devolucion devolucion) {
        if (devolucion == null) {
            throw new IllegalArgumentException("La devolución no puede estar vacía.");
        }

        if (devolucion.getEquipoId() <= 0) {
            throw new IllegalArgumentException("Debe ingresar un ID de equipo válido.");
        }

        if (devolucion.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("Debe ingresar un ID de usuario válido.");
        }

        if (devolucion.getEstadoEquipo() == null
                || devolucion.getEstadoEquipo().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar el estado del equipo.");
        }

        if (!"BUENO".equals(devolucion.getEstadoEquipo())
                && !"DAÑADO".equals(devolucion.getEstadoEquipo())) {
            throw new IllegalArgumentException(
                    "La condición debe ser BUENO o DAÑADO."
            );
        }

        if (devolucion.getObservaciones() != null
                && devolucion.getObservaciones().length() > 500) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los 500 caracteres."
            );
        }
    }
}
