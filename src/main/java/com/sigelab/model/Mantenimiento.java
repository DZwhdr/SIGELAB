package com.sigelab.model;

import java.time.LocalDateTime;

public class Mantenimiento {

    private int id;
    private int equipoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String motivo;
    private String estado;
    private String observaciones;

    public Mantenimiento() {
    }

    public Mantenimiento(int equipoId, String motivo,
                         String estado, String observaciones) {
        this.equipoId = equipoId;
        this.motivo = motivo;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public Mantenimiento(int id, int equipoId,
                         LocalDateTime fechaInicio,
                         LocalDateTime fechaFin,
                         String motivo, String estado,
                         String observaciones) {
        this.id = id;
        this.equipoId = equipoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
        this.estado = estado;
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

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}