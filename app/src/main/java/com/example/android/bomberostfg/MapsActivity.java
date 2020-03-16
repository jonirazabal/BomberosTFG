package com.example.android.bomberostfg;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.ClusterItem;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationClickListener {
    private String mcptt_id="";
    private HashMap<Integer, ClusterManager> listaCluster;
    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude = 0.00;
    JsonObjectRequest jsonObjectRequest;
    private double latobjetivo = 0;
    private double longobjetivo = 0;
    private boolean touched = false;
    RequestQueue request;
    BaseDeDatos database;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Marker objetivo;
    private Polyline polyline;
    private static Marker[] marcadores =null;
    private static Marker[] actuaciones =null;
    private static final int ACCESS_FINE_LOCATION = 61;
    private static BitmapDescriptor icono,actuacionIcono;
    private String pueblo, calle = "";
    private ArrayList<ClusterManager<MyItem>>listaClusterManager=new ArrayList<>();
    private ClusterRenderer clusterRenderer;
    private ClusterManager mClusterManager;
    private boolean zoomDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        database = new BaseDeDatos(this);
        Intent intent = getIntent();
        latitude = intent.getFloatExtra("latitud", (float) 0.00);
        longitude = intent.getFloatExtra("longitud", (float) 0.00);
        mcptt_id = intent.getStringExtra("mcptt_id");
        icono = bitmapDescriptorFromVector(this, R.mipmap.ic_unidad);
        actuacionIcono = bitmapDescriptorFromVector(this, R.mipmap.ic_actuacion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        request = Volley.newRequestQueue(getApplicationContext());
        mapFragment.getMapAsync(this);
        zoomDone=false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if(latitude!=0 && longitude!=0){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11.0f));
            System.out.println("Camera: " +latitude+", "+longitude);
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.25, -2.83), 9.0f));
            System.out.println("Camera: " +latitude+", "+longitude);

        }
        createMarcadoresActuaciones();
        mMap.setOnMarkerClickListener(this);
        //createMarcadoresUnidades();
        addItems();

       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (touched) {
                    objetivo.remove();
                    polyline.remove();
                    touched = false;
                } else {
                    latobjetivo = latLng.latitude;
                    longobjetivo = latLng.longitude;
                    objetivo = mMap.addMarker(new MarkerOptions().position(latLng).title("Tu objetivo"));
                    touched = true;
                    Log.d("Request", request.toString());
                    getCamino(latitude, longitude, latobjetivo, longobjetivo, "driving");
                }
            }
        });
        */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < actuaciones.length; i++) {
                    if (marker.equals(actuaciones[i])) {
                        Intent intent = new Intent(getApplicationContext(), GestionarUnidades.class);
                        intent.putExtra("id", Utilidades.actuaciones.get(i).getId());
                        intent.putExtra("nombre", Utilidades.actuaciones.get(i).getNombre());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void addItems() {
        MarkerOptions markerOptions;
        MyItem marcador;
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        clusterRenderer = new MyClusterRenderer(this, mMap, mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        if(Utilidades.unidades.size()!=0) {

            for (int i = 1; i < Utilidades.unidades.size(); i++) {
                float latitudCluster = Utilidades.unidades.get(i).getLatitud();
                float longitudCluster = Utilidades.unidades.get(i).getLongitud();
                final int patrullaCluster = Utilidades.unidades.get(i).getPatrulla();
                String nombre = Utilidades.unidades.get(i).getNombre();
                markerOptions = new MarkerOptions()
                        .position(new LatLng(latitudCluster, longitudCluster))
                        .title(nombre)
                        .snippet(String.valueOf(patrullaCluster))
                        .icon(bitmapDescriptorFromVector(this, R.mipmap.ic_unidad_round));
                marcador = new MyItem(markerOptions);
                //mMap.addMarker(markerOptions);

                    mClusterManager.setAnimation(true);
                    mClusterManager.setRenderer(clusterRenderer);
                    mClusterManager.addItem(marcador);

                    mMap.setOnCameraIdleListener(mClusterManager);
                    mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener() {
                        @Override
                        public boolean onClusterClick(Cluster cluster) {
                            if(!zoomDone){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cluster.getPosition().latitude,cluster.getPosition().longitude),
                                    mMap.getCameraPosition().zoom+3));
                                zoomDone=true;
                            }
                            else{
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cluster.getPosition().latitude,cluster.getPosition().longitude),
                                        mMap.getCameraPosition().zoom+1));
                            }
                            return true;
                        }
                    });
                }

            }
        }


    private static void clearMarcadoresActuaciones() {
        if (actuaciones != null) {
            for (int i = 0; i < actuaciones.length; i++) {
                if(actuaciones[i]!=null) {
                    actuaciones[i].remove();
                }
            }
        }
    }

    public static void createMarcadoresActuaciones() {
        clearMarcadoresActuaciones();
        actuaciones = new Marker[Utilidades.actuaciones.size()];
        for (int i = 0; i < Utilidades.actuaciones.size(); i++) {
            actuaciones[i] = mMap.addMarker(new MarkerOptions()
                    .icon(actuacionIcono).snippet("Click para ver recursos")
                    .position(new LatLng(Utilidades.actuaciones.get(i).getLatitud(), Utilidades.actuaciones.get(i).getLongitud()))
                    .title(Utilidades.actuaciones.get(i).getNombre()).anchor(0.1f, 0.5f));

        }
    }


    public void createMarcadoresUnidades() {
       /* clearMarcadoresUnidades();
        marcadores = new Marker[Utilidades.unidades.size()+Utilidades.actuaciones.size()];
        Log.d("Actuaciones", String.valueOf(Utilidades.actuaciones.size()));
        Log.d("Unidades", String.valueOf(Utilidades.unidades.size()));

        for (int i = 0; i < Utilidades.unidades.size(); i++) {
            marcadores[i] = mMap.addMarker(new MarkerOptions()
                    .icon(icono)
                    .position(new LatLng(Utilidades.unidades.get(i).getLatitud(), Utilidades.unidades.get(i).getLongitud()))
                    .title("Unidad " + Utilidades.unidades.get(i).getId()).anchor(0.1f, 0.5f));
        }

        */
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 70, 70);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMyLocationClick(Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    public void getCamino(double mylat, double mylong, double yourlat, double yourlong, String mode) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mylat
                + ',' + mylong + "&destination=" + yourlat + "," + yourlong + "&mode=" + mode + "&key=" + getString(R.string.google_maps_key);
        Log.d("url", url);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;
                try {
                    jRoutes = response.getJSONArray("routes");
                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */

                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                    hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                    path.add(hm);
                                }
                            }
                            Log.wtf("path", path.toString());
                            Utilidades.routes.add(path);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                }
                drawRoutes();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar " + error.toString(), Toast.LENGTH_LONG).show();
                Log.d("ERROR: ", error.toString());
            }
        });

        request.add(jsonObjectRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void drawRoutes() {
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for (int i = 0; i < Utilidades.routes.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(4);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta

            polyline = mMap.addPolyline(lineOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        /////////////
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        database = new BaseDeDatos(this);

        for (int i = 0; i < actuaciones.length; i++) {
            if (marker.equals(actuaciones[i])) {
                Intent intent = new Intent(getApplicationContext(), GestionarUnidades.class);
                intent.putExtra("id", Utilidades.actuaciones.get(i).getId());
                intent.putExtra("nombre", Utilidades.actuaciones.get(i).getNombre());

                Utilidades.actuacionFragment = new LatLng(actuaciones[i].getPosition().latitude, actuaciones[i].getPosition().longitude);
                Utilidades.selectedActuacion = Utilidades.actuaciones.get(i);
                startActivity(intent);
                return true;
            }
            else{
                Log.d("Marcadores", "Distinto");
            }
        }
        return false;
    }

}
