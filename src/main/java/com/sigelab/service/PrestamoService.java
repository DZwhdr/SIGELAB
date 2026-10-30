package com.sigelab.service;

import com.sigelab.model.DetallePrestamo;
import com.sigelab.model.Equipo;
import com.sigelab.model.Prestamo;
import com.sigelab.repository.EquipoRepository;
import com.sigelab.repository.PrestamoRepository;
import org.example.enums.EstadoEquipo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PrestamoService {

    private final PrestamoRepository repository;
    private final EquipoRepository equipoRepository;

    public PrestamoService() {
        this.repository = new PrestamoRepository();
        this.equipoRepository = new EquipoRepository();
    }

    // Buscar y validar un equipo por código de inventario
    public Equipo buscarEquipoDisponible(String codigo)
            throws SQLException {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingrese un código de inventario."
            );
        }

        Equipo equipo = equipoRepository.buscarPorCodigo(
                codigo.trim()
        );

        if (equipo == null) {
            throw new IllegalArgumentException(
                    "No existe un equipo con ese código de inventario."
            );
        }

        // RN-07 y RN-08
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw new IllegalArgumentException(
                    "El equipo no está disponible para préstamo. "
                            + "Estado actual: "
                            + equipo.getEstado()
            );
        }

        return equipo;
    }

    public int registrarPrestamo(Prestamo prestamo)
            throws SQLException {

        validarPrestamo(prestamo);

        return repository.guardarPrestamo(prestamo);
    }

    public void validarPrestamo(Prestamo prestamo) {

        if (prestamo == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar los datos del préstamo."
            );
        }

        if (prestamo.getSolicitante() == null
                || prestamo.getSolicitante().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe ingresar el nombre del solicitante."
            );
        }

        if (prestamo.getIdUsuarioRegistro() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un usuario válido."
            );
        }

        LocalDateTime fechaPrestamo =
                prestamo.getFechaPrestamo();

        LocalDateTime fechaDevolucion =
                prestamo.getFechaDevolucionEsperada();

        if (fechaPrestamo == null || fechaDevolucion == null) {
            throw new IllegalArgumentException(
                    "Debe ingresar las fechas del préstamo."
            );
        }

        if (!fechaDevolucion.isAfter(fechaPrestamo)) {
            throw new IllegalArgumentException(
                    "La fecha de devolución debe ser posterior "
                            + "a la fecha del préstamo."
            );
        }

        // RN-05: mínimo un equipo
        if (prestamo.getDetalles() == null
                || prestamo.getDetalles().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe agregar al menos un equipo al préstamo."
            );
        }

        // RN-06: impedir equipos duplicados
        Set<Integer> equiposAgregados = new HashSet<>();

        for (DetallePrestamo detalle : prestamo.getDetalles()) {

            if (detalle == null || detalle.getIdEquipo() <= 0) {
                throw new IllegalArgumentException(
                        "Existe un equipo inválido en el préstamo."
                );
            }

            if (!equiposAgregados.add(detalle.getIdEquipo())) {
                throw new IllegalArgumentException(
                        "No puede agregar el mismo equipo "
                                + "dos veces al préstamo."
                );
            }
        }
    }
}
