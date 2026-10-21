package org.example.controller;

import org.example.model.Equipo;
import org.example.model.Laboratorio;
import com.sigelab.model.TipoEquipo;
import com.sigelab.service.EquipoService;

import java.sql.SQLException;
import java.util.List;

public class EquipoController {

    private final EquipoService equipoService = new EquipoService();

    public void guardar(Equipo equipo) throws SQLException {
        equipoService.guardar(equipo);
    }

    public void actualizar(Equipo equipo) throws SQLException {
        equipoService.actualizar(equipo);
    }

    public List<Equipo> listar() throws SQLException {
        return equipoService.listar();
    }

    public Equipo buscarPorCodigo(String codigo) throws SQLException {
        return equipoService.buscarPorCodigo(codigo);
    }

    public List<TipoEquipo> listarTipos() throws SQLException {
        return equipoService.listarTipos();
    }

    public List<Laboratorio> listarLaboratorios() throws SQLException {
        return equipoService.listarLaboratorios();
    }
}