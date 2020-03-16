package com.example.android.bomberostfg;

public class Emergencia {
    private String Tipo;
    private int Patrulla;
    private String Inicio;
    private String Final;
    public Emergencia(){

    }
    public Emergencia(String tipo, int patrulla, String inicio, String Final) {
        Tipo = tipo;
        Patrulla = patrulla;
        inicio = inicio;
        Final = Final;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public int getPatrulla() {
        return Patrulla;
    }

    public void setPatrulla(int patrulla) {
        Patrulla = patrulla;
    }

    public String getInicio() {
        return Inicio;
    }

    public void setinicio(String inicio) {
        inicio = inicio;
    }

    public String getFinal() {
        return Final;
    }

    public void setFinal(String Final) {
        Final = Final;
    }
}
