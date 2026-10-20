
package org.example.controller;

import org.example.repository.EquipoQRRepository;
import org.example.repository.EquipoQRRepository.DatosEquipoQR;

import java.sql.SQLException;

public class EquipoQRController {

    private static final String PREFIJO_QR = "SIGELAB-EQ:";

    private final EquipoQRRepository repository;

    public EquipoQRController() {
        this.repository = new EquipoQRRepository();
    }

    // Extrae el código de inventario del contenido del QR.
    public String obtenerCodigoInventario(String contenidoQR) {

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

        String codigoInventario =
                contenido.substring(PREFIJO_QR.length()).trim();

        if (codigoInventario.isEmpty()) {
            throw new IllegalArgumentException(
                    "El QR no contiene un código de inventario."
            );
        }

        return codigoInventario;
    }

    // Busca en SQL Server el equipo identificado por el QR.
    public DatosEquipoQR buscarEquipoPorQR(String contenidoQR)
            throws SQLException {

        String codigoInventario =
                obtenerCodigoInventario(contenidoQR);

        return repository.buscarPorCodigo(codigoInventario);
    }

    // Prepara el contenido que debe guardarse dentro de un QR.
    public String generarContenidoQR(String codigoInventario) {

        if (codigoInventario == null ||
                codigoInventario.isBlank()) {

            throw new IllegalArgumentException(
                    "El código de inventario no puede estar vacío."
            );
        }

        return PREFIJO_QR + codigoInventario.trim();
    }
}
