package com.sigelab.model;

public class TipoEquipo {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public TipoEquipo() {
        this.activo = true;
        this.descripcion = "";
    }

    public TipoEquipo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = "";
        this.activo = true;
    }

    public TipoEquipo(
            int id,
            String nombre,
            String descripcion,
            boolean activo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public TipoEquipo(
            String nombre,
            String descripcion
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdTipoEquipo() {
        return id;
    }

    public void setIdTipoEquipo(Integer idTipoEquipo) {
        this.id = idTipoEquipo == null
                ? 0
                : idTipoEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
