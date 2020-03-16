package com.example.android.bomberostfg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentMaps extends Fragment {
    private GoogleMap googleMap;
    private static Marker[] actuaciones =null;
    private static BitmapDescriptor actuacionIcono;
    private HashMap<Integer, ClusterManager> listaCluster;
    private int idActuacion;
    private MapView mMapView;
    private Polyline polyline;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue request;
    private float latitudUnidad;
    private float longitudUnidad;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actuacionIcono = bitmapDescriptorFromVector(getContext(), R.mipmap.ic_actuacion);
        idActuacion = Utilidades.selectedActuacion.getId();
        if(savedInstanceState!=null) {
            latitudUnidad = savedInstanceState.getFloat("latitudUnidad", 0);
            longitudUnidad = savedInstanceState.getFloat("longitudUnidad", 0);
            if(latitudUnidad!=0 && longitudUnidad!=0){
                getCamino(Float.parseFloat(String.valueOf(Utilidades.actuacionFragment.latitude)),Float.parseFloat(String.valueOf(Utilidades.actuacionFragment.longitude)),
                        latitudUnidad, longitudUnidad, "driving");
            }
            else{
                polyline=null;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        mMapView = rootView.findViewById(R.id.mapFragment);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                createMarcadoresActuaciones();
                addItems();

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(Utilidades.actuacionFragment).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    public void createMarcadoresActuaciones() {
        actuaciones = new Marker[Utilidades.actuaciones.size()];
        for (int i = 0; i < Utilidades.actuaciones.size(); i++) {
            if(idActuacion == Utilidades.actuaciones.get(i).getId()) {
                        actuaciones[i] = googleMap.addMarker(new MarkerOptions()
                        .icon(actuacionIcono).snippet("Calcular KM hasta actuación")
                        .position(new LatLng(Utilidades.actuaciones.get(i).getLatitud(), Utilidades.actuaciones.get(i).getLongitud()))
                        .title(Utilidades.actuaciones.get(i).getNombre()).anchor(0.1f, 0.5f));
            }

        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 70  , 70);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addItems() {
        listaCluster = new HashMap<>();
        if(Utilidades.unidades.size()!=0) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Utilidades.unidades.get(0).getLatitud(), Utilidades.unidades.get(0).getLongitud()))
                    .title("Unidad : " + Utilidades.unidades.get(0).getPatrulla())
                    .snippet(Utilidades.unidades.get(0).getNombre())
                    .icon(bitmapDescriptorFromVector(getContext(), R.mipmap.ic_unidad));
            //mMap.addMarker(markerOptions);
            MyItem marcador = new MyItem(markerOptions);
            ClusterManager mClusterManager = new ClusterManager<MyItem>(getContext(), googleMap);
            //mMap.setOnCameraIdleListener(mClusterManager);
            ClusterRenderer clusterRenderer = new MyClusterRenderer(getContext(), googleMap, mClusterManager);
            mClusterManager.setRenderer(clusterRenderer);
            mClusterManager.addItem(marcador);
            listaCluster.put(Utilidades.unidades.get(0).getPatrulla(), mClusterManager);
            ;
            for (int i = 1; i < Utilidades.unidades.size(); i++) {
                float latitudCluster = Utilidades.unidades.get(i).getLatitud();
                float longitudCluster = Utilidades.unidades.get(i).getLongitud();
                int patrullaCluster = Utilidades.unidades.get(i).getPatrulla();
                String nombre = Utilidades.unidades.get(i).getNombre();
                markerOptions = new MarkerOptions()
                        .position(new LatLng(latitudCluster, longitudCluster))
                        .title("Unidad : " + patrullaCluster)
                        .snippet(nombre)
                        .icon(bitmapDescriptorFromVector(getContext(), R.mipmap.ic_unidad));
                marcador = new MyItem(markerOptions);
                //mMap.addMarker(markerOptions);
                if (listaCluster.containsKey(patrullaCluster)) {
                    listaCluster.get(patrullaCluster).addItem(marcador);
                } else {
                    mClusterManager = new ClusterManager<MyItem>(getContext(), googleMap);
                    clusterRenderer = new MyClusterRenderer(getContext(), googleMap, mClusterManager);
                    mClusterManager.setRenderer(clusterRenderer);
                    mClusterManager.addItem(marcador);
                    listaCluster.put(patrullaCluster, mClusterManager);
                }
            }
        }
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                for (Integer key:listaCluster.keySet())
                {
                    listaCluster.get(key).cluster();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void getCamino(double mylat, double mylong, double yourlat, double yourlong, String mode) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mylat
                + ',' + mylong + "&destination=" + yourlat + "," + yourlong + "&mode=" + mode + "&key=" + getString(R.string.google_maps_key);
        System.out.println("Getcamino: "+url);
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
                Toast.makeText(getContext(), "No se puede conectar " + error.toString(), Toast.LENGTH_LONG).show();
                Log.d("ERROR: ", error.toString());
            }
        });

        request.add(jsonObjectRequest);

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

        polyline = googleMap.addPolyline(lineOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        /////////////
    }
    public void deletePolyline(){
        polyline=null;
    }
}
