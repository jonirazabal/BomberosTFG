package com.example.android.bomberostfg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
    private String currentPhotoPath;
    private ImageView foto;
    private Spinner especialidadSpinner;
    private Spinner patrullaSpinner;
    private int positionUnidad = -1;

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
        System.out.println("ID: "+mcptt_id);

        BaseDeDatos database = BaseDeDatos.getDatabase(this);

        nombreEdit = (EditText) findViewById(R.id.editNombre);
        Button btnActualizar = (Button) findViewById(R.id.btnActualizar);
        TextView idUsuario = (TextView) findViewById(R.id.mcptt_id);
        idUsuario.setText(mcptt_id);
        especialidadSpinner = (Spinner) findViewById(R.id.especialidadSpinner);
        patrullaSpinner = (Spinner) findViewById(R.id.patrullaSpinner);
        foto = (ImageView) findViewById(R.id.fotoDePerfil);
        foto.setEnabled(false);
        foto.setVisibility(View.INVISIBLE);
        if(registered){
            especialidadSpinner.setEnabled(false);
            patrullaSpinner.setEnabled(false);
            nombreEdit.setEnabled(false);
            btnActualizar.setVisibility(View.INVISIBLE);
            btnActualizar.setEnabled(false);
        }

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sacar foto", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });

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
                selectedEspecialidad = adapterView.getItemAtPosition(0).toString();
            }
        });

        ArrayAdapter adapterPatrulla = new ArrayAdapter(this, android.R.layout.simple_spinner_item, patrullas);
        adapterPatrulla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patrullaSpinner.setAdapter(adapterPatrulla);
        System.out.println("SIZE: "+Utilidades.unidades.size());

        for(int i=0;i<Utilidades.unidades.size();i++) {
            if (Utilidades.unidades.get(i).getMcptt_id().equals(mcptt_id)) {
                positionUnidad = i;
                break;
            }
        }
        if(positionUnidad!=-1) {
            if (!Utilidades.unidades.get(positionUnidad).getFoto().isEmpty()) {
                System.out.println("TIENE FOTO");
                Bitmap bitmap = stringToBitmap(Utilidades.unidades.get(positionUnidad).getFoto());
                if (bitmap != null) {
                    foto.setImageBitmap(bitmap);
                }
            }

            especialidadSpinner.post(new Runnable() {
                @Override
                public void run() {
                    especialidadSpinner.setSelection(especialidades.indexOf(Utilidades.unidades.get(positionUnidad).getEspecialidad()));
                }
            });
            patrullaSpinner.post(new Runnable() {
                @Override
                public void run() {
                    patrullaSpinner.setSelection(patrullas.indexOf(String.valueOf(Utilidades.unidades.get(positionUnidad).getPatrulla())));
                }
            });
            nombreEdit.setText(Utilidades.unidades.get(positionUnidad).getNombre());
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
                            database.execute("insertarUnidad.php", mcptt_id, nombreEdit.getText().toString(), selectedEspecialidad, selectedPatrulla, String.valueOf(latitud), String.valueOf(longitud));
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
                                    database.execute("actualizarPatrulla.php", selectedPatrulla, selectedEspecialidad,String.valueOf(Utilidades.patrullas.get(i).isOperativa()));
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
       // if(registered){
            super.onBackPressed();
       // }
       // else{
       //     Toast.makeText(getApplicationContext(),"Tienes que registrarte", Toast.LENGTH_SHORT).show();
       // }
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Bomberos/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("CurrentPhotoPath: "+ currentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println("Current Photo Path: "+currentPhotoPath);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK){
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth()/2, myBitmap.getHeight()/2, true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                String fotoString = bitmapToString(rotatedBitmap);
                BaseDeDatos database = new BaseDeDatos(this);
                database.execute("actualizarUnidad.php",mcptt_id,fotoString);
                foto.setImageBitmap(rotatedBitmap);
                galleryAddPic();
            }
        }
    }
    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
    private Bitmap stringToBitmap(String fotoCode){
        try{
            byte [] encodeByte=Base64.decode(fotoCode,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
        }
        return null;
    }
}
