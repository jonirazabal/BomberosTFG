package com.example.android.bomberostfg;


public class Unidad {
    private String mcptt_id;
    private String nombre;
    private String especialidad;
    private int patrulla;
    private float latitud;
    private float longitud;
    public Unidad() { }

    public String getMcptt_id() {
        return mcptt_id;
    }

    public void setMcptt_id(String mcptt_id) {
        this.mcptt_id = mcptt_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getPatrulla() {
        return patrulla;
    }

    public void setPatrulla(int patrulla) {
        this.patrulla = patrulla;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "{" +
                ", mcptt_id='" + mcptt_id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", especialcodigoad='" + especialidad + '\'' +
                ", patrulla=" + patrulla +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                "}\n";
    }
}
