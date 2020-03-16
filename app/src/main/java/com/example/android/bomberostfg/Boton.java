package com.example.android.bomberostfg;

public class Boton {
    private String descripcion;
    private int icono;

    public Boton() {

    }

    public Boton(String descripcion, int icono) {
        this.descripcion = descripcion;
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}