package com.sigelab.controller;

import com.sigelab.model.Equipo;
import com.sigelab.model.Prestamo;
import com.sigelab.service.PrestamoService;

import java.sql.SQLException;

public class PrestamoController {

    private final PrestamoService service;

    public PrestamoController() {
        this.service = new PrestamoService();
    }

    public Equipo buscarEquipoDisponible(String codigo)
            throws SQLException {

        return service.buscarEquipoDisponible(codigo);
    }

    public int registrarPrestamo(Prestamo prestamo)
            throws SQLException {

        return service.registrarPrestamo(prestamo);
    }

    public void validarPrestamo(Prestamo prestamo) {
        service.validarPrestamo(prestamo);
    }
}
