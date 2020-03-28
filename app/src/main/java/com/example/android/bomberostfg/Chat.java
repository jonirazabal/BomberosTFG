package com.example.android.bomberostfg;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.android.bomberostfg.preference.PreferencesManagerDefault;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

public class Chat extends AppCompatActivity {
    private BaseDeDatos database;
    private FirebaseDatabase firebaseDatabase;
    private EditText mensajeEdit;
    private ImageView camaraImage;
    private Button enviarButton;
    private LinearLayout chatLayout;
    private static final int CAMERA_PERMISSION = 81;
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String mcptt_id="";
    private int patrulla=0;
    private ImageView fotoView;
    private fotoGrande fotograndeDialog;
    private Bitmap imageBitmap;
    private View mensajeTextoLayout;
    private ScrollView scroll;
    private Mensaje mensaje;
    private PreferencesManagerDefault preferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
        EscucharFirebase escucharFirebase = new EscucharFirebase();
        escucharFirebase.execute();
        preferencesManager = new PreferencesManagerDefault();
        mensajeEdit = (EditText) findViewById(R.id.mensajeEdit);
        loadConfiguration();

        scroll = (ScrollView) findViewById(R.id.scrollView);
        camaraImage = (ImageView) findViewById(R.id.cameraImage);
        enviarButton = (Button) findViewById(R.id.enviarMensaje);
        Intent intent = getIntent();
        mcptt_id = intent.getStringExtra("mcptt_id");
        patrulla = intent.getIntExtra("patrulla",0);


        camaraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION);
                }
            }
        });
        mensajeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mensajeEdit.setText("");
            }
        });

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fecha = dateFormat.format(date);
                if(!mensajeEdit.getText().toString().equals("")) {
                    crearMensajeUI(mcptt_id, null, mensajeEdit.getText().toString(), fecha);
                    FirebaseActivity.getDatabase(getApplicationContext()).insertarMensaje(mcptt_id, patrulla, "texto", mensajeEdit.getText().toString());
                    mensajeEdit.setText("");
                }
            }
        });
        System.out.println("MENSAJES SIZE: "+Utilidades.mensajes.size());
        for(int i=0;i<Utilidades.mensajes.size();i++){
            System.out.println("KEYS: "+Utilidades.mensajes.get(i).getKey());
            if(Utilidades.mensajes.get(i).getTipo().equals("texto")){
                crearMensajeUI(Utilidades.mensajes.get(i).getMcptt_id(), null, Utilidades.mensajes.get(i).getMensaje(), Utilidades.mensajes.get(i).getFecha());
            }
            else {
                String filePath = Utilidades.mensajes.get(i).getMensaje();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                crearMensajeUI(Utilidades.mensajes.get(i).getMcptt_id(), bitmap, "", Utilidades.mensajes.get(i).getFecha());
            }
        }
        scrollHaciaAbajo();
    }


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

    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println("Current Photo Path: "+currentPhotoPath);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Foto guardada", Toast.LENGTH_SHORT).show();
                Bitmap imageBitmap = setPic();
                galleryAddPic();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fecha = dateFormat.format(date);
                FirebaseActivity.getDatabase(this).insertarMensaje(mcptt_id,patrulla,"imagen",bitmapToString(imageBitmap));
                //crearMensajeUI(mcptt_id, imageBitmap, "", fecha);

            }
        }
        if (requestCode == CAMERA_PERMISSION) {
            if(resultCode == RESULT_OK) {
                dispatchTakePictureIntent();
            }
        }
    }
    public void crearMensajeUI(String idMensaje, Bitmap pimageBitmap,String mensaje, String fechaString){
        String nombreMensaje="";
        for(int i=0;i<Utilidades.unidades.size();i++){
            if(Utilidades.unidades.get(i).getMcptt_id().equals(idMensaje)){
                nombreMensaje = Utilidades.unidades.get(i).getNombre();
            }
        }
        boolean esMiMensaje = false;
        if(idMensaje.equals(mcptt_id)) esMiMensaje=true;

        imageBitmap = pimageBitmap;
        if(imageBitmap!=null) {
            View mensajeImagenLayout = LayoutInflater.from(this).inflate(R.layout.mensaje,null);
            LinearLayout colorlayout = (LinearLayout) mensajeImagenLayout.findViewById(R.id.colorLayoutImagen);
            TextView nombreText = (TextView) mensajeImagenLayout.findViewById(R.id.nombreImagen);
            fotoView = (ImageView) mensajeImagenLayout.findViewById(R.id.fotoImagen);
            fotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imagen = (ImageView) view;
                    Bitmap bm=((BitmapDrawable)imagen.getDrawable()).getBitmap();
                    if(fotograndeDialog == null || fotograndeDialog.getDialog() == null || !fotograndeDialog.getDialog().isShowing()){

                        // Si estas en un activity
                        FragmentManager fm = getSupportFragmentManager();

                        Bundle arguments = new Bundle();

                        // Aqui le pasas el bitmap de la imagen
                        arguments.putParcelable("foto", bm);
                        fotograndeDialog = fotoGrande.newInstance(arguments);
                        fotograndeDialog.show(fm, "fotogrande");
                    }
                }
            });
                TextView fechaText = (TextView) mensajeImagenLayout.findViewById(R.id.fechaImagen);
                nombreText.setText(nombreMensaje);
                fotoView.setImageBitmap(imageBitmap);
                fechaText.setText(fechaString);
                if (esMiMensaje) {
                    colorlayout.setBackground(getResources().getDrawable(R.drawable.shapemio, null));
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) colorlayout.getLayoutParams();
                    params.weight = 1.0f;
                    params.gravity = Gravity.END;
                    colorlayout.setLayoutParams(params);

                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) colorlayout.getLayoutParams();
                    params.weight = 1.0f;
                    params.gravity = Gravity.START;
                    colorlayout.setLayoutParams(params);

                }
                chatLayout.addView(mensajeImagenLayout);

            }
            else if(!mensaje.isEmpty()){
                mensajeTextoLayout = LayoutInflater.from(this).inflate(R.layout.mensaje_texto,null);
                LinearLayout layout = (LinearLayout) mensajeTextoLayout.findViewById(R.id.mensajeTextoLinear);
                LinearLayout colorlayout = (LinearLayout) mensajeTextoLayout.findViewById(R.id.colorLayoutTexto);
                TextView nombreText = (TextView) mensajeTextoLayout.findViewById(R.id.nombreMensaje);
                TextView mensajeText = (TextView) mensajeTextoLayout.findViewById(R.id.mensajeMensaje);
                TextView fechaText = (TextView) mensajeTextoLayout.findViewById(R.id.fechaMensaje);
                nombreText.setText(nombreMensaje);
                mensajeText.setText(mensaje);
                fechaText.setText(fechaString);
                LinearLayout.LayoutParams params;
                if (esMiMensaje) {
                    colorlayout.setBackground(getResources().getDrawable(R.drawable.shapemio, null));
                    params = (LinearLayout.LayoutParams) colorlayout.getLayoutParams();
                    params.weight = 1.0f;
                    params.gravity = Gravity.END;
                    colorlayout.setLayoutParams(params);

                } else {
                    params = (LinearLayout.LayoutParams) colorlayout.getLayoutParams();
                    params.weight = 1.0f;
                    params.gravity = Gravity.START;
                    colorlayout.setLayoutParams(params);

                }
                chatLayout.addView(mensajeTextoLayout);
                scrollHaciaAbajo();
            }

    }

    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("IMAGE SIZE: "+encoded.length());
        return encoded;
    }
    private Bitmap stringToBitmap(String fotoCode){
        try{
            byte [] encodeByte=Base64.decode(fotoCode,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
        }
        return null;
    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = 10;
        int targetH = 10;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        if (bitmap != null) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void scrollHaciaAbajo(){
        scroll.post(new Runnable() {
            public void run() {
                scroll.fullScroll(scroll.FOCUS_DOWN);
            }
        });
    }
    private void loadConfiguration() {
        if (preferencesManager != null) {
            String mensaje = preferencesManager.getString(this, "mensaje", "");
            if(!mensaje.equals("")) mensajeEdit.setText(mensaje);
        }
    }
    @Override
    protected void onDestroy() {
        if (preferencesManager != null) {
            if (!mensajeEdit.getText().toString().equals("")) {
                preferencesManager.putString(this, "mensaje", mensajeEdit.getText().toString());
            }
        }
        super.onDestroy();
    }

    public class EscucharFirebase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("mensajes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        String nombreMensaje = "";
                        String key = ds.getKey();
                        String idMensaje = String.valueOf(map.get("mcptt_id"));
                        String tipo = String.valueOf(map.get("Tipo"));
                        Integer patrullaMensaje = Integer.parseInt(String.valueOf(map.get("Patrulla")));
                        String mensajeString = String.valueOf(map.get("Mensaje"));
                        String fecha = String.valueOf(map.get("Fecha"));
                        boolean añadir=true;
                        if (patrullaMensaje == patrulla) {
                            mensaje= new Mensaje(key, idMensaje, patrullaMensaje, fecha, tipo, mensajeString);
                            if(Utilidades.mensajes.size()==0){
                                if (tipo.equals("texto") ){
                                    crearMensajeUI(idMensaje, null, mensajeString, fecha);
                                } else {
                                    crearMensajeUI(idMensaje, stringToBitmap(mensajeString), mensajeString, fecha);
                                    try {
                                        Bitmap bitmap = stringToBitmap(mensajeString);
                                        File f = createImageFile();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(f));
                                        galleryAddPic();
                                        mensaje.setMensaje(currentPhotoPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Utilidades.mensajes.add(mensaje);
                            }
                            else {
                                for (int i = 0; i < Utilidades.mensajes.size(); i++) {
                                    if (Utilidades.mensajes.get(i).getKey().equals(key)) {
                                        añadir = false;
                                        break;
                                    }
                                }
                                if(añadir) {
                                    System.out.println("Añadido: " +mensaje.getKey());
                                    if (tipo.equals("texto")) {
                                        crearMensajeUI(idMensaje, null, mensajeString, fecha);
                                    } else {
                                        crearMensajeUI(idMensaje, stringToBitmap(mensajeString), mensajeString, fecha);
                                        try {
                                            Bitmap bitmap = stringToBitmap(mensajeString);
                                            File f = createImageFile();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(f));
                                            galleryAddPic();
                                            mensaje.setMensaje(currentPhotoPath);
                                            scrollHaciaAbajo();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Utilidades.mensajes.add(mensaje);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("BOMBEROS", "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(getApplicationContext(), "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });

        return null;
        }
    }


}
