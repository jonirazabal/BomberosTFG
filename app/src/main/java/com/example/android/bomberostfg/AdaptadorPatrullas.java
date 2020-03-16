package com.example.android.bomberostfg;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AdaptadorPatrullas extends ArrayAdapter<Patrulla> implements View.OnClickListener {
        private Context context;
        private ArrayList<Patrulla> lista;
        private boolean enviado;
        private View row;
        private View view;
        private Button enviarPatrulla;
        private Button[] button;
        private float[] latitudUnidadCercana;
        private float[] longitudUnidadCercana;
        private int linea=0;
        private String key="";
        private TextView patrulla;
    public AdaptadorPatrullas(@NonNull Context pContext, ArrayList<Patrulla> pLista){
        super(pContext,R.layout.patrulla_item, pLista);
        context = pContext;
        lista = pLista;
        button = new Button[pLista.size()];
        latitudUnidadCercana = new float[pLista.size()];
        longitudUnidadCercana = new float[pLista.size()];

    }

        @Override
        public int getCount () {
        return lista.size();
    }


        @Override
        public long getItemId ( int i){
        return 0;
    }

        @Override
        public View getView (int i, View convertView, final ViewGroup viewGroup){
        linea = i;
        row = convertView;
        System.out.println(lista.toString());
        Patrulla patrullaItem = lista.get(i);
            if(row == null){

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.patrulla_item, null);

            }
        //row = LayoutInflater.from(context).inflate(R.layout.patrulla_item, null);
        patrulla = (TextView) row.findViewById(R.id.patrullaItem);
        patrulla.setText(String.valueOf(patrullaItem.getId()));
        TextView incendios = (TextView) row.findViewById(R.id.incendiosItem);
        TextView salvamentos = (TextView) row.findViewById(R.id.salvamentosItem);
        TextView asistencias = (TextView) row.findViewById(R.id.asistenciasItem);
        TextView distancia = (TextView) row.findViewById(R.id.distanciaMin);
        final ImageView audio = (ImageView) row.findViewById(R.id.audioPatrulla);
        incendios.setVisibility(View.INVISIBLE);
        salvamentos.setVisibility(View.INVISIBLE);
        asistencias.setVisibility(View.INVISIBLE);
        if (patrullaItem.isIncendio()==1) {
            incendios.setVisibility(View.VISIBLE);
            if(Utilidades.selectedActuacion.getTipo().equals("Incendio")){
                incendios.setTextColor(Color.GREEN);
            }
        }
        if (patrullaItem.isSalvamento()==1) {
            salvamentos.setVisibility(View.VISIBLE);
            if(Utilidades.selectedActuacion.getTipo().equals("Salvamento")){
                salvamentos.setTextColor(Color.GREEN);
            }
        }
        if (patrullaItem.isAsistencia()==1) {
            asistencias.setVisibility(View.VISIBLE);
            if(Utilidades.selectedActuacion.getTipo().equals("Asistencia")){
                asistencias.setTextColor(Color.GREEN);
            }
        }

        float minDistance = calcularDistanciaMinima(Integer.parseInt(patrulla.getText().toString()));
        distancia.setText(String.format("%.2f", minDistance)+" Km");

        enviarPatrulla = (Button) row.findViewById(R.id.enviarPatrulla);
        audio.setTag(patrullaItem.getId());
        audio.setVisibility(View.VISIBLE);
        enviarPatrulla.setTag(patrulla.getText().toString());
        button[i] = enviarPatrulla;
        System.out.println("ORDENES: "+Utilidades.ordenMandada.toString());
        for(int ordenes=0;ordenes<Utilidades.ordenMandada.size();ordenes++){
            if(Integer.valueOf(patrulla.getText().toString()) == Utilidades.ordenMandada.get(ordenes).getPatrulla() &&
            Utilidades.ordenMandada.get(ordenes).getId() == Utilidades.selectedActuacion.getId() && Utilidades.ordenMandada.get(ordenes).getFin().equals("") ){
                enviarPatrulla.setText("Cancelar");
                enviarPatrulla.setEnabled(true);
                row.setBackgroundColor(Color.GRAY);
            }
            else if(Integer.valueOf(patrulla.getText().toString()) == Utilidades.ordenMandada.get(ordenes).getPatrulla() &&
                    Utilidades.ordenMandada.get(ordenes).getId() != Utilidades.selectedActuacion.getId() && Utilidades.ordenMandada.get(ordenes).getFin().equals("") ){
                        enviarPatrulla.setText("Operativa");
                        enviarPatrulla.setEnabled(false);
                        row.setBackgroundColor(Color.RED);
            }
            else if(Integer.valueOf(patrulla.getText().toString()) != Utilidades.ordenMandada.get(ordenes).getPatrulla() &&
                Utilidades.ordenMandada.get(ordenes).getId() != Utilidades.selectedActuacion.getId() && Utilidades.ordenMandada.get(ordenes).getFin().equals("") ){
                enviarPatrulla.setText("Enviar");
                row.setBackgroundColor(Color.WHITE);
                enviarPatrulla.setEnabled(true);
            }
            else {
                enviarPatrulla.setText("Enviar");
                enviarPatrulla.setEnabled(true);
                row.setBackgroundColor(Color.WHITE);
            }
        }
        enviarPatrulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fecha = dateFormat.format(date);
                LinearLayout parent = (LinearLayout) v.getParent();
                Button cambiar = (Button) v;
                String operativa="";
                if (cambiar.getText().toString().equals("Cancelar")) {

                    //terminar orden la cual se elimina de Utilidades
                    for(int ordenes=0;ordenes<Utilidades.ordenMandada.size();ordenes++){
                        if(Integer.valueOf(v.getTag().toString()) == Utilidades.ordenMandada.get(ordenes).getPatrulla() &&
                                Utilidades.ordenMandada.get(ordenes).getId() == Utilidades.selectedActuacion.getId() && Utilidades.ordenMandada.get(ordenes).getFin().equals("")){
                            FirebaseActivity.getDatabase(getContext()).terminarOrden(fecha,Utilidades.ordenMandada.get(ordenes).getKey());
                        }
                    }
                    BaseDeDatos database = new BaseDeDatos(getContext());
                    operativa = "0";
                    database.execute("actualizarPatrulla.php",patrulla.getText().toString(),"", operativa);
                    cambiar.setText("Enviar");
                    parent.setBackgroundColor(Color.WHITE);
                } else if(cambiar.getText().toString().equals("Enviar")) {
                    FirebaseActivity.getDatabase(getContext()).insertarOrden(Utilidades.selectedActuacion.getId(),Utilidades.selectedActuacion.getNombre() ,Integer.valueOf(v.getTag().toString()),
                            fecha, Utilidades.selectedActuacion.getLatitud(),Utilidades.selectedActuacion.getLongitud());
                    BaseDeDatos database = new BaseDeDatos(getContext());
                    operativa = "1";
                    database.execute("actualizarPatrulla.php",patrulla.getText().toString(),"", operativa);
                    cambiar.setText("Cancelar");
                    parent.setBackgroundColor(Color.GRAY);
                }
            }
        });


        return row;
    }


        private float calcularDistanciaMinima ( int patrulla){
        Location locationActuacion = new Location("Actuacion");
        locationActuacion.setLatitude(Utilidades.actuacionFragment.latitude);
        locationActuacion.setLongitude(Utilidades.actuacionFragment.longitude);
        float minDistance = 999999999;
            Location locationUnidad = new Location("Unidad");
            for (int j = 0; j < Utilidades.unidades.size(); j++) {
            if (Utilidades.unidades.get(j).getPatrulla() == patrulla) {
                locationUnidad.setLatitude(Utilidades.unidades.get(j).getLatitud());
                locationUnidad.setLongitude(Utilidades.unidades.get(j).getLongitud());

                float distance = locationActuacion.distanceTo(locationUnidad);
                if (distance < minDistance) {
                    latitudUnidadCercana[linea] = Utilidades.unidades.get(j).getLatitud();
                    longitudUnidadCercana[linea] = Utilidades.unidades.get(j).getLongitud();
                    minDistance = distance;
                }
            }
        }
            minDistance = minDistance/1000;
            return minDistance;
    }


    @Override
    public void onClick(View v) {

    }
}
