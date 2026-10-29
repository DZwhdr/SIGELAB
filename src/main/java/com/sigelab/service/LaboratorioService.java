
package com.sigelab.service;

import com.sigelab.model.Laboratorio;
import com.sigelab.repository.LaboratorioRepository;

import java.sql.SQLException;
import java.util.List;

public class LaboratorioService {

    private final LaboratorioRepository repository =
            new LaboratorioRepository();

    // Registrar un laboratorio nuevo
    public void guardar(Laboratorio laboratorio) throws SQLException {
        validar(laboratorio);

        if (laboratorio.getId() != 0) {
            throw new IllegalArgumentException(
                    "Para guardar un laboratorio nuevo, presiona Nuevo."
            );
        }

        comprobarCodigoDisponible(laboratorio);
        repository.guardar(laboratorio);
    }

    // Actualizar un laboratorio existente
    public void actualizar(Laboratorio laboratorio) throws SQLException {
        validar(laboratorio);

        if (laboratorio.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un laboratorio para actualizar."
            );
        }

        comprobarCodigoDisponible(laboratorio);

        if (!repository.actualizar(laboratorio)) {
            throw new IllegalArgumentException(
                    "El laboratorio ya no existe en la base de datos."
            );
        }
    }

    // Listar todos los laboratorios
    public List<Laboratorio> listar() throws SQLException {
        return repository.listar();
    }

    // Buscar un laboratorio por su código
    public Laboratorio buscarPorCodigo(String codigo)
            throws SQLException {

        String codigoLimpio = validarTexto(
                codigo,
                "Código",
                20
        );

        return repository.buscarPorCodigo(codigoLimpio);
    }

    // Comprobar que el código no esté asignado a otro laboratorio
    private void comprobarCodigoDisponible(Laboratorio laboratorio)
            throws SQLException {

        Laboratorio existente = repository.buscarPorCodigo(
                laboratorio.getCodigo()
        );

        if (existente != null
                && existente.getId() != laboratorio.getId()) {

            throw new IllegalArgumentException(
                    "Ya existe otro laboratorio con ese código."
            );
        }
    }

    // Validar los datos del laboratorio
    private void validar(Laboratorio laboratorio) {
        if (laboratorio == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar los datos del laboratorio."
            );
        }

        laboratorio.setCodigo(
                validarTexto(
                        laboratorio.getCodigo(),
                        "Código",
                        20
                )
        );

        laboratorio.setNombre(
                validarTexto(
                        laboratorio.getNombre(),
                        "Nombre",
                        100
                )
        );

        laboratorio.setUbicacion(
                validarTexto(
                        laboratorio.getUbicacion(),
                        "Ubicación",
                        150
                )
        );

        if (laboratorio.getCapacidad() <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero."
            );
        }

        String estado = validarTexto(
                laboratorio.getEstado(),
                "Estado",
                30
        ).toUpperCase();

        if (!estado.equals("ACTIVO")
                && !estado.equals("INACTIVO")) {

            throw new IllegalArgumentException(
                    "El estado debe ser ACTIVO o INACTIVO."
            );
        }

        laboratorio.setEstado(estado);
    }

    // Validar textos obligatorios y su longitud máxima
    private String validarTexto(
            String texto,
            String campo,
            int limite
    ) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio."
            );
        }

        String textoLimpio = texto.strip();

        if (textoLimpio.length() > limite) {
            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " permite máximo "
                            + limite
                            + " caracteres."
            );
        }

        return textoLimpio;
    }

    // Eliminar un laboratorio por su ID
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del laboratorio no es válido."
            );
        }

        return repository.eliminar(id);
    }
}
