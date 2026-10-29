
package com.sigelab.model;

public class Laboratorio {

    private int id;
    private String codigo;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private String estado;

    // Constructor vacío
    public Laboratorio() {
        this.estado = "ACTIVO";
    }

    // Constructor con todos los atributos
    public Laboratorio(int id, String codigo, String nombre,
                       String ubicacion, int capacidad, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Obtener el ID
    public int getId() {
        return id;
    }

    // Establecer el ID
    public void setId(int id) {
        this.id = id;
    }

    // Obtener el código
    public String getCodigo() {
        return codigo;
    }

    // Establecer el código
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // Obtener el nombre
    public String getNombre() {
        return nombre;
    }

    // Establecer el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Obtener la ubicación
    public String getUbicacion() {
        return ubicacion;
    }

    // Establecer la ubicación
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    // Obtener la capacidad
    public int getCapacidad() {
        return capacidad;
    }

    // Establecer la capacidad
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    // Obtener el estado
    public String getEstado() {
        return estado;
    }

    // Establecer el estado
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Mostrar el laboratorio como texto
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
