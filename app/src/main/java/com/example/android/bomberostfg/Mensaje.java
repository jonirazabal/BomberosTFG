package com.example.android.bomberostfg;

class Mensaje {
    private String key;
    private String mcptt_id;
    private int patrulla;
    private String fecha;
    private String tipo;
    private String mensaje;
    public Mensaje(String pkey, String pid, int ppatrulla, String pfecha, String ptipo, String pmensaje) {
        key = pkey;
        mcptt_id = pid;
        patrulla = ppatrulla;
        fecha = pfecha;
        tipo = ptipo;
        mensaje = pmensaje;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMcptt_id() {
        return mcptt_id;
    }

    public void setMcptt_id(String mcptt_id) {
        this.mcptt_id = mcptt_id;
    }

    public int getPatrulla() {
        return patrulla;
    }

    public void setPatrulla(int patrulla) {
        this.patrulla = patrulla;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "{" +
                "key='" + key + '\'' +
                ", mcptt_id='" + mcptt_id + '\'' +
                ", patrulla=" + patrulla +
                ", fecha='" + fecha + '\'' +
                ", tipo='" + tipo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
