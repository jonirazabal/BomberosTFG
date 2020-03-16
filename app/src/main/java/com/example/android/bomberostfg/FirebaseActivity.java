package com.example.android.bomberostfg;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class FirebaseActivity {
    private static FirebaseActivity firebaseActivity=null;
    private DatabaseReference databaseFirebase=null;
    private Context mContext;
    public static FirebaseActivity getDatabase(Context pcontext) {
        if(firebaseActivity==null){
            firebaseActivity = new FirebaseActivity(pcontext);
        }
        return firebaseActivity;
    }
    public FirebaseActivity(Context pContext){
        mContext=pContext;
        databaseFirebase = FirebaseDatabase.getInstance().getReference();
    }



    public String insertarEmergencia(String tipo,int patrulla, String fecha) {
        HashMap<String,Object> lista = new HashMap<>();
        lista.put("Tipo", tipo);
        lista.put("Patrulla", patrulla);
        lista.put("Inicio", fecha);
        lista.put("Final", null);
        String key = databaseFirebase.child("emergencias").push().getKey();
        databaseFirebase.child("emergencias").child(key).setValue(lista);
        Utilidades.emergenciaKey.add(key);
        return key;
    }
    public String insertarOrden(int id,String nombre,Integer patrulla,String fecha, float latitud, float longitud) {
        //consigue la direccion con latitud y longitud
        String direccion="";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(Double.parseDouble(String.valueOf(latitud)),Double.parseDouble(String.valueOf(longitud)), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!list.isEmpty()) {
            String pueblo = list.get(0).toString().split("locality=")[1].split(",thoroughfare=")[0];
            String calle = list.get(0).toString().split(",thoroughfare=")[1].split(",postalCode=")[0];
            direccion=pueblo+" "+calle;
        }
        //Consigue fecha actual

        //Añadir argumentos
        HashMap<String,Object> lista = new HashMap<>();
        lista.put("id", id);
        lista.put("Patrulla", patrulla);
        lista.put("Nombre", nombre);
        lista.put("Direccion", direccion);
        lista.put("Inicio", fecha);
        lista.put("Fin", "");
        String key = databaseFirebase.child("ordenes").push().getKey();
        databaseFirebase.child("ordenes").child(key).setValue(lista);
        Utilidades.ordenMandada.add(new Orden(id, key, nombre, patrulla, direccion, fecha, ""));
        System.out.println("ORDENES: "+Utilidades.ordenMandada.toString());
        return key;
    }


    public void terminarEmergencia(String fecha,String key){
        databaseFirebase.child("emergencias").child(key).child("Final").setValue(fecha);
    }
    public void terminarOrden(String fecha,String key){
        databaseFirebase.child("ordenes").child(key).child("Fin").setValue(fecha);
        for(int i=0;i<Utilidades.ordenMandada.size();i++) {
            if(Utilidades.ordenMandada.get(i).getKey().equals(key)){
                Utilidades.ordenMandada.get(i).setFin(fecha);
            }
        }
    }
}
