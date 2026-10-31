package com.sigelab.controller;

import com.sigelab.repository.EquipoQRRepository;
import com.sigelab.repository.EquipoQRRepository.DatosEquipoQR;

import java.sql.SQLException;

public class EquipoQRController {

    private static final String PREFIJO_QR = "SIGELAB-EQ:";

    private final EquipoQRRepository repository;

    public EquipoQRController() {
        this.repository = new EquipoQRRepository();
    }

    public String obtenerCodigoInventario(
            String contenidoQR
    ) {

        if (contenidoQR == null || contenidoQR.isBlank()) {
            throw new IllegalArgumentException(
                    "El código QR está vacío."
            );
        }

        String contenido = contenidoQR.trim();

        if (!contenido.startsWith(PREFIJO_QR)) {
            throw new IllegalArgumentException(
                    "El código QR no pertenece al formato de SIGELAB."
            );
        }

        String codigoInventario = contenido
                .substring(PREFIJO_QR.length())
                .trim();

        if (codigoInventario.isEmpty()) {
            throw new IllegalArgumentException(
                    "El QR no contiene un código de inventario."
            );
        }

        return codigoInventario;
    }

    public DatosEquipoQR buscarEquipoPorQR(
            String contenidoQR
    ) throws SQLException {

        String codigoInventario =
                obtenerCodigoInventario(contenidoQR);

        return repository.buscarPorCodigo(
                codigoInventario
        );
    }

    public String generarContenidoQR(
            String codigoInventario
    ) {

        if (codigoInventario == null
                || codigoInventario.isBlank()) {

            throw new IllegalArgumentException(
                    "El código de inventario no puede estar vacío."
            );
        }

        return PREFIJO_QR + codigoInventario.trim();
    }
}
