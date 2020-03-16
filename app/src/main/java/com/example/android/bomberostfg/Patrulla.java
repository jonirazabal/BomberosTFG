package com.example.android.bomberostfg;

public class Patrulla {
    private int id;
    private int incendio;
    private int salvamento;
    private int asistencia;
    private int operativa;
    public Patrulla(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int isIncendio() {
        return incendio;
    }

    public void setIncendio(int incendio) {
        this.incendio = incendio;
    }

    public int isSalvamento() {
        return salvamento;
    }

    public void setSalvamento(int salvamento) {
        this.salvamento = salvamento;
    }

    public int isAsistencia() {
        return asistencia;
    }

    public void setAsistencia(int asistencia) {
        this.asistencia = asistencia;
    }

    public int isOperativa() {
        return operativa;
    }

    public void setOperativa(int operativa) {
        this.operativa = operativa;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", incendio=" + incendio +
                ", salvamento=" + salvamento +
                ", asistencia=" + asistencia +
                ", operativa=" + operativa +
                "}\n";
    }
}
