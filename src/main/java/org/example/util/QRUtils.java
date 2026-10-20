
package org.example.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class QRUtils {

    // Genera un código QR y lo guarda como archivo PNG.
    public static void generarQR(
            String contenido,
            String rutaArchivo,
            int ancho,
            int alto
    ) throws Exception {

        validarDatos(contenido, ancho, alto);

        Path ruta = Path.of(rutaArchivo);
        Path carpeta = ruta.toAbsolutePath().getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        BitMatrix matriz = new QRCodeWriter().encode(
                contenido,
                BarcodeFormat.QR_CODE,
                ancho,
                alto
        );

        MatrixToImageWriter.writeToPath(
                matriz,
                "PNG",
                ruta
        );
    }

    // Genera un código QR como imagen en memoria.
    public static BufferedImage generarQR(
            String contenido,
            int ancho,
            int alto
    ) throws Exception {

        validarDatos(contenido, ancho, alto);

        BitMatrix matriz = new QRCodeWriter().encode(
                contenido,
                BarcodeFormat.QR_CODE,
                ancho,
                alto
        );

        return MatrixToImageWriter.toBufferedImage(matriz);
    }

    // Lee un QR desde una imagen, por ejemplo de la cámara.
    // Devuelve null cuando no encuentra un código legible.
    public static String leerQRDesdeImagen(
            BufferedImage imagen
    ) throws Exception {

        if (imagen == null) {
            throw new IllegalArgumentException(
                    "La imagen está vacía."
            );
        }

        LuminanceSource fuente =
                new BufferedImageLuminanceSource(imagen);

        BinaryBitmap bitmap = new BinaryBitmap(
                new HybridBinarizer(fuente)
        );

        try {
            Result resultado = new MultiFormatReader().decode(
                    bitmap,
                    java.util.Map.of(
                            DecodeHintType.POSSIBLE_FORMATS,
                            java.util.List.of(BarcodeFormat.QR_CODE)
                    )
            );

            return resultado.getText();

        } catch (NotFoundException e) {
            return null;
        }
    }

    // Lee un QR desde un archivo de imagen.
    public static String leerQRDesdeArchivo(
            File archivo
    ) throws Exception {

        if (archivo == null || !archivo.isFile()) {
            throw new IllegalArgumentException(
                    "El archivo no existe."
            );
        }

        BufferedImage imagen = ImageIO.read(archivo);

        if (imagen == null) {
            throw new IllegalArgumentException(
                    "No se pudo cargar la imagen."
            );
        }

        return leerQRDesdeImagen(imagen);
    }

    // Permite leer un QR indicando su ruta.
    public static String leerQRDesdeArchivo(
            String rutaArchivo
    ) throws Exception {

        return leerQRDesdeArchivo(
                new File(rutaArchivo)
        );
    }

    private static void validarDatos(
            String contenido,
            int ancho,
            int alto
    ) {

        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del QR no puede estar vacío."
            );
        }

        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException(
                    "Las dimensiones del QR deben ser positivas."
            );
        }
    }
}
