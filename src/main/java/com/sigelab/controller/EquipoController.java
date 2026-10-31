
package com.sigelab.controller;

import com.sigelab.model.Equipo;
import com.sigelab.model.Laboratorio;
import com.sigelab.model.TipoEquipo;
import com.sigelab.service.EquipoService;

import java.sql.SQLException;
import java.util.List;

public class EquipoController {

    private final EquipoService equipoService = new EquipoService();

    // Guardar un equipo
    public void guardar(Equipo equipo) throws SQLException {
        equipoService.guardar(equipo);
    }

    // Actualizar un equipo existente
    public void actualizar(Equipo equipo) throws SQLException {
        equipoService.actualizar(equipo);
    }

    // Listar todos los equipos
    public List<Equipo> listar() throws SQLException {
        return equipoService.listar();
    }

    // Buscar un equipo por su código de inventario
    public Equipo buscarPorCodigo(String codigo) throws SQLException {
        return equipoService.buscarPorCodigo(codigo);
    }

    // Listar los tipos de equipo
    public List<TipoEquipo> listarTipos() throws SQLException {
        return equipoService.listarTipos();
    }

    // Listar los laboratorios disponibles
    public List<Laboratorio> listarLaboratorios() throws SQLException {
        return equipoService.listarLaboratorios();
    }
}
