package com.sigelab.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Prestamo {

    private int idPrestamo;
    private String solicitante;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucionEsperada;
    private int idUsuarioRegistro;
    private String estado;
    private String observaciones;

    private List<DetallePrestamo> detalles;

    public Prestamo() {
        this.fechaPrestamo = LocalDateTime.now();
        this.estado = "ACTIVO";
        this.detalles = new ArrayList<>();
    }

    public Prestamo(
            String solicitante,
            LocalDateTime fechaDevolucionEsperada,
            int idUsuarioRegistro,
            String observaciones
    ) {
        this();
        this.solicitante = solicitante;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.idUsuarioRegistro = idUsuarioRegistro;
        this.observaciones = observaciones;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(
            LocalDateTime fechaDevolucionEsperada
    ) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public int getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(int idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
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

    public List<DetallePrestamo> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePrestamo> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetallePrestamo detalle) {
        this.detalles.add(detalle);
    }
}
