package com.example.android.bomberostfg;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.android.bomberostfg.datatype.UserData;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import com.example.android.bomberostfg.preference.PreferencesManagerDefault;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.mcopenplatform.muoapi.IMCOPCallback;
import org.mcopenplatform.muoapi.IMCOPsdk;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection mConnection = null;
    private boolean isConnect = false;
    private Intent serviceIntent;
    private MenuItem itemIdMSCSM;
    private MenuItem itemAutoReg;
    private MenuItem itemExit;
    private PreferencesManagerDefault preferencesManager;
    private ArrayList<String> groupsCurrent;
    private static final int DEFAULT_REGISTER_DELAY = 3000;
    private static final int AUTHETICATION_RESULT = 101;
    private static final boolean VALUE_BOOLEAN_DEFAULT = false;
    private static final int ERROR_CODE_DEFAULT = -1;
    private boolean autoRegister = false;
    private boolean registered = false;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder encenderUbicacion;
    private AlertDialog.Builder registroAutomatico;
    private AlertDialog.Builder terminarEmergencia;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int GET_PERMISSION = 60;
    private final static String TAG = "BOMBEROS";
    private static final int ACCESS_FINE_LOCATION = 61;
    private LocationManager locationManager;
    private static final int UBICACION_ACTIVADA = 90;
    private static final int CAMERA_RESULT = 80;
    private static final int CAMERA_PERMISSION = 81;
    private static final int MANDAR_FOTO = 66;
    private List<Boton> botones;
    private boolean isSpeakerphoneOn = false;
    private RecyclerViewAdapter adaptadorBotones;
    private IMCOPCallback mMCOPCallback;
    private IMCOPsdk mService;
    private boolean IdMSCMS = false;
    private Map<String, String> clients;
    private String currentProfile;
    private static final String PARAMETER_SAVE_PROFILE = "TAG.PARAMETER_SAVE_PROFILE";
    private static final String PARAMETER_CONFIG_IDMSCMS = "TAG.PARAMETER_CONFIG_IDMSCMS";
    private static final String PARAMETER_CONFIG_AUTOREGISTER = "TAG.PARAMETER_CONFIG_AUTOREGISTER";
    private CardView cortar;
    private CardView datos;
    private CardView altavoz;
    private CardView chat;
    private CardView emergencia;
    private CardView añadirActuacion;
    private CardView botonHablar;
    private CardView location;
    private TextView consola;
    private TextView status;
    private TextView switchGroup;
    private TextView switchPrivate;
    private Switch aSwitch;
    private Spinner spinnerUsers;
    private Spinner spinnerGroups;
    private com.example.android.bomberostfg.datatype.UserData userData;
    CardView registro;
    private FusedLocationProviderClient fusedLocationClient;
    private static TextView adress;
    private static TextView latitudText;
    private static TextView longitudText;
    private TextView altavozTexto;

    String pueblo, calle;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private BaseDeDatos database;
    private DialogMenu mDialogMenu;
    private String[] regConfig = {"IdMS/CMS", "None"};
    private String[] autoReg = {"Manual", "Automatic"};
    private DialogAlert mDialogAlert;
    private static final String PARAMETER_PROFILE = "parameters";
    private DialogMenu mDialogIds;
    private static final String ACTION_BUTTON_PTT_DOWN_BITTIUM = "com.elektrobit.pttbutton.PTTBUTTON_DOWN";
    private static final String ACTION_BUTTON_PTT_UP_BITTIUM = "com.elektrobit.pttbutton.PTTBUTTON_UP";
    private static final String ACTION_BUTTON_PTT_LONG_PRESS_BITTIUM = "com.elektrobit.pttbutton.PTTBUTTON_LONG_PRESS";
    private BroadcastReceiver mButtonPTTBroadCastRecvMCPTT;
    private ArrayList<Integer> imagenes = new ArrayList<>();
    private ArrayList<String> nombres = new ArrayList<>();
    private DatabaseReference mEmergenciaReference;
    private AudioManager mAudioManager;
    private Thread threadSpeakerOn;
    private String mensajeTotal= "";

    private enum State {
        GRANTED,
        IDLE,
        TAKEN,
        NONE
    }

    private State mState = State.NONE;

    private enum CallType {
        PRIVATE,
        GROUP
    }

    private CallType mCallType = CallType.GROUP;
    private boolean mERState = false;
    private String selGroup = "sip:jonira_group@organization.org";
    private String selUser = "sip:mcptt_id_jonira_B@organization.org";
    private RelativeLayout fondoPrincipal;
    private FirebaseActivity firebaseActivity;
    private boolean emergenciaEnCurso = false;
    public PendingIntent pendingIntent;
    private int patrulla;
    private String mcptt_id;
    private float latitud, longitud = 0;
    private int REGISTRO_RESULT = 99;
    private String nombre;
    private SharedPreferences sharedPreferences;
    private Mensaje mensaje;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("mensajes",Context.MODE_PRIVATE);
        this.setTitle("MCOP communication");
        firebaseActivity = FirebaseActivity.getDatabase(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setPermissions();
        database = new BaseDeDatos(getApplicationContext());
        database.execute("selectUnidades.php");
        database = new BaseDeDatos(getApplicationContext());
        database.execute("selectPatrullas.php");
        database = new BaseDeDatos(getApplicationContext());
        database.execute("selectActuaciones.php");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        empezarLocationServices();
        Toolbar toolbar = findViewById(R.id.toolbar);
        altavozTexto = (TextView) findViewById(R.id.textView3);
        setSupportActionBar(toolbar);
        loadConfiguration();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        cortar = (CardView) findViewById(R.id.cortarCard);
        datos = (CardView) findViewById(R.id.datosCard);
        altavoz = (CardView) findViewById(R.id.altavozCard);
        chat = (CardView) findViewById(R.id.chatCard);
        emergencia = (CardView) findViewById(R.id.emergenciaCard);
        location = (CardView) findViewById(R.id.locationCard);
        añadirActuacion = (CardView) findViewById(R.id.actuacionesCard);
        botonHablar = (CardView) findViewById(R.id.hablar);
        consola = (TextView) findViewById(R.id.consola);
        status = (TextView) findViewById(R.id.status);
        aSwitch = (Switch) findViewById(R.id.selector);
        registro = (CardView) findViewById(R.id.registroCard);
        latitudText = (TextView) findViewById(R.id.latitud);
        longitudText = (TextView) findViewById(R.id.longitud);
        adress = (TextView) findViewById(R.id.adress);
        switchGroup = (TextView) findViewById(R.id.group);
        switchPrivate = (TextView) findViewById(R.id.privatetext);
        fondoPrincipal = (RelativeLayout) findViewById(R.id.FondoPrincipal);
        botonHablar.setEnabled(false);
        spinnerUsers = (Spinner) findViewById(R.id.spinnerUsers);
        spinnerGroups = (Spinner) findViewById(R.id.spinnerGroups);

        registro.setEnabled(true);
        activarBotones(false);

        cortar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userData.getSessionIDs().size() > 0) {
                    try {
                        mService.hangUpCall(userData.getSessionIDs().get(userData.getSessionIDs().size() - 1));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        botonHablar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("mState", mState.toString());
                Log.d("eventAction", String.valueOf(event.getAction()));
                if (mState != State.NONE && event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mState == State.IDLE) {
                        //Request token
                        Log.d(TAG, "TOKEN REQUEST");
                        showIdsOperationFloorControl(getApplicationContext(), true);
                    }
                } else if (mState != State.NONE && event.getAction() == MotionEvent.ACTION_UP) {
                    if (mState == State.GRANTED) {
                        //Release token
                        Log.d(TAG, "TOKEN RELEASE");
                        showIdsOperationFloorControl(getApplicationContext(), false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    makeCall();
                }
                return true;
            }
        });

        datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registered) {
                    Intent i = new Intent(MainActivity.this, RegistroUsuario.class);
                    i.putExtra("id", mcptt_id);
                    i.putExtra("registered", registered);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "No estas registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (userData == null) userData = new UserData();

        nombres.add("Evacuación");
        imagenes.add(R.drawable.evacuacion);
        nombres.add("Inmobilización general");
        imagenes.add(R.drawable.stop);
        nombres.add("Informe de situación");
        imagenes.add(R.mipmap.ic_informe);


        spinnerUsers.setEnabled(true);
        spinnerUsers.setVisibility(View.VISIBLE);
        ArrayList<String> usersCurrent = new ArrayList<String>();
        usersCurrent.add("sip:mcptt_id_jonira_B@organization.org");
        usersCurrent.add("sip:mcptt_id_jonira_A@organization.org");
        usersCurrent.add("sip:mcptt_id_jonira_C@organization.org");

        // Group list
        // EDIT THIS LIST WITH THE PROVIDED GROUP NAME(s)
        groupsCurrent = new ArrayList<String>();
        groupsCurrent.add("sip:jonira_group@organization.org");
        // Adapter for User Spinner
        ArrayAdapter<String> userAdaptor = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, usersCurrent);
        // Drop down layout style - list view with radio button
        userAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(userAdaptor);
        loadGroups();

        ArrayList<String> strings = getIntent().getStringArrayListExtra(PARAMETER_PROFILE);
        Map<String, String> parameterClients = getProfilesParameters(strings);
        if (parameterClients != null && !parameterClients.isEmpty())
            clients = parameterClients;

        preferencesManager = new PreferencesManagerDefault();
        clients = new HashMap<>();

        // PTT button on Bittium Devices
        mButtonPTTBroadCastRecvMCPTT = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "PTT button");
                final String action = intent.getAction();
                if (action.compareTo(ACTION_BUTTON_PTT_DOWN_BITTIUM) == 0
                ) {

                    Log.d(TAG, "PTT button Down");
                    if (mState == State.IDLE && !showIdsOperationFloorControl(getApplicationContext(), true)) {
                        Log.e(TAG, "Error: the device can´t request the Token now");
                    } else if (mState == null || mState == State.NONE) {
                        makeCall();
                    }
                } else if (action.compareTo(ACTION_BUTTON_PTT_UP_BITTIUM) == 0 && mState == State.GRANTED) {
                    Log.d(TAG, "PTT button Up");
                    if (!showIdsOperationFloorControl(getApplicationContext(), false)) {
                        Log.e(TAG, "Error: the device can´t release the Token now");
                    }
                } else if (action.compareTo(ACTION_BUTTON_PTT_LONG_PRESS_BITTIUM) == 0) {
                    Log.d(TAG, "Long PTT button press");
                }
            }
        };
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(ACTION_BUTTON_PTT_DOWN_BITTIUM);
        intentFilter2.addAction(ACTION_BUTTON_PTT_UP_BITTIUM);
        intentFilter2.addAction(ACTION_BUTTON_PTT_LONG_PRESS_BITTIUM);

        if (mConnection == null) {
            mConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    Log.d(TAG, "Service binded! " + className.getPackageName() + "\n");
                    Toast.makeText(getApplicationContext(), "Binded to MCOP SDK", Toast.LENGTH_SHORT).show();
                    mService = IMCOPsdk.Stub.asInterface(service);

                    try {
                        Log.d(TAG, "execute registerCallback " + mMCOPCallback);
                        mService.registerCallback(mMCOPCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    isConnect = true;
                    Log.d("isconnect", "true");


                    // Auto Registration
                    if (autoRegister) {
                        Log.d("AutoRegister", "autoregister");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registro();
                            }
                        }, DEFAULT_REGISTER_DELAY);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName className) {
                    mService = null;
                    // This method is only invoked when the service quits from the other end or gets killed
                    // Invoking exit() from the AIDL interface makes the Service kill itself, thus invoking this.
                    Log.d(TAG, "Service disconnected.\n");
                    isConnect = false;
                }
            };
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String imei = tm.getDeviceId();
                String client = clients.get(imei);
                if (client != null) {
                    this.currentProfile = client;
                    Log.i(TAG, "currentProfile: " + currentProfile);
                    connectService(currentProfile);
                } else {
                    connectService();
                }
            }
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Switch is on. Private Call
                    mCallType = CallType.PRIVATE;
                    spinnerGroups.setVisibility((View.GONE));
                    spinnerUsers.setVisibility((View.VISIBLE));
                    spinnerGroups.setEnabled(false);
                    spinnerUsers.setEnabled(true);
                } else {
                    // Switch is off. Group Call
                    mCallType = CallType.GROUP;
                    spinnerUsers.setVisibility((View.INVISIBLE));
                    spinnerGroups.setVisibility((View.VISIBLE));
                    spinnerUsers.setEnabled(false);
                    spinnerGroups.setEnabled(true);
                }
            }
        });

        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selUser = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!registered) {
                    registro();
                    System.out.println("Registro clickado");
                }
            }
        });
        añadirActuacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AñadirActuacion.class);
                startActivity(i);
            }
        });
        mAudioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        threadSpeakerOn = new Thread() {
            @Override
            public void run() {
                        try {
                            sleep(1000);
                            System.out.println("STATUS: "+mAudioManager.isSpeakerphoneOn());
                            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                            if (!mAudioManager.isSpeakerphoneOn()){
                                altavozTexto.setText("Altavoz ON");
                                mAudioManager.setSpeakerphoneOn(true);
                                System.out.println("STATUS CHANGED: "+mAudioManager.isSpeakerphoneOn());
                            }
                            else{
                                altavozTexto.setText("Altavoz OFF");
                                mAudioManager.setSpeakerphoneOn(false);
                                System.out.println("STATUS CHANGED: "+mAudioManager.isSpeakerphoneOn());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
        };

        altavoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadSpeakerOn.run();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Chat.class);
                i.putExtra("mcptt_id", mcptt_id);
                i.putExtra("patrulla", patrulla);
                startActivity(i);
            }
        });
        emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilidades.emergenciaKey.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LinearLayout layoutLista = (LinearLayout) findViewById(R.id.layoutLista);
                    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    View row = getLayoutInflater().inflate(R.layout.emergencia_dialog, null);
                    final RecyclerView recyclerView = row.findViewById(R.id.emergenciasRecycler);

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(nombres, imagenes,patrulla, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(lm);
                    builder.setView(row);
                    Dialog dialog = builder.create();
                    dialog.getWindow().setLayout(200, 620);
                    dialog.show();
                } else {
                    final AlertDialog.Builder terminarEmergencia = new AlertDialog.Builder(MainActivity.this);
                    terminarEmergencia.setMessage("¿Terminar Emergencia?").setCancelable(false).setIcon(R.drawable.alarma)
                            .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    Date date = new Date();
                                    String fecha = dateFormat.format(date);
                                    FirebaseActivity.getDatabase(MainActivity.this).terminarEmergencia(fecha, Utilidades.emergenciaKey.get(0));
                                    Utilidades.emergenciaKey.clear();
                                }
                            }).setNegativeButton("No terminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    terminarEmergencia.create().show();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        activarGps();
                    } else {
                        Intent maps = new Intent(MainActivity.this, MapsActivity.class);
                        maps.putExtra("latitud", Float.valueOf(latitudText.getText().toString()));
                        maps.putExtra("longitud", Float.valueOf(longitudText.getText().toString()));
                        maps.putExtra("mcptt_id", mcptt_id);
                        System.out.println("ID: "+mcptt_id);
                        System.out.println("Latitud: "+latitudText.getText().toString());
                        System.out.println("Longitud: "+longitudText.getText().toString());
                        startActivity(maps);
                    }
                } else {
                    //Pedir permisos
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
                }
            }
        });




        mMCOPCallback = new IMCOPCallback.Stub() {
            @Override
            public void handleOnEvent(final List<Intent> actionList) throws RemoteException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Intent action : actionList) {
                            int codeError = -1;
                            int eventTypeInt = -1;
                            String stringError = null;
                            String sessionID = null;
                            int callType = 0;
                            if (action != null && action.getAction() != null && !action.getAction().trim().isEmpty())
                                try {

                                    switch (ConstantsMCOP.ActionsCallBack.fromString(action.getAction())) {
                                        case none:
                                            if (BuildConfig.DEBUG) Log.d(TAG, "none");
                                            break;
                                        case authorizationRequestEvent:
                                            codeError = -1;
                                            if ((codeError = action.getIntExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                // Error in authorizationRequestEvent
                                                stringError = action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_STRING);
                                                showLastError("authorizationRequestEvent", codeError, stringError);
                                            } else {
                                                // No error
                                                String requestUri = null;
                                                String redirect = null;
                                                if ((requestUri = action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.REQUEST_URI)) != null &&
                                                        (redirect = action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.REDIRECT_URI)) != null
                                                ) {
                                                    if (BuildConfig.DEBUG)
                                                        Log.d(TAG, "onAuthentication URI: " + requestUri + " redirectionURI: " + redirect);
                                                    Intent intent2 = new Intent(getApplicationContext(), ScreenAuthenticationWebView.class);
                                                    intent2.putExtra(ScreenAuthenticationWebView.DATA_USER, mcptt_id.split("-")[1]);
                                                    intent2.putExtra(ScreenAuthenticationWebView.DATA_PASS, mcptt_id.split("-")[1]);
                                                    intent2.putExtra(ScreenAuthenticationWebView.DATA_URI_INTENT, requestUri.trim());
                                                    intent2.putExtra(ScreenAuthenticationWebView.DATA_REDIRECTION_URI, redirect.trim());
                                                    startActivityForResult(intent2, AUTHETICATION_RESULT);
                                                }
                                            }
                                            break;
                                        case loginEvent:
                                            codeError = -1;
                                            if ((codeError = action.getIntExtra(ConstantsMCOP.LoginEventExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                // Error in LoginEvent
                                                stringError = action.getStringExtra(ConstantsMCOP.LoginEventExtras.ERROR_STRING);
                                                showLastError("LoginEvent", codeError, stringError);
                                            } else {
                                                // No error
                                                boolean success;
                                                mcptt_id = null;
                                                String displayName;
                                                if ((success = action.getBooleanExtra(ConstantsMCOP.LoginEventExtras.SUCCESS, VALUE_BOOLEAN_DEFAULT)) == true &&
                                                        (mcptt_id = action.getStringExtra(ConstantsMCOP.LoginEventExtras.MCPTT_ID)) != null
                                                ) {
                                                    if (BuildConfig.DEBUG) {
                                                        Log.d(TAG, "Login success: " + success + " mcptt_id: " + mcptt_id);
                                                    }
                                                    displayName = action.getStringExtra(ConstantsMCOP.LoginEventExtras.DISPLAY_NAME);
                                                    database = new BaseDeDatos(getApplicationContext());
                                                    for (int i = 0; i < Utilidades.unidades.size(); i++) {
                                                        if (Utilidades.unidades.get(i).getMcptt_id().equals(mcptt_id)) {
                                                            System.out.println(Utilidades.unidades.get(i).getMcptt_id());
                                                            System.out.println(mcptt_id);
                                                            patrulla = Utilidades.unidades.get(i).getPatrulla();
                                                            nombre = Utilidades.unidades.get(i).getNombre();
                                                            System.out.println("PATRULLA: "+patrulla);
                                                            System.out.println("NOMBRE: "+nombre);
                                                            status.setText(nombre);
                                                            EscucharFirebase escucharFirebase = new EscucharFirebase();
                                                            escucharFirebase.execute();
                                                            registered = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!registered) {
                                                        Intent registro = new Intent(MainActivity.this, RegistroUsuario.class);
                                                        registro.putExtra("registered", registered);
                                                        registro.putExtra("latitud", latitud);
                                                        registro.putExtra("longitud", longitud);
                                                        registro.putExtra("id", mcptt_id);
                                                        startActivityForResult(registro, REGISTRO_RESULT);
                                                    }
                                                    //actualizarPatrullas();
                                                    isRegisted(success, mcptt_id, displayName);
                                                } else {
                                                    Log.e(TAG, "Error: Registration process");
                                                }
                                            }
                                            break;
                                        case unLoginEvent:
                                            codeError = -1;
                                            if ((codeError = action.getIntExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                // Error in unLoginEvent
                                                stringError = action.getStringExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING);
                                                showLastError("unLoginEvent", codeError, stringError);
                                            } else {
                                                // No error
                                                boolean success = false;
                                                if ((success = action.getBooleanExtra(ConstantsMCOP.UnLoginEventExtras.SUCCESS, VALUE_BOOLEAN_DEFAULT)) == true) {
                                                    unRegisted(success);
                                                } else {
                                                    Log.e(TAG, "Error: Unregistration process");
                                                }
                                            }
                                            break;
                                        case configurationUpdateEvent:
                                            break;
                                        case callEvent:
                                            codeError = -1;
                                            eventTypeInt = action.getIntExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventTypeCall = null;

                                            if (eventTypeInt != ERROR_CODE_DEFAULT &&
                                                    (eventTypeCall = ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.fromInt(eventTypeInt)) != null) {
                                                switch (eventTypeCall) {
                                                    case NONE:
                                                        break;
                                                    case INCOMING:
                                                        Log.d(TAG, "STATE: INCOMING");
                                                        stringError = action.getStringExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING);
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        String callerID = action.getStringExtra(ConstantsMCOP.CallEventExtras.CALLER_USERID);
                                                        callType = action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE, ERROR_CODE_DEFAULT);
                                                        if (CallEvent.validationCallType(callType) == CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrivateEmergency) {
                                                            Log.d(TAG, "Prearranged Emergency Group Call");
                                                            //startERState();
                                                        } else if (CallEvent.validationCallType(callType) == CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrivateEmergency) {
                                                            Log.d(TAG, "Private Emergency Call");
                                                            //startERState();
                                                        }
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        showData("callEvent (" + sessionID + ")", "INCOMING -> " + callerID);
                                                        //text_talking.setVisibility((View.VISIBLE));
                                                        //text_callingid.setVisibility((View.VISIBLE));
                                                        //text_callingid.setText(callerID);
                                                        spinnerGroups.setEnabled(false);
                                                        spinnerUsers.setEnabled(false);
                                                        aSwitch.setEnabled(false);
                                                        showIdsAcceptCall(getApplicationContext(), sessionID);
                                                        break;
                                                    case RINGING:
                                                        Log.d(TAG, "STATE: RINGING");
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        spinnerGroups.setEnabled(false);
                                                        spinnerUsers.setEnabled(false);
                                                        aSwitch.setEnabled(false);
                                                        showData("callEvent (" + sessionID + ")", "RINGING");
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        break;
                                                    case INPROGRESS:
                                                        Log.d(TAG, "STATE: INPROGRESS");
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        spinnerGroups.setEnabled(false);
                                                        spinnerUsers.setEnabled(false);
                                                        aSwitch.setEnabled(false);
                                                        emergencia.setEnabled(true);
                                                        showData("callEvent (" + sessionID + ")", "INPROGRESS");
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        break;
                                                    case CONNECTED:
                                                        Log.d(TAG, "STATE: CONNECTED");
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        callType = action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE, ERROR_CODE_DEFAULT);
                                                        if (CallEvent.validationCallType(callType) == CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrearrangedGroupEmergency) {
                                                            Log.d(TAG, "Prearranged Emergency Group Call");
                                                            //startERState();
                                                        } else if (CallEvent.validationCallType(callType) == CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrivateEmergency) {
                                                            Log.d(TAG, "Private Emergency Call");
                                                            //startERState();
                                                        }
                                                        showData("callEvent (" + sessionID + ")", "CONNECTED");
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        //emergencia.setEnabled(false);
                                                        break;
                                                    case TERMINATED:
                                                        Log.d(TAG, "STATE: TERMINATED");
                                                        consola.setText("Fin de comunicación");
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        spinnerGroups.setEnabled(true);
                                                        spinnerUsers.setEnabled(true);
                                                        aSwitch.setEnabled(true);
                                                        switchGroup.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
                                                        switchGroup.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
                                                        showData("callEvent (" + sessionID + ")", "TERMINATED");
                                                        if (sessionID != null)
                                                            userData.removeSessionID(sessionID);
                                                        if (mERState == true) {
                                                            endERState();
                                                        }
                                                        botonHablar.setCardBackgroundColor(getResources().getColor(R.color.CardView, null));
                                                        mState = State.NONE;
                                                        botonHablar.setEnabled(true);
                                                        emergencia.setEnabled(true);
                                                        //text_talking.setVisibility((View.INVISIBLE));
                                                        //text_callingid.setVisibility((View.INVISIBLE));
                                                        break;
                                                    case ERROR:
                                                        Log.e(TAG, "STATE: ERROR");
                                                        if ((codeError = action.getIntExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                            // Error in callEvent
                                                            stringError = action.getStringExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING);
                                                            sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                            showLastError("callEvent (" + sessionID + ")", codeError, stringError);
                                                        }
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        break;
                                                    case UPDATE:
                                                        Log.d(TAG, "STATE: UPDATE");
                                                        sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        int updateCallType = action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE, ERROR_CODE_DEFAULT);
                                                        showData("callEvent (" + sessionID + ")", "UPDATE -> CallType: " + updateCallType);
                                                        if (sessionID != null)
                                                            userData.addSessionID(sessionID);
                                                        break;
                                                    default:
                                                        showLastError("callEvent:", 999, "RECEIVE EVENT NO VALID");
                                                        break;
                                                }
                                            } else {
                                                showLastError("callEvent:", 999, "RECEIVE EVENT NO VALID");
                                            }
                                            break;
                                        case floorControlEvent:
                                            codeError = -1;
                                            if ((codeError = action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                // Error in unLoginEvent
                                                sessionID = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.SESSION_ID);
                                                stringError = action.getStringExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING);
                                                showLastError("floorControlEvent(" + sessionID + ")", codeError, stringError);
                                            } else {
                                                // No error
                                                boolean success = false;
                                                String eventFloorControl = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.FLOOR_CONTROL_EVENT);
                                                String causeString = null;
                                                int causeInt = -1;
                                                try {
                                                    sessionID = action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                    switch (ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.fromString(eventFloorControl)) {
                                                        case none:
                                                            break;
                                                        case granted:
                                                            Log.d(TAG, "TOKEN GRANTED");
                                                            int durationGranted = action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.DURATION_TOKEN, ERROR_CODE_DEFAULT);
                                                            showData("floorControl (" + sessionID + ")", " granted " + "-> Duration: " + durationGranted);
                                                            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.OK, null));
                                                            mState = State.GRANTED;
                                                            botonHablar.setEnabled(true);
                                                            datos.setEnabled(true);
                                                            break;
                                                        case idle:
                                                            Log.d(TAG, "TOKEN IDLE");
                                                            showData("floorControl (" + sessionID + ")", " idle");
                                                            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.ocupado, null));
                                                            mState = State.IDLE;
                                                            botonHablar.setEnabled(true);
                                                            datos.setEnabled(true);
                                                            //text_talking.setVisibility((View.INVISIBLE));
                                                            //text_callingid.setVisibility((View.INVISIBLE));
                                                            break;
                                                        case taken:
                                                            Log.d(TAG, "TOKEN TAKEN");
                                                            String userIDTaken = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.USER_ID);
                                                            String displayNameTaken = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.DISPLAY_NAME);
                                                            boolean allow_request = action.getBooleanExtra(ConstantsMCOP.FloorControlEventExtras.ALLOW_REQUEST, VALUE_BOOLEAN_DEFAULT);
                                                            showData("floorControl (" + sessionID + ")", " granted " + "-> userIDTaken(allowRequest=" + allow_request + "):(" + userIDTaken + ":" + displayNameTaken + ")");
                                                            mState = State.TAKEN;
                                                            botonHablar.setEnabled(false);
                                                            datos.setEnabled(true);
                                                            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.Red, null));
                                                            //text_talking.setVisibility((View.VISIBLE));
                                                            //text_callingid.setVisibility((View.VISIBLE));
                                                            //text_callingid.setText(userIDTaken);
                                                            break;
                                                        case denied:
                                                            Log.d(TAG, "TOKEN DENIED");
                                                            causeString = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_STRING);
                                                            causeInt = action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_CODE, ERROR_CODE_DEFAULT);
                                                            showData("floorControl (" + sessionID + ")", " denied " + "-> cause(" + causeInt + ":" + causeString + ")");
                                                            break;
                                                        case revoked:
                                                            Log.d(TAG, "TOKEN REVOKED");
                                                            causeString = action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_STRING);
                                                            causeInt = action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_CODE, ERROR_CODE_DEFAULT);
                                                            showData("floorControl (" + sessionID + ")", " revoked " + "-> cause(" + causeInt + ":" + causeString + ")");
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                } catch (Exception e) {

                                                }
                                            }

                                            break;
                                        case groupInfoEvent:
                                            break;
                                        case groupAffiliationEvent:
                                            codeError = -1;
                                            eventTypeInt = action.getIntExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE, ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum eventTypeAffiliation = null;
                                            if (eventTypeInt != ERROR_CODE_DEFAULT &&
                                                    (eventTypeAffiliation = ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.fromInt(eventTypeInt)) != null) {
                                                switch (eventTypeAffiliation) {
                                                    case GROUP_AFFILIATION_UPDATE:
                                                        Map<String, Integer> groups = (HashMap<String, Integer>) action.getSerializableExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUPS_LIST);
                                                        if (groups != null)
                                                            showGroups(groups);
                                                        break;
                                                    case GROUP_AFFILIATION_ERROR:
                                                        if ((codeError = action.getIntExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_CODE, ERROR_CODE_DEFAULT)) != ERROR_CODE_DEFAULT) {
                                                            // Error in unLoginEvent
                                                            stringError = action.getStringExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_STRING);
                                                            String groupID = action.getStringExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUP_ID);
                                                            showLastError("groupAffiliationEvent (" + groupID + ")", codeError, stringError);
                                                        }
                                                        break;
                                                    case REMOTE_AFFILIATION:
                                                        break;
                                                    default:
                                                        showLastError("groupAffiliationEvent:", 999, "INVALID RECEIVE EVENT");
                                                        break;
                                                }
                                            } else {
                                                showLastError("groupAffiliationEvent:", 999, "INVALID RECEIVE EVENT");
                                            }

                                            break;
                                        case selectedContactChangeEvent:
                                            break;
                                        case eMBMSNotificationEvent:
                                            if (BuildConfig.DEBUG)
                                                Log.d(TAG, "Receipt eMBMS Notification Event");
                                            codeError = -1;
                                            String TMGI = null;
                                            String areaList = null;
                                            eventTypeInt = action.getIntExtra(ConstantsMCOP.EMBMSNotificationEventExtras.EVENT_TYPE, ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum eventTypeEmbms = null;
                                            if (eventTypeInt != ERROR_CODE_DEFAULT &&
                                                    (eventTypeEmbms = ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum.fromInt(eventTypeInt)) != null) {
                                                switch (eventTypeEmbms) {
                                                    case none:
                                                        break;
                                                    case eMBMSAvailable:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS available");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            //reg_eMBMS.setEnabled(true);
                                                        }
                                                        break;
                                                    case UndereMBMSCoverage:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS under coverage");
                                                        TMGI = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.TMGI);
                                                        sessionID = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.SESSION_ID);
                                                        areaList = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.AREA_LIST);
                                                        break;
                                                    case eMBMSBearerInUse:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS bearer in use");
                                                        TMGI = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.TMGI);
                                                        sessionID = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.SESSION_ID);
                                                        areaList = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.AREA_LIST);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            //reg_eMBMS.setEnabled(true);
                                                        } else {
                                                            //reg_eMBMS.setEnabled(true);
                                                        }
                                                        break;
                                                    case eMBMSBearerNotInUse:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS bearer not in use");
                                                        TMGI = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.TMGI);
                                                        sessionID = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.SESSION_ID);
                                                        areaList = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.AREA_LIST);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            //reg_eMBMS.setEnabled(false);
                                                        } else {
                                                            //reg_eMBMS.setEnabled(false);
                                                        }
                                                        break;
                                                    case NoeMBMSCoverage:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS not under coverage");
                                                        TMGI = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.TMGI);
                                                        sessionID = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.SESSION_ID);
                                                        areaList = action.getStringExtra(ConstantsMCOP.EMBMSNotificationEventExtras.AREA_LIST);
                                                        break;
                                                    case eMBMSNotAvailable:
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "Receipt eMBMS not available");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            //reg_eMBMS.setBackgroundColor(getColor(R.color.unregistered));
                                                        }
                                                        break;
                                                    default:
                                                        showLastError("eMBMSNotificationEvent:", 999, "INVALID RECEIVE EVENT");
                                                        break;
                                                }
                                            } else {
                                                showLastError("eMBMSNotificationEvent:", 999, "INVALID RECEIVE EVENT");
                                            }
                                            break;
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "Event Action Error: " + action.getAction() + " error:" + ex.getMessage());
                                }
                        }
                    }
                });
            }
        };
    }

    private void empezarLocationServices() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (location != null) {
                        // Logic to handle location object
                        latitudText.setText(String.valueOf(location.getLatitude()));
                        longitudText.setText(String.valueOf(location.getLongitude()));
                        try {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            pueblo = list.get(0).toString().split("locality=")[1].split(",thoroughfare=")[0];
                            calle = list.get(0).toString().split(",thoroughfare=")[1].split(",postalCode=")[0];
                            MainActivity.setDireccion(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                                    pueblo, calle);
                            database = new BaseDeDatos(getApplicationContext());
                            database.execute("actualizarLatLongUnidad.php", mcptt_id, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                            database = new BaseDeDatos(getApplicationContext());
                            database.execute("selectUnidades.php");
                            database = new BaseDeDatos(getApplicationContext());
                            database.execute("selectActuaciones.php");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            ;
        };
        locationRequest = new LocationRequest().setInterval(30000).setFastestInterval(30000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    private void lanzarNotificacion(String tipo, String direccion) {
        Log.d("Lanzando notificacion", tipo);
        Notification.Builder notificacion = new Notification.Builder(this);
        notificacion.setAutoCancel(true);

        notificacion.setSmallIcon(R.drawable.alarma);
        notificacion.setContentTitle(tipo);
        notificacion.setContentText(direccion);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("4", "Actuación", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.YELLOW);
            mChannel.enableLights(true);
            mChannel.setDescription("Actuación");
            mChannel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setVibrationPattern(new long[]{3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000});

            nm.createNotificationChannel(mChannel);
            notificacion.setChannelId("4");

        }

        Intent i = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion.setContentIntent(pendingIntent);
        nm.notify(4, notificacion.build());
    }

    private void lanzarMensaje(String nombre, String mensaje) {
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, "5");
        notificacion.setAutoCancel(true);

        notificacion.setSmallIcon(R.drawable.ic_chat_foreground);
        notificacion.setContentTitle(nombre);
        notificacion.setContentText(mensaje);
        notificacion.setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje)).build();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("5", "Mensajes", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription("Mensajes");
            mChannel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setVibrationPattern(new long[]{3000, 3000, 3000, 0, 3000, 0, 3000, 0, 3000, 0});

            nm.createNotificationChannel(mChannel);
            notificacion.setChannelId("5");

        }

        Intent i = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion.setContentIntent(pendingIntent);
        nm.notify(5, notificacion.build());
    }

    private void showLastError(String from, int code, String errorString) {
        Log.e(TAG, "ERROR " + from + ": " + code + " " + errorString);
        consola.setText("ERROR " + from + ": " + code + " " + errorString, TextView.BufferType.EDITABLE);
    }

    private void showData(String eventType, String data) {
        consola.setText(eventType + ": " + data);
    }

    private void makeCall() {
        if (mERState == false) {
            // Non-Emergency Calls
            if (mCallType == CallType.GROUP) {
                // Group Call
                try {
                    Log.d(TAG, "Call type: " + mCallType);
                    if (mService != null)
                        mService.makeCall(
                                selGroup, //DEFAULT_GROUP,
                                ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.PrearrangedGroup.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (mCallType == CallType.PRIVATE) {
                // Private Call
                try {
                    Log.d(TAG, "Call type: " + mCallType);
                    if (mService != null)
                        System.out.println("SELUSER: "+selUser);
                        mService.makeCall(
                                selUser, //DEFAULT_PRIVATE_CALL,
                                ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.Private.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Emergency Calls
            if (mCallType == CallType.GROUP) {
                // Emergency Group Call
                try {
                    Log.d(TAG, "Call type: Emergency " + mCallType);
                    if (mService != null)
                        mService.makeCall(
                                selGroup, //DEFAULT_GROUP,
                                ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.PrearrangedGroup.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (mCallType == CallType.PRIVATE) {
                // Private Call
                try {
                    Log.d(TAG, "Call type: Emergency " + mCallType);
                    if (mService != null)
                        mService.makeCall(
                                selUser, //DEFAULT_PRIVATE_CALL,
                                ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.Private.getValue() |
                                        ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        datos.setEnabled(true);
    }

    private boolean showIdsOperationFloorControl(final Context context, final boolean request) {
        Log.i(TAG, "Send floor control operation : " + ((request) ? "request" : "release"));
        if (userData.getSessionIDs() == null) return false;
        final String[] strings = userData.getSessionIDs().toArray(new String[userData.getSessionIDs().size()]);
        if (strings == null || strings.length == 0) return false;
        if (strings.length == 1) {
            try {
                if (mService != null) {
                    mService.floorControlOperation(
                            strings[0],
                            request ? ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Request.getValue() : ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Release.getValue(),
                            mcptt_id);
                }
                Log.i(TAG, "Send floor control operation 2: " + ((request) ? "request" : "release"));

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            mDialogIds = DialogMenu.newInstance(strings, null);
            mDialogIds.setOnClickItemListener(new DialogMenu.OnClickListener() {
                @Override
                public void onClickItem(int item) {
                    if (item >= 0 && strings.length > item) {
                        try {
                            if (mService != null) {
                                mService.floorControlOperation(
                                        strings[item],
                                        request ? ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Request.getValue() : ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Release.getValue(),
                                        mcptt_id);
                                Log.i(TAG, "Send floor control operation 3: " + ((request) ? "request" : "release"));
                            }

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mDialogIds.show(getSupportFragmentManager(), "SimpleDialog");
        }
        return true;
    }

    private void showIdsAcceptCall(final Context context, String sessionID) {
        if (userData.getSessionIDs() == null) return;
        final String[] strings = userData.getSessionIDs().toArray(new String[userData.getSessionIDs().size()]);
        if (strings == null) return;
        try {
            if (mService != null)
                mService.acceptCall(sessionID);
            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.Red, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void showGroups(Map<String, Integer> groups) {
        String result = "";
        if (groups != null) {
            for (String groupID : groups.keySet()) {
                String type = "";
                switch (ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.fromInt(groups.get(groupID))) {
                    case notaffiliated:
                        type = "notaffiliated";
                        break;
                    case affiliating:
                        type = "affiliating";
                        break;
                    case affiliated:
                        type = "affiliated";
                        groupsCurrent.add(groupID);
                        break;
                    case deaffiliating:
                        type = "deaffiliating";
                        break;
                }
                result = result + "groupID:" + groupID + ":" + type + "\n";
            }
            loadGroups();
        }
        consola.setText("List Affiliation Groups: \n" + result);
    }

    private void loadGroups() {
        ArrayAdapter<String> groupAdaptor = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, groupsCurrent);
        // Drop down layout style - list view with radio button
        groupAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroups.setAdapter(groupAdaptor);
    }

    private void isRegisted(boolean success, String mcpttID, String displayName) {
        userData.setRegisted(success);
        if (mcpttID != null)
            userData.setMcpttID(mcpttID);
        if (displayName != null) {
            userData.setDisplayName(displayName);
        }
        consola.setText("REGISTERED - MCPTT ID: " + mcpttID + " DISPLAY NAME: " + displayName);
        status.setText(nombre);
        status.setTextColor(getColor(R.color.OK));
        registered=true;
        invalidateOptionsMenu();
        activarBotones(true);
    }

    @SuppressLint("ResourceAsColor")
    private void unRegisted(boolean success) {
        userData.setRegisted(false);
        userData.setDisplayName(null);
        userData.setMcpttID(null);
        registered = false;
        consola.setText("UNREGISTERED");
        status.setText("STATUS OFF");
        status.setTextColor(getResources().getColor(R.color.Red, null));
        invalidateOptionsMenu();
        activarBotones(false);
    }

    private void activarBotones(boolean estado) {
        if (estado) {
            datos.setEnabled(true);
            registro.setEnabled(false);
            cortar.setEnabled(true);
            botonHablar.setEnabled(true);
            añadirActuacion.setEnabled(true);
            //reg_status.setEnabled(true);
            emergencia.setEnabled(true);
            location.setEnabled(true);
            aSwitch.setEnabled(true);
            spinnerUsers.setEnabled(true);
            spinnerGroups.setEnabled(true);
            spinnerUsers.setVisibility((View.VISIBLE));
            spinnerGroups.setVisibility((View.VISIBLE));
            altavoz.setEnabled(true);
            cortar.setCardBackgroundColor(getColor(R.color.CardView));
            añadirActuacion.setCardBackgroundColor(getColor(R.color.CardView));
            datos.setCardBackgroundColor(getColor(R.color.CardView));
            altavoz.setCardBackgroundColor(getColor(R.color.CardView));
            chat.setCardBackgroundColor(getColor(R.color.CardView));
            emergencia.setCardBackgroundColor(getColor(R.color.Red));
            location.setCardBackgroundColor(getColor(R.color.CardView));
            registro.setCardBackgroundColor(getColor(R.color.Disabled));
            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.CardView, null));

        } else {
            cortar.setEnabled(false);
            datos.setEnabled(false);
            registro.setEnabled(true);
            location.setEnabled(false);
            emergencia.setEnabled(false);
            //reg_status.setEnabled(false);
            aSwitch.setEnabled(false);
            añadirActuacion.setEnabled(false);
            spinnerUsers.setEnabled(true);
            spinnerGroups.setEnabled(true);
            altavoz.setEnabled(false);
            isSpeakerphoneOn = false;
            botonHablar.setCardBackgroundColor(getResources().getColor(R.color.Disabled, null));
            registro.setCardBackgroundColor(getColor(R.color.CardView));
            cortar.setCardBackgroundColor(getColor(R.color.Disabled));
            añadirActuacion.setCardBackgroundColor(getColor(R.color.Disabled));
            datos.setCardBackgroundColor(getColor(R.color.Disabled));
            altavoz.setCardBackgroundColor(getColor(R.color.Disabled));
            chat.setCardBackgroundColor(getColor(R.color.Disabled));
            emergencia.setCardBackgroundColor(getColor(R.color.Disabled));
            location.setCardBackgroundColor(getColor(R.color.Disabled));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        itemIdMSCSM = menu.findItem(R.id.action_registration);
        itemAutoReg = menu.findItem(R.id.action_auto_reg);
        itemExit = menu.findItem(R.id.action_exit);
        if (registered) {
            itemIdMSCSM.setVisible(false);
            itemAutoReg.setVisible(false);
            itemExit.setVisible(true);
            itemExit.setEnabled(true);
        } else {
            itemIdMSCSM.setVisible(true);
            itemAutoReg.setVisible(true);
            itemExit.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        result = super.onOptionsItemSelected(item);
        // Handle action bar item clicks. The action bar will
        // automatically handle clicks on the Home/Up button, as long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // No inspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_registration:
                if (BuildConfig.DEBUG) Log.d(TAG, "Selected Registration configuration");
                mDialogMenu = DialogMenu.newInstance(regConfig, IdMSCMS ? "IdMS/CMS" : "None");
                mDialogMenu.setOnClickItemListener(new DialogMenu.OnClickListener() {
                    @Override
                    public void onClickItem(int item) {
                        if (item >= 0 && regConfig.length > item) {
                            Log.d(TAG, "Selected registration configuration: " + regConfig[item]);
                            if (regConfig[item].equals("IdMS/CMS")) {
                                IdMSCMS = true;
                                Toast.makeText(getApplicationContext(), "IdMS/CMS Registration", Toast.LENGTH_SHORT).show();
                            } else if (regConfig[item].equals("None")) {
                                IdMSCMS = false;
                                Toast.makeText(getApplicationContext(), "None", Toast.LENGTH_SHORT).show();
                            }
                        }
                        saveConfiguration();
                    }
                });
                mDialogMenu.show(getSupportFragmentManager(), "SimpleDialog");
                break;
            case R.id.action_auto_reg:
                if (BuildConfig.DEBUG) Log.d(TAG, "Selected Auto-Registration");
                mDialogMenu = DialogMenu.newInstance(autoReg, autoRegister ? "Automatic" : "Manual");
                mDialogMenu.setOnClickItemListener(new DialogMenu.OnClickListener() {
                    @Override
                    public void onClickItem(int item) {
                        if (item >= 0 && autoReg.length > item) {
                            Log.d(TAG, "Selected registration configuration: " + autoReg[item]);
                            if (autoReg[item].equals("Automatic")) {
                                autoRegister = true;
                                Toast.makeText(getApplicationContext(), "Auto-Registration", Toast.LENGTH_SHORT).show();
                                registro();
                            } else if (autoReg[item].equals("Manual")) {
                                autoRegister = false;
                                Toast.makeText(getApplicationContext(), "Manual Registration", Toast.LENGTH_SHORT).show();
                            }
                        }
                        saveConfiguration();
                    }
                });
                mDialogMenu.show(getSupportFragmentManager(), "SimpleDialog");
                break;
            case R.id.action_about:
                if (BuildConfig.DEBUG) Log.d(TAG, "Selected About");
                mDialogAlert = DialogAlert.newInstance(getAbout(this), getString(R.string.option_about), false);
                mDialogAlert.show(getSupportFragmentManager(), "SimpleDialog");
                break;
            case R.id.action_exit:
                if (BuildConfig.DEBUG) Log.d(TAG, "Selected Exit");
                saveConfiguration();
                if (mService != null) {
                    try {
                        mService.unLoginMCOP();
                        if (serviceIntent != null) {
                            mConnection = null;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
        saveConfiguration();
        return result;
    }

    public static String getAbout(Context context) {
        if (context == null) return "";
        PackageInfo pInfo = null;
        String version = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = null;
        }
        String about = context.getString(R.string.app_name) + "\n";
        about += context.getString(R.string.Build_version) + ": " + version;
        about += "\n" + context.getString(R.string.Developed_by) + ": " + context.getString(R.string.web);
        about += "\n" + context.getString(R.string.copyright);

        return about;
    }

    private void connectService() {
        connectService(null);
    }

    private void connectService(String client) {
        if (BuildConfig.DEBUG) Log.d(TAG, "connectService execute");
        if (!isConnect) {
            serviceIntent = new Intent()
                    .setComponent(new ComponentName(
                            "org.mcopenplatform.muoapi",
                            "org.mcopenplatform.muoapi.MCOPsdk"));

            if (client != null && !client.trim().isEmpty()) {
                Log.i(TAG, "Current profile: " + currentProfile);
                serviceIntent.putExtra("PROFILE_SELECT", currentProfile != null ? currentProfile : client);
            }

            serviceIntent.putExtra(ConstantsMCOP.MBMS_PLUGIN_PACKAGE_ID, "com.expway.embmsserver");
            serviceIntent.putExtra(ConstantsMCOP.MBMS_PLUGIN_SERVICE_ID, "com.expway.embmsserver.MCOP");

            try {
                ComponentName componentName;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    componentName = this.startForegroundService(serviceIntent);
                } else {
                    componentName = this.startService(serviceIntent);
                }
                if (componentName == null) {
                    Log.e(TAG, "Starting Error: " + componentName.getPackageName());
                } else if (serviceIntent == null) {
                    Log.e(TAG, "serviceIntent Error: " + componentName.getPackageName());
                } else if (mConnection == null) {
                    Log.e(TAG, "mConnection Error: " + componentName.getPackageName());
                } else {

                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.w(TAG, "Error in start service: " + e.getMessage());
            }
            if (mConnection != null) Log.i(TAG, "Bind Service: " + bindService(serviceIntent, mConnection, BIND_AUTO_CREATE));
        }
    }


    private Map<String, String> getProfilesParameters(List<String> parameters) {
        Map<String, String> parametersMap = new HashMap<>();
        if (parameters != null && !parameters.isEmpty()) {
            Log.i(TAG, "External parameters");
        } else {
            Log.i(TAG, "No external parameters");
            parameters = loadParameters();
        }

        if (parameters != null && !parameters.isEmpty())
            for (String parameter : parameters) {
                Log.i(TAG, "parameter: " + parameter);
                String[] parametersSplit = parameter.split(":");
                if (parametersSplit != null && parametersSplit.length == 2) {
                    parametersMap.put(parametersSplit[0], parametersSplit[1]);
                }
            }
        if (parametersMap != null && !parametersMap.isEmpty()) {
            saveParameters(parameters);
        }
        return parametersMap;
    }

    private boolean saveParameters(List<String> parameters) {
        if (preferencesManager != null) {
            return preferencesManager.putStringSet(this, PARAMETER_SAVE_PROFILE, new HashSet<String>(parameters));
        }
        return false;
    }

    private ArrayList<String> loadParameters() {
        if (preferencesManager != null) {
            Set<String> stringSet = preferencesManager.getStringSet(this, PARAMETER_SAVE_PROFILE);
            if (stringSet != null) {
                return (new ArrayList<String>(stringSet));
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG) Log.i(TAG, "onDestroy");
        saveConfiguration();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        loadConfiguration();
        super.onResume();
    }

    private boolean saveConfiguration() {
        if (preferencesManager != null) {
            if (mcptt_id != null) {
                preferencesManager.putString(this, "mcptt_id", mcptt_id);
            }
            if (IdMSCMS) {
                preferencesManager.putString(this, PARAMETER_CONFIG_IDMSCMS, "IdMS/CMS");
            } else {
                preferencesManager.putString(this, PARAMETER_CONFIG_IDMSCMS, "None");
            }
            if (autoRegister) {
                preferencesManager.putString(this, PARAMETER_CONFIG_AUTOREGISTER, "Automatic");
            } else {
                preferencesManager.putString(this, PARAMETER_CONFIG_AUTOREGISTER, "Manual");
            }
        }
        System.out.println("Mensajes guardados: " + Utilidades.mensajes.toString());
        SharedPreferences SharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = SharedPrefs.edit();
        Gson gson = new Gson();
        for(int i=0;i<Utilidades.mensajes.size();i++){
            System.out.println("Guardados: "+Utilidades.mensajes.get(i).getKey());
        }
        String json = gson.toJson(Utilidades.mensajes);
        editor.remove("mensajes").apply();
        editor.remove("mensajes").commit();
        editor.putString("mensajes", json);
        editor.commit();
        return true;
    }

    private void loadConfiguration() {
        if (preferencesManager != null) {
            if (mcptt_id != null) {
                preferencesManager.getString(this, "mcptt_id", "");
            }
            String idmscms = preferencesManager.getString(this, PARAMETER_CONFIG_IDMSCMS, "None");
            if (idmscms == null) activarIdMSCMS();
            if (idmscms.equals("IdMS/CMS")) {
                IdMSCMS = true;
            } else if (idmscms == null) activarIdMSCMS();
            else {

                IdMSCMS = false;
            }
            String auto = preferencesManager.getString(this, PARAMETER_CONFIG_AUTOREGISTER, "Manual");

            if (auto.equals("Automatic")) {
                autoRegister = true;
            } else if (auto == null) activarAuto();
            else {
                autoRegister = false;
            }
        }
        SharedPreferences SharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = SharedPrefs.getString("notificaciones", "");
        Type type = new TypeToken<List<Mensaje>>(){}.getType();
        if(gson.fromJson(json,type)!=null) {
            Utilidades.mensajes.clear();
            Utilidades.mensajes = gson.fromJson(json, type);
        }

    }

    private void startERState() {
        // Start Emergency State
        Log.d(TAG, "Start Emergency State");
        consola.setText("EMERGENCIA");
        consola.setTextSize(20);
        consola.setBackgroundColor(getResources().getColor(R.color.Red, null));
    }

    private void endERState() {
        // End Emergency State
        Log.d(TAG, "End Emergency State");
        consola.setText("Emergencia terminada");
        consola.setTextSize(14);
        consola.setBackgroundColor(getResources().getColor(R.color.micolor, null));
    }

    private void activarAuto() {

        registroAutomatico = new AlertDialog.Builder(this);
        registroAutomatico.setMessage("Registro automático").setCancelable(false).setIcon(R.drawable.portapapeles)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        autoRegister = true;
                    }
                }).setNegativeButton("Desactivar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                autoRegister = false;
            }
        });
        registroAutomatico.create().show();
    }

    private void activarIdMSCMS() {

        registroAutomatico = new AlertDialog.Builder(this);
        registroAutomatico.setMessage("Recomendamos activar IdMSCMS").setCancelable(false).setIcon(R.drawable.portapapeles)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IdMSCMS = true;
                    }
                }).setNegativeButton("Desactivar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IdMSCMS = false;
            }
        });
        registroAutomatico.create().show();
    }

    private void activarGps() {

        encenderUbicacion = new AlertDialog.Builder(this);
        encenderUbicacion.setMessage("Es necesario activar la ubicación").setCancelable(false).setIcon(R.drawable.ubi)
                .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), UBICACION_ACTIVADA);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "IDM", Toast.LENGTH_SHORT).show();
            }
        });
        encenderUbicacion.create().show();
    }

    public void registro() {
        if (IdMSCMS) {
            try {
                if (mService != null) {
                    Toast.makeText(getApplicationContext(), "service login", Toast.LENGTH_SHORT).show();
                    mService.loginMCOP();
                } else {
                    Log.i("mService", "mService == null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else { // CMS
            try {
                if (mService != null) {
                    Toast.makeText(getApplicationContext(), "authorize user", Toast.LENGTH_SHORT).show();
                    mService.authorizeUser(null);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UBICACION_ACTIVADA) {
            Log.d("Ubicacion", Integer.toString(UBICACION_ACTIVADA));
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    empezarLocationServices();
                } else {
                    Toast.makeText(this, "Location permission disabled", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Location provider disabled", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REGISTRO_RESULT) {
            if (resultCode == RESULT_OK) {
                registered = data.getBooleanExtra("registered", true);
                actualizarPatrullas();
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            Toast.makeText(getApplicationContext(), "Foto guardada", Toast.LENGTH_SHORT).show();
            galleryAddPic();
        }
        if (requestCode == CAMERA_RESULT) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                View registro = LayoutInflater.from(this).inflate(R.layout.registro_usuarios, null);
                ImageView fotoPerfil = (ImageView) registro.findViewById(R.id.fotoDePerfil);
                fotoPerfil.setImageBitmap(imageBitmap);
                dispatchTakePictureIntent();
                galleryAddPic();
            } else {

            }
        }
        if (requestCode == AUTHETICATION_RESULT) {
            if (resultCode == ScreenAuthenticationWebView.RETURN_ON_AUTHENTICATION_LISTENER_FAILURE) {
                String dataError;
                if (data != null &&
                        (dataError = data.getStringExtra(com.example.android.bomberostfg.ScreenAuthenticationWebView.RETURN_ON_AUTHENTICATION_ERROR)) != null &&
                        dataError instanceof String) {
                    Log.e(TAG, "Authentication Error: " + dataError);
                } else {
                    Log.e(TAG, "Error processing authentication.");
                }
            } else if (resultCode == ScreenAuthenticationWebView.RETURN_ON_AUTHENTICATION_LISTENER_OK) {
                String dataUri;
                if (data != null &&
                        (dataUri = data.getStringExtra(com.example.android.bomberostfg.ScreenAuthenticationWebView.RETURN_ON_AUTHENTICATION_RESPONSE)) != null &&
                        dataUri instanceof String) {
                    URI uri = null;
                    try {
                        uri = new URI(dataUri);
                        Log.i(TAG, "Uri: " + uri.toString());
                        try {
                            if (mService != null)
                                mService.authorizeUser(dataUri);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } catch (URISyntaxException e) {
                        Log.e(TAG, "Authentication Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Error processing file to import profiles.");
                }
            }
        }
    }

    String currentPhotoPath;

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
        System.out.println("CurrentPhotoPath: "+ currentPhotoPath);
        return image;
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println("Current Photo Path: "+currentPhotoPath);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso ha sido concedido.
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = Uri.parse(currentPhotoPath);
                    camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(camera, CAMERA_RESULT);
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                    Toast.makeText(this, "Permiso de camara denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GET_PERMISSION: {
                //If request is cancelled, result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted, yay! Do the
                    //contacts-related task you need to do.
                    //API>22
                    setPermissionsWriteSetting();
                    connectService();
                } else {
                    setPermissions();
                    //Permission denied, boo! Disable the
                    //functionality that depends on this permission.
                }
                return;
            }
            default:
                break;
            // otros bloques de 'case' para controlar otros permisos de la aplicación
        }
    }

    public void getFirstLocation() {
        System.out.println("FIRST LOCATION");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                latitud = Float.valueOf(String.valueOf(location.getLatitude()));
                                longitud = Float.valueOf(String.valueOf(location.getLongitude()));
                                latitudText.setText(String.valueOf(latitud));
                                longitudText.setText(String.valueOf(longitud));

                                if (!list.isEmpty()) {
                                    pueblo = list.get(0).toString().split("locality=")[1].split(",thoroughfare=")[0];
                                    calle = list.get(0).toString().split(",thoroughfare=")[1].split(",postalCode=")[0];
                                    adress.setText(pueblo + ", " + calle);
                                    if (pueblo.equals("null") || calle.equals("null")) {
                                        adress.setText("Calculando...");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    protected void setPermissions() {
        //Set permissions
        //READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                //Show an explanation to the user *asynchronously* -- don't block
                //this thread waiting for the user's response! After the user
                //sees the explanation, request the permission again.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE},
                        GET_PERMISSION);
            } else {
                //No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE},
                        GET_PERMISSION);

                //MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                //app-defined int constant. The callback method gets the
                //result of the request.
            }
        }
    }

    protected void setPermissionsWriteSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.System.canWrite(this)) {
                //Do stuff here
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public static void setDireccion(String platitud, String plongitud, String pueblo, String calle) {
        latitudText.setText(platitud);
        longitudText.setText(plongitud);
        adress.setText(pueblo + ", " + calle);
    }

    public void actualizarPatrullas() {
        boolean existePatrulla = false;
        for (int i = 0; i < Utilidades.unidades.size(); i++) {
            for (int j = 0; j < Utilidades.patrullas.size(); j++) {
                if (Utilidades.unidades.get(i).getPatrulla() == Utilidades.patrullas.get(j).getId()) {
                    database = new BaseDeDatos(this);
                    database.execute("actualizarPatrulla.php", String.valueOf(Utilidades.unidades.get(i).getPatrulla()), Utilidades.unidades.get(i).getEspecialidad(),null);
                    existePatrulla = true;
                    break;
                }
            }
            if (!existePatrulla) {
                database = new BaseDeDatos(this);
                database.execute("insertarPatrulla.php", String.valueOf(Utilidades.unidades.get(i).getPatrulla()), Utilidades.unidades.get(i).getEspecialidad(),"0");
            }
            existePatrulla = false;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null /* Looper */);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        }
    }

    public class EscucharFirebase extends AsyncTask<String, String, String> {
        private boolean esta = false;
        @Override
        protected String doInBackground(String... strings) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("emergencias").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        if ((Long) map.get("Patrulla") == patrulla && map.get("Final") == null) {
                            mERState=true;
                            if (map.get("Tipo").equals("Informe de situación"))
                                fondoPrincipal.setBackgroundColor(getColor(R.color.ocupado));
                            if (map.get("Tipo").equals("Evacuacion inmediata"))
                                fondoPrincipal.setBackgroundColor(getColor(R.color.OK));
                            if (map.get("Tipo").equals("Inmobilización general"))
                                fondoPrincipal.setBackgroundColor(getColor(R.color.Red));
                            consola.setText(map.get("Tipo") + "\r\n" + map.get("Patrulla") + "\r\n" + map.get("Inicio"));
                            lanzarNotificacion("EMERGENCIA", map.get("Tipo").toString());
                            if (!Utilidades.emergenciaKey.contains(ds.getKey()))
                                Utilidades.emergenciaKey.add(ds.getKey());

                        }
                        if ( Integer.parseInt(map.get("Patrulla").toString()) == patrulla && map.get("Final") != null) {
                            mERState=false;
                            fondoPrincipal.setBackgroundColor(Color.BLACK);
                            if(Utilidades.emergenciaKey.contains(ds.getKey())) {
                                consola.setText(map.get("Tipo") + " FINALIZADO" + "\r\n" + map.get("Patrulla") + "\r\n" + map.get("Final"));
                                Utilidades.emergenciaKey.clear();
                            }
                        }
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(getApplicationContext(), "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });
            final HashMap<String,String> mensajesUsuario = new HashMap<>();
            databaseReference.child("mensajes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    mensajesUsuario.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        String idMensaje =  String.valueOf(map.get("mcptt_id"));
                        String tipo =  String.valueOf(map.get("Tipo"));
                        int patrullaMensaje = Integer.parseInt(String.valueOf(map.get("Patrulla")));
                        String mensajeString =  String.valueOf(map.get("Mensaje"));
                        String fecha = String.valueOf(map.get("Fecha"));
                        if(tipo.equals("imagen")){
                            mensajeString = "Imagen";
                        }
                        Mensaje notificacion = new Mensaje(ds.getKey(),idMensaje,patrulla,fecha,tipo,mensajeString);
                        if(patrullaMensaje == patrulla) {
                            if(!mcptt_id.equals(idMensaje)) {
                                if(Utilidades.mensajes.size()==0){
                                    //Utilidades.mensajes.add(notificacion);
                                    if(tipo.equals("imagen")) mensajeString = "imagen";
                                    mensajesUsuario.put(idMensaje, mensajeString);
                                    esta = true;
                                }
                                for(int i=0;i<Utilidades.mensajes.size();i++) {
                                    if(Utilidades.mensajes.get(i).getKey().equals(ds.getKey())) {
                                        esta = true;
                                        break;
                                    }
                                }
                                if(!esta){
                                    //Utilidades.mensajes.add(notificacion);
                                    if(tipo.equals("imagen")) mensajeString = "imagen";
                                    if (mensajesUsuario.containsKey(idMensaje)) {
                                        String otroMensaje = mensajesUsuario.get(idMensaje);
                                        mensajeTotal = otroMensaje + "\r\n" + mensajeString;
                                        mensajesUsuario.remove(idMensaje);
                                        mensajesUsuario.put(idMensaje, otroMensaje);
                                    } else {
                                        mensajesUsuario.put(idMensaje, mensajeString);
                                    }
                                }
                            }
                        }
                    }
                    for(String key: mensajesUsuario.keySet()) {
                        for(int i=0;i<Utilidades.unidades.size();i++){
                            if(Utilidades.unidades.get(i).getMcptt_id().equals(key)){
                                lanzarMensaje(Utilidades.unidades.get(i).getNombre(), mensajesUsuario.get(key));
                            }
                        }
                    }
                    esta = false;
                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(getApplicationContext(), "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });

            databaseReference.child("ordenes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                            if (Integer.parseInt(map.get("Patrulla").toString()) == patrulla && map.get("Fin").equals("")) {
                                lanzarNotificacion(map.get("Nombre").toString(), map.get("Direccion").toString());
                                consola.setText(map.get("Nombre") + "\r\n" + map.get("Direccion") + "\r\n" + map.get("Inicio"));
                            } else if (Integer.parseInt(map.get("Patrulla").toString()) == patrulla && map.get("Fin") != "") {
                                consola.setText(map.get("Nombre") + " FINALIZADO" + "\r\n" + map.get("Patrulla") + "\r\n" + map.get("Fin"));
                            }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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



