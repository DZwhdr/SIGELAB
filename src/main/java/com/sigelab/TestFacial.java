package com.sigelab;

import com.sigelab.service.FacialAuthService;
import java.io.File;

public class TestFacial {
    public static void main(String[] args) {
        FacialAuthService facialService = new FacialAuthService();

        // Puedes cambiar estas rutas por las rutas de 2 fotos reales que tengas en tu computadora
        File fotoCamara = new File("C:\\Users\\jazmi\\OneDrive\\Escritorio\\foto1.jpg");
        File fotoDB = new File("C:\\Users\\jazmi\\OneDrive\\Escritorio\\foto2.jpg");

        System.out.println("Enviando imágenes al servicio biométrico de Python...");
        boolean esValido = facialService.autenticarRostro(fotoCamara, fotoDB);

        if (esValido) {
            System.out.println("✅ ¡Rostro autenticado con éxito!");
        } else {
            System.out.println("❌ Los rostros no coinciden o el archivo de imagen no fue encontrado.");
        }
    }
}