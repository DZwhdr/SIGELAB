package com.sigelab.model;

public class Usuario {
    private int idUsuario;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String correo;
    private boolean activo;
    private Rol rol;
    private boolean primerLogin; // Bandera para obligar el cambio de pass y registro facial en el primer ingreso

    // Constructor vacío de cajón
    public Usuario() {
    }

    // Constructor completo con todos los datos y la bandera de primer login
    public Usuario(int idUsuario, String username, String password, String nombre, String apellido, String correo, boolean activo, Rol rol, boolean primerLogin) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.activo = activo;
        this.rol = rol;
        this.primerLogin = primerLogin;
    }

    // Getters y Setters de rigor
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isPrimerLogin() {
        return primerLogin;
    }

    public void setPrimerLogin(boolean primerLogin) {
        this.primerLogin = primerLogin;
    }
}