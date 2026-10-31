
package com.sigelab.model;

import org.example.enums.EstadoEquipo;

public class Equipo {

    private int id;
    private String codigoInventario;
    private String nombre;
    private TipoEquipo tipoEquipo;
    private String marca;
    private String modelo;
    private String serie;
    private Laboratorio laboratorio;
    private EstadoEquipo estado;
    private String observaciones;

    // Constructor
    public Equipo() {
        this.estado = EstadoEquipo.DISPONIBLE;
        this.observaciones = "";
    }

    // Obtener el ID
    public int getId() {
        return id;
    }

    // Establecer el ID
    public void setId(int id) {
        this.id = id;
    }

    // Obtener el código de inventario
    public String getCodigoInventario() {
        return codigoInventario;
    }

    // Establecer el código de inventario
    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    // Obtener el nombre
    public String getNombre() {
        return nombre;
    }

    // Establecer el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Obtener el tipo de equipo
    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    // Establecer el tipo de equipo
    public void setTipoEquipo(TipoEquipo tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    // Obtener la marca
    public String getMarca() {
        return marca;
    }

    // Establecer la marca
    public void setMarca(String marca) {
        this.marca = marca;
    }

    // Obtener el modelo
    public String getModelo() {
        return modelo;
    }

    // Establecer el modelo
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    // Obtener el número de serie
    public String getSerie() {
        return serie;
    }

    // Establecer el número de serie
    public void setSerie(String serie) {
        this.serie = serie;
    }

    // Obtener el laboratorio
    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    // Establecer el laboratorio
    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    // Obtener el estado
    public EstadoEquipo getEstado() {
        return estado;
    }

    // Establecer el estado
    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    // Obtener las observaciones
    public String getObservaciones() {
        return observaciones;
    }

    // Establecer las observaciones
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // Mostrar el equipo como texto
    @Override
    public String toString() {
        return codigoInventario + " - " + nombre;
    }
}
