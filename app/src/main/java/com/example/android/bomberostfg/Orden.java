package com.example.android.bomberostfg;

public class Orden {
    private int id;
    private String nombre;
    private int patrulla;
    private String direccion;
    private String fecha;
    private String fin;
    private String key;

    public Orden(int id, String key, String nombre, int patrulla, String direccion, String fecha, String pfinal) {
        this.id = id;
        this.key = key;
        this.nombre = nombre;
        this.patrulla = patrulla;
        this.direccion = direccion;
        this.fecha = fecha;
        this.fin = pfinal;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPatrulla() {
        return patrulla;
    }

    public void setPatrulla(int patrulla) {
        this.patrulla = patrulla;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", patrulla=" + patrulla +
                ", direccion='" + direccion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", fin='" + fin + '\'' +
                "}\n";
    }
}
