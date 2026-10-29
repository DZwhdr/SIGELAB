package com.sigelab.service;

import com.sigelab.model.Equipo;
import com.sigelab.model.Laboratorio;
import com.sigelab.model.TipoEquipo;

import com.sigelab.repository.EquipoRepository;
import com.sigelab.repository.TipoEquipoRepository;
import com.sigelab.repository.LaboratorioRepository;

import java.sql.SQLException;
import java.util.List;

public class EquipoService {

    private final EquipoRepository equipoRepository =
            new EquipoRepository();

    private final TipoEquipoRepository tipoRepository =
            new TipoEquipoRepository();

    private final LaboratorioRepository laboratorioRepository =
            new LaboratorioRepository();

    // Registrar un equipo nuevo
    public void guardar(Equipo equipo) throws SQLException {
        validarDatos(equipo);

        if (equipo.getId() != 0) {
            throw new IllegalArgumentException(
                    "Para registrar un equipo nuevo, usa el botón Nuevo."
            );
        }

        validarCodigoUnico(equipo);
        equipoRepository.guardar(equipo);
    }

    // Actualizar un equipo existente
    public void actualizar(Equipo equipo) throws SQLException {
        validarDatos(equipo);

        if (equipo.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona el equipo que deseas actualizar."
            );
        }

        validarCodigoUnico(equipo);

        if (!equipoRepository.actualizar(equipo)) {
            throw new IllegalArgumentException(
                    "El equipo ya no existe en la base de datos."
            );
        }
    }

    // Listar todos los equipos
    public List<Equipo> listar() throws SQLException {
        return equipoRepository.listar();
    }

    // Buscar equipo por código de inventario
    public Equipo buscarPorCodigo(String codigo) throws SQLException {
        String codigoLimpio = validarTexto(
                codigo,
                "Código de inventario",
                30
        );

        return equipoRepository.buscarPorCodigo(codigoLimpio);
    }

    // Listar tipos de equipo
    public List<TipoEquipo> listarTipos() throws SQLException {
        return tipoRepository.listar();
    }

    // Listar laboratorios disponibles
    public List<Laboratorio> listarLaboratorios()
            throws SQLException {
        return laboratorioRepository.listar();
    }

    // Validar los datos del equipo
    private void validarDatos(Equipo equipo) throws SQLException {

        if (equipo == null) {
            throw new IllegalArgumentException(
                    "Los datos del equipo son obligatorios."
            );
        }

        equipo.setCodigoInventario(
                validarTexto(
                        equipo.getCodigoInventario(),
                        "Código de inventario",
                        30
                )
        );

        equipo.setNombre(
                validarTexto(
                        equipo.getNombre(),
                        "Nombre",
                        100
                )
        );

        equipo.setMarca(
                validarTexto(
                        equipo.getMarca(),
                        "Marca",
                        60
                )
        );

        equipo.setModelo(
                validarTexto(
                        equipo.getModelo(),
                        "Modelo",
                        100
                )
        );

        equipo.setSerie(
                validarTexto(
                        equipo.getSerie(),
                        "Serie",
                        100
                )
        );

        if (equipo.getEstado() == null) {
            throw new IllegalArgumentException(
                    "Selecciona el estado del equipo."
            );
        }

        String observaciones = equipo.getObservaciones();

        if (observaciones == null) {
            observaciones = "";
        }

        observaciones = observaciones.strip();

        if (observaciones.length() > 500) {
            throw new IllegalArgumentException(
                    "Las observaciones permiten hasta 500 caracteres."
            );
        }

        equipo.setObservaciones(observaciones);

        validarTipo(equipo);
        validarLaboratorio(equipo);
    }

    // Validar el tipo de equipo seleccionado
    private void validarTipo(Equipo equipo) throws SQLException {

        TipoEquipo seleccionado = equipo.getTipoEquipo();

        if (seleccionado == null || seleccionado.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un tipo de equipo."
            );
        }

        TipoEquipo existente =
                tipoRepository.buscarPorId(seleccionado.getId());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El tipo de equipo seleccionado ya no existe."
            );
        }

        equipo.setTipoEquipo(existente);
    }

    // Validar el laboratorio seleccionado
    private void validarLaboratorio(Equipo equipo)
            throws SQLException {

        Laboratorio seleccionado = equipo.getLaboratorio();

        if (seleccionado == null || seleccionado.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un laboratorio."
            );
        }

        List<Laboratorio> laboratorios =
                laboratorioRepository.listar();

        for (Laboratorio laboratorio : laboratorios) {

            if (laboratorio.getId() == seleccionado.getId()) {
                equipo.setLaboratorio(laboratorio);
                return;
            }
        }

        throw new IllegalArgumentException(
                "El laboratorio seleccionado ya no existe."
        );
    }

    // Comprobar que el código de inventario sea único
    private void validarCodigoUnico(Equipo equipo)
            throws SQLException {

        Equipo existente = equipoRepository.buscarPorCodigo(
                equipo.getCodigoInventario()
        );

        if (existente != null
                && existente.getId() != equipo.getId()) {

            throw new IllegalArgumentException(
                    "Ya existe otro equipo con ese código de inventario."
            );
        }
    }

    // Validar textos obligatorios y su longitud
    private String validarTexto(
            String valor,
            String campo,
            int longitudMaxima
    ) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio."
            );
        }

        String limpio = valor.strip();

        if (limpio.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " permite hasta "
                            + longitudMaxima + " caracteres."
            );
        }

        return limpio;
    }
    // Eliminar un equipo por su ID
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del equipo no es válido.");
        }

        return equipoRepository.eliminar(id);
    }
}