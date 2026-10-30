package com.sigelab.model;

import java.time.LocalDateTime;

public class Devolucion {

    private int id;
    private int equipoId;
    private int usuarioId;
    private LocalDateTime fechaDevolucion;
    private String estadoEquipo;
    private String observaciones;

    public Devolucion() {
    }

    public Devolucion(int equipoId, int usuarioId, String estadoEquipo, String observaciones) {
        this.equipoId = equipoId;
        this.usuarioId = usuarioId;
        this.estadoEquipo = estadoEquipo;
        this.observaciones = observaciones;
    }

    public Devolucion(int id, int equipoId, int usuarioId,
                      LocalDateTime fechaDevolucion, String estadoEquipo,
                      String observaciones) {
        this.id = id;
        this.equipoId = equipoId;
        this.usuarioId = usuarioId;
        this.fechaDevolucion = fechaDevolucion;
        this.estadoEquipo = estadoEquipo;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstadoEquipo() {
        return estadoEquipo;
    }

    public void setEstadoEquipo(String estadoEquipo) {
        this.estadoEquipo = estadoEquipo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}