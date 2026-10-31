package com.sigelab; // O el paquete donde tengas tu TestLogin

import com.sigelab.model.Usuario;
import com.sigelab.service.UsuarioService; // Usamos el Service para autenticar

public class TestLogin {
    public static void main(String[] args) {

        // Instanciamos el Service (que maneja la lógica de negocio y usa el DAO por debajo)
        UsuarioService usuarioService = new UsuarioService();

        // Credenciales de prueba
        String userPrueba = "admin";
        String passPrueba = "admin123";

        // Llamamos al método de autenticación del servicio
        Usuario usuarioEncontrado = usuarioService.autenticar(userPrueba, passPrueba);

        if (usuarioEncontrado != null) {
            System.out.println("¡Inicio de sesión exitoso!");
            System.out.println("Bienvenido, " + usuarioEncontrado.getNombre() + " " + usuarioEncontrado.getApellido());
            System.out.println("Correo electrónico: " + usuarioEncontrado.getCorreo());
            System.out.println("Activo: " + usuarioEncontrado.isActivo());
        } else {
            System.out.println("Fallo en la autenticación: Usuario o contraseña incorrectos.");
        }
    }
}