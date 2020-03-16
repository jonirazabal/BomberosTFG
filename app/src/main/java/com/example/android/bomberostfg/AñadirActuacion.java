package com.example.android.bomberostfg;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AñadirActuacion extends AppCompatActivity {
    private Spinner spinnerActuacion;
    private Spinner spinnerSituacion;
    private ArrayList<String> incendios;
    private ArrayList<String> salvamentos;
    private ArrayList<String> asistencia;
    private ArrayList<String> situacion;
    private EditText latitudEdit;
    private EditText longitudEdit;
    private EditText localidad;
    private EditText calle;
    private EditText numero;
    private Button crear;
    private String actuacionElegida;
    private BaseDeDatos database;
    float latitud=0, longitud=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actuaciones);
        database = BaseDeDatos.getDatabase(this);
        spinnerActuacion = (Spinner) findViewById(R.id.spinnerActuaciones);
        spinnerSituacion = (Spinner) findViewById(R.id.spinnerSituacion);
        latitudEdit = (EditText) findViewById(R.id.latitudEdit);
        longitudEdit = (EditText) findViewById(R.id.longitudEdit);
        localidad = (EditText) findViewById(R.id.localidadEdit);
        calle = (EditText) findViewById(R.id.calleEdit);
        numero = (EditText) findViewById(R.id.numeroEdit);
        crear = (Button) findViewById(R.id.crearButton);
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!actuacionElegida.isEmpty()) {
                    if (!localidad.getText().toString().matches("") && !calle.getText().toString().matches("")) {
                        LatLng location = getLocationFromAddress(localidad.getText().toString()+" "+calle.getText().toString()+" "+numero.getText().toString());
                        latitud = (float) location.latitude;
                        longitud = (float) location.longitude;
                        database = new BaseDeDatos(getApplicationContext());
                        database.execute("insertarActuacion.php",spinnerSituacion.getSelectedItem().toString(), spinnerActuacion.getSelectedItem().toString(),String.valueOf(latitud),String.valueOf(longitud));
                        database = new BaseDeDatos(getApplicationContext());
                        database.execute("selectActuaciones.php");

                    }
                    else if(!latitudEdit.getText().toString().matches("") && !longitudEdit.getText().toString().matches("")
                                && (Float.compare(latitud,90)<0 && Float.compare(latitud,-90)>0) &&
                            (Float.compare(longitud,180)<0 && Float.compare(longitud,-180)>0)){
                        latitud = Float.valueOf(latitudEdit.getText().toString());
                        longitud = Float.valueOf(longitudEdit.getText().toString());
                        database = new BaseDeDatos(getApplicationContext());
                        database.execute("insertarActuacion.php",spinnerSituacion.getSelectedItem().toString(),spinnerActuacion.getSelectedItem().toString(),latitudEdit.getText().toString(),longitudEdit.getText().toString());
                    }
                }
                finish();
            }
        });

        situacion = new ArrayList<>();
        situacion.add("Incendio");
        situacion.add("Salvamento");
        situacion.add("Asistencia");
        ArrayAdapter<String> userAdaptor = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, situacion);
        // Drop down layout style - list view with radio button
        userAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSituacion.setAdapter(userAdaptor);
        spinnerActuacion.setEnabled(false);
        spinnerSituacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> elegido = new ArrayList<>();
                switch(i){
                    case 0:
                        elegido = incendios;
                        break;
                    case 1:
                        elegido = salvamentos;
                        break;
                    case 2:
                        elegido = asistencia;
                        break;
                }
                ArrayAdapter<String> userAdaptor = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, elegido);
                // Drop down layout style - list view with radio button
                userAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerActuacion.setAdapter(userAdaptor);
                spinnerActuacion.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerActuacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                actuacionElegida =  spinnerActuacion.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                actuacionElegida = spinnerActuacion.getItemAtPosition(0).toString();
            }
        });
        incendios = new ArrayList<>();
        incendios.add("Incendio Urbano");
        incendios.add("Incendio urbano de pequeñas dimensiones");
        incendios.add("Incendio urbano de vivienda");
        incendios.add("Incendio urbano de pequeño comercio");
        incendios.add("Incendio urbano de local público");
        incendios.add("Incendios en industrias y almacenes");
        incendios.add("Incendios industrial de pequeñas dimensiones nivel 2");
        incendios.add("Incendio industrial nivel 3");
        incendios.add("Gran incendio industrial nivel 4");
        incendios.add("Incendios rurales");
        incendios.add("Incendio forestal nivel 2");
        incendios.add("Incendio forestal nivel 3");
        incendios.add("Incendio rural nivel 2 bajo riesgo propagacion");
        incendios.add("Incendio rural nivel 3 con propagacion y/o victimas");

        salvamentos = new ArrayList<>();
        salvamentos.add("Salvamentos en accidentes de trafico");
        salvamentos.add("Accidente de tráfico con atrapados (I)");
        salvamentos.add("Accidente de tráfico con atrapados (II)");
        salvamentos.add("Accidente con MM.PP");
        salvamentos.add("Rescates y salvamentos con víctimas");
        salvamentos.add("Acceso a viviendas cerradas con riesgo de incendio (III)");
        salvamentos.add("Acceso a viviendas cerradas con personas en riesgo (II)");
        salvamentos.add("Rescates urbanos");
        salvamentos.add("Amenaza de suicidio por salto al vacio");
        salvamentos.add("Amenaza de suicidio violento");
        salvamentos.add("Rescates rurales");
        salvamentos.add("Rescate rural sin tecnicas especiales / animales (I)");
        salvamentos.add("Rescates proximos a vias comunicacion (II)");
        salvamentos.add("Rescates requieren tecnicas especiales (III)");
        salvamentos.add("Rescates varias victimas / Condiciones penosas (IV)");
        salvamentos.add("Servicios auxialiares y saneamientos");
        salvamentos.add("Acceso a viviendas cerradas sin riesgo (I)");
        salvamentos.add("Auxialiares (I)");
        salvamentos.add("Auxialiares (II)");
        salvamentos.add("Saneado de construcciones");

        asistencia = new ArrayList<>();
        asistencia.add("Asistencia técnica operativa");
        asistencia.add("Incidente con gas");
        asistencia.add("Posible fuga de gas");
        asistencia.add("Fuga de gas pequeña");
        asistencia.add("Fuga de gas inflamada");
        asistencia.add("Retenes de prevencion");
        asistencia.add("Suministro de agua");
        asistencia.add("Limpieza de calzada");
        asistencia.add("Realizacion de simulacros");
        asistencia.add("Visitas a riesgos concretos");
        asistencia.add("Investigacion");
        asistencia.add("Prevencion");
        asistencia.add("Visitas escolares");
        asistencia.add("Petronor");
        asistencia.add("Viento aviso amarillo");
        asistencia.add("Hielo/Nieve aviso amarillo");
        asistencia.add("Lluvia aviso amarillo");
        asistencia.add("Lluvia alerta naranja");
        asistencia.add("Maritimo-costero aviso amarillo");
        asistencia.add("Temp. extremas aviso amarillo");
        asistencia.add("Temp. extremas alerta naranja");
        asistencia.add("Notificacion SOS");
        asistencia.add("Peticion SOS");
        asistencia.add("Simulacion informatica");


    }
    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            System.out.println("Direccion: "+strAddress);
            System.out.println("Latitud = "+location.getLatitude());
            System.out.println("Longitud = "+location.getLongitude());

            p1 = new LatLng((location.getLatitude()),
                    (location.getLongitude()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }
}
