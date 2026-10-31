
package com.sigelab.model;

public class DetallePrestamo {

    private int idDetallePrestamo;
    private int idPrestamo;
    private int idEquipo;
    private boolean devuelto;

    public DetallePrestamo() {
        this.devuelto = false;
    }

    public DetallePrestamo(int idEquipo) {
        this.idEquipo = idEquipo;
        this.devuelto = false;
    }

    public int getIdDetallePrestamo() {
        return idDetallePrestamo;
    }

    public void setIdDetallePrestamo(int idDetallePrestamo) {
        this.idDetallePrestamo = idDetallePrestamo;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
}
