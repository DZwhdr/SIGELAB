package com.sigelab.service;

import com.sigelab.model.TipoEquipo;
import com.sigelab.repository.TipoEquipoRepository;

import java.sql.SQLException;
import java.util.List;

public class TipoEquipoService {

    private final TipoEquipoRepository repository;

    public TipoEquipoService() {
        this.repository = new TipoEquipoRepository();
    }

    public void registrar(TipoEquipo tipo) throws SQLException {

        validarTipo(tipo);

        String nombre = tipo.getNombre().trim();

        if (repository.existeNombre(nombre, null)) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de equipo con el nombre: " + nombre
            );
        }

        tipo.setNombre(nombre);

        if (!repository.guardar(tipo)) {
            throw new SQLException(
                    "No se pudo registrar el tipo de equipo."
            );
        }
    }

    public void actualizar(TipoEquipo tipo) throws SQLException {

        validarTipo(tipo);

        if (tipo.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El ID del tipo de equipo es obligatorio."
            );
        }

        String nombre = tipo.getNombre().trim();

        if (repository.existeNombre(nombre, tipo.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe otro tipo de equipo con el nombre: " + nombre
            );
        }

        tipo.setNombre(nombre);

        if (!repository.actualizar(tipo)) {
            throw new SQLException(
                    "No se encontró el tipo de equipo para actualizar."
            );
        }
    }

    public void darDeBaja(int idTipo) throws SQLException {

        if (idTipo <= 0) {
            throw new IllegalArgumentException(
                    "El ID del tipo de equipo no es válido."
            );
        }

        if (!repository.eliminarLogico(idTipo)) {
            throw new SQLException(
                    "No se encontró el tipo de equipo para dar de baja."
            );
        }
    }

    public List<TipoEquipo> obtenerActivos() throws SQLException {
        return repository.listarTodos();
    }

    public List<TipoEquipo> obtenerTodos() throws SQLException {
        return repository.listar();
    }

    public TipoEquipo buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero."
            );
        }

        return repository.buscarPorId(id);
    }

    private void validarTipo(TipoEquipo tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un tipo de equipo."
            );
        }

        if (tipo.getNombre() == null ||
                tipo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del tipo de equipo es obligatorio."
            );
        }
    }
}
