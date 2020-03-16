package com.example.android.bomberostfg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroUsuario extends AppCompatActivity {
    private String mcptt_id="";
    private EditText nombreEdit;
    private String selectedPatrulla="";
    private String selectedEspecialidad="";
    private ArrayList<String> especialidades = new ArrayList<>();
    private ArrayList<String> patrullas = new ArrayList<>();
    private boolean registered;
    private float latitud,longitud=0;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_usuarios);
        intent = getIntent();
        latitud = intent.getFloatExtra("latitud",0);
        longitud = intent.getFloatExtra("longitud",0);
        registered = intent.getBooleanExtra("registered", false);
        System.out.println("esta Registrado = "+registered);
        mcptt_id = intent.getStringExtra("id");

        BaseDeDatos database = BaseDeDatos.getDatabase(this);

        nombreEdit = (EditText) findViewById(R.id.editNombre);
        Button btnActualizar = (Button) findViewById(R.id.btnActualizar);
        TextView idUsuario = (TextView) findViewById(R.id.mcptt_id);
        idUsuario.setText(mcptt_id);
        final Spinner especialidadSpinner = (Spinner) findViewById(R.id.especialidadSpinner);
        Spinner patrullaSpinner = (Spinner) findViewById(R.id.patrullaSpinner);
        if(registered){
            especialidadSpinner.setEnabled(false);
            patrullaSpinner.setEnabled(false);
        }
        else{
            especialidadSpinner.setEnabled(true);
            patrullaSpinner.setEnabled(true);
        }


        for(int i=111;i<=118;i++){
            patrullas.add(String.valueOf(i));
        }
        especialidades.add("Incendios");
        especialidades.add("Salvamentos");
        especialidades.add("Asistencias");
        selectedEspecialidad = especialidades.get(0);
        selectedPatrulla = patrullas.get(0);


        ArrayAdapter adapterEspecialidad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, especialidades);
        adapterEspecialidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especialidadSpinner.setAdapter(adapterEspecialidad);

        especialidadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEspecialidad = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedEspecialidad = especialidades.get(0);

            }
        });

        ArrayAdapter adapterPatrulla = new ArrayAdapter(this, android.R.layout.simple_spinner_item, patrullas);
        adapterPatrulla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patrullaSpinner.setAdapter(adapterPatrulla);

        for(int i=0;i<Utilidades.unidades.size();i++) {
            if (Utilidades.unidades.get(i).getMcptt_id().equals(mcptt_id)){
                if(Utilidades.unidades.get(i).getEspecialidad()!=null) {
                    if (Utilidades.unidades.get(i).getEspecialidad().equals("Incendios")) {
                        especialidadSpinner.setSelection(0);
                    } else if (Utilidades.unidades.get(i).getEspecialidad().equals("Salvamentos")) {
                        especialidadSpinner.setSelection(1);
                    } else if (Utilidades.unidades.get(i).getEspecialidad().equals("Asistencias")) {
                        especialidadSpinner.setSelection(2);
                    }
                }
                if(Utilidades.unidades.get(i).getPatrulla()!=0) {
                    patrullaSpinner.setSelection(patrullas.indexOf(Utilidades.unidades.get(i).getPatrulla()));
                }
                if(Utilidades.unidades.get(i).getNombre()!=null) {
                    nombreEdit.setText(Utilidades.unidades.get(i).getNombre());
                }
            }
        }

        patrullaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPatrulla = patrullas.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedPatrulla = patrullas.get(0);
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BaseDeDatos database = new BaseDeDatos(getApplicationContext());
                    if(!registered) {
                        if(!mcptt_id.isEmpty() && !nombreEdit.getText().toString().isEmpty() && !selectedEspecialidad.isEmpty() && !selectedPatrulla.isEmpty()) {
                            database.execute("insertarUnidad.php", mcptt_id, nombreEdit.getText().toString(), selectedPatrulla, selectedEspecialidad, String.valueOf(latitud), String.valueOf(longitud));
                            //Actualizar las unidades
                            database = new BaseDeDatos(getApplicationContext());
                            database.execute("selectUnidades.php");
                            //Selecciona todas las patrullas
                            database = new BaseDeDatos(getApplicationContext());
                            database.execute("selectPatrullas.php");
                            boolean existePatrulla = false;
                            //Mirar si la patrulla existe en la base de datos
                            for(int i=0;i<Utilidades.patrullas.size();i++) {
                                if (Utilidades.patrullas.get(i).getId()==Integer.parseInt(selectedPatrulla)){
                                    System.out.println("Patrulla encontrada");
                                    database = new BaseDeDatos(getApplicationContext());
                                    database.execute("actualizarPatrulla.php", selectedPatrulla, selectedEspecialidad);
                                    existePatrulla=true;
                                    break;
                                }
                            }
                            if(!existePatrulla){
                                database = new BaseDeDatos(getApplicationContext());
                                database.execute("insertarPatrulla.php", selectedPatrulla, selectedEspecialidad, "0");

                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Rellena todos los campos", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Para actualizar datos, pongase en contacto con la central", Toast.LENGTH_LONG).show();

                    }
                intent.putExtra("registered", true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(registered){
            super.onBackPressed();
        }
        else{
            Toast.makeText(getApplicationContext(),"Tienes que registrarte", Toast.LENGTH_SHORT).show();
        }
    }
}
