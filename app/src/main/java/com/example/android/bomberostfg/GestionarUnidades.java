package com.example.android.bomberostfg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class GestionarUnidades extends AppCompatActivity {
    private int idActuacion;
    private BaseDeDatos database;
    private String selectedItem;
    private AdaptadorPatrullas adapterPatrullas;
    private ArrayAdapter<String> adapterSeleccion;
    private ArrayList<String> listaPatrullas = new ArrayList<>();
    private ArrayList<String> listaSeleccion = new ArrayList<>();
    ListView listViewPatrullas;
    ListView listViewSeleccion;
    private static int ONCLICK=10;
    private ArrayList<Patrulla> lista;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestionar_unidades);
        lista = Utilidades.patrullas;
        lista.sort(new Comparator<Patrulla>() {
            @Override
            public int compare(Patrulla patrulla, Patrulla t1) {
                Float distancia = calcularDistanciaMinima(patrulla.getId());
                Float distancia2 = calcularDistanciaMinima(t1.getId());
                return distancia.compareTo(distancia2);
            }
        });
        Intent intent = getIntent();
        idActuacion = intent.getIntExtra("id",0);
        String nombre = intent.getStringExtra("nombre");
        TextView nombreText = (TextView) findViewById(R.id.nombreActuacion);
        nombreText.setText(nombre);
        TextView idText =(TextView) findViewById(R.id.idActuacion);
        idText.setText("ID: "+idActuacion);
         this.setTitle("");
        System.out.println(lista.toString());
        listViewPatrullas = (ListView) findViewById(R.id.listPatrullas);
        adapterPatrullas=new AdaptadorPatrullas(getApplicationContext(), lista);
        listViewPatrullas.setAdapter(adapterPatrullas);
    }

    private Float calcularDistanciaMinima ( int patrulla){
        float[]  latitudUnidadCercana = new float[Utilidades.patrullas.size()];
        float[]  longitudUnidadCercana = new float[Utilidades.patrullas.size()];
        int linea =0;
        Location locationActuacion = new Location("Actuacion");
        locationActuacion.setLatitude(Utilidades.actuacionFragment.latitude);
        locationActuacion.setLongitude(Utilidades.actuacionFragment.longitude);
        float minDistance = 999999999;
        Location locationUnidad = new Location("Unidad");
        for (int j = 0; j < Utilidades.unidades.size(); j++) {
            if (Utilidades.unidades.get(j).getPatrulla() == patrulla) {
                locationUnidad.setLatitude(Utilidades.unidades.get(j).getLatitud());
                locationUnidad.setLongitude(Utilidades.unidades.get(j).getLongitud());

                Float distance = locationActuacion.distanceTo(locationUnidad);
                if (distance < minDistance) {
                    latitudUnidadCercana[linea] = Utilidades.unidades.get(j).getLatitud();
                    longitudUnidadCercana[linea] = Utilidades.unidades.get(j).getLongitud();
                    linea++;
                    minDistance = distance;
                }
            }
        }
        minDistance = minDistance/1000;
        return minDistance;
    }

    @Override
    protected void onDestroy() {
        Intent intent = getIntent();
        setResult(Activity.RESULT_OK, intent);
        super.onDestroy();
    }
}
