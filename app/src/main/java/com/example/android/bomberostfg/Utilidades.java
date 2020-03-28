package com.example.android.bomberostfg;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utilidades {
    public static List<List<HashMap<String,String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    public static Punto coordenadas=new Punto();
    public static List<Unidad> unidades = new ArrayList<Unidad>();
    public static List<Actuacion> actuaciones = new ArrayList<Actuacion>();
    public static List<String> emergenciaKey = new ArrayList<String>();
    public static HashMap<Integer, ArrayList<String>> patrullaActuacion = new HashMap<>();
    public static ArrayList<Patrulla> patrullas = new ArrayList<>();
    public static LatLng actuacionFragment = new LatLng(0,0);
    public static Actuacion selectedActuacion;
    public static ArrayList<Orden> ordenMandada= new ArrayList<Orden>();
    public static ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
    public static ArrayList<Mensaje> notificacionMensajes = new ArrayList<Mensaje>();
}


