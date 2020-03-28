package com.example.android.bomberostfg;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class BaseDeDatos extends AsyncTask<String, String,String> {
    RequestQueue requestQueue;
    private int responseCode=0;
    private String correctResponse="";
    private HashMap<String, String> params = new HashMap<>();
    private Context context;
    public static final int MY_DEFAULT_TIMEOUT = 3000;
    private static BaseDeDatos miBaseDeDatos= null;
    private int codigo, patrulla;
    private String nombre,mcptt_id;
    private String especialidad;
    private boolean respuesta=false;
    private float latitud;
    private float longitud;
    private Unidad bombero;
    int idPatrulla;
    private String ip_servidor = "https://134.209.235.115/jirazabal004/WEB/";
    private String contraseña = "UvAWaI2JyH";
    private HttpsURLConnection urlConnection;
    ArrayList<String> patrullas = new ArrayList<>();
    public BaseDeDatos(Context pcontext) {
        context=pcontext;
        conexion("https://134.209.235.115/jirazabal004/WEB/");
    }

    public static BaseDeDatos getDatabase(Context pcontext) {
        if(miBaseDeDatos==null){
            miBaseDeDatos = new BaseDeDatos(pcontext);
        }
        return miBaseDeDatos;
    }
    private void conexion(String url){
        urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context,url);
    }
    private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String,String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    @Override
    protected String doInBackground(String[] strings) {
        URL url =null;
        params = new HashMap<String,String>();
        try{
            url = new URL("https://134.209.235.115/jirazabal004/WEB/"+strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context,url.toString());
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoOutput(true);
        switch (strings[0]) {
            case "selectUnidades.php":
                try {
                    urlConnection.setRequestMethod("GET");
                    correctResponse = "Unidades seleccionadas correctamente";
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "selectActuaciones.php":
                try {
                    urlConnection.setRequestMethod("GET");
                    correctResponse = "Actuaciones seleccionadas correctamente";

                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "selectPatrullas.php":
                try {
                    urlConnection.setRequestMethod("GET");
                    correctResponse = "Patrullas seleccionadas correctamente";
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "selectpatrullaActuaciones.php":
                try {
                    urlConnection.setRequestMethod("GET");
                    correctResponse = "patrullaActuaciones seleccionadas correctamente";

                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "actualizarUnidad.php":
                try {
                    correctResponse = "Unidad insertada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("mcptt_id", strings[1]);
                    params.put("foto", strings[2]);
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "insertarUnidad.php":
                try {
                    correctResponse = "Unidad actualizada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("mcptt_id", strings[1]);
                    params.put("nombre", strings[2]);
                    params.put("especialidad", strings[3]);
                    params.put("patrulla", strings[4]);
                    params.put("latitud", strings[5]);
                    params.put("longitud", strings[6]);
                    System.out.println("PARAMS: "+params.toString());
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "eliminarActuacion.php":
                try {
                    correctResponse = "Actuacion eliminada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("id", strings[1]);
                    params.put("fecha", strings[2]);
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "insertarActuacion.php":
                try {
                    correctResponse = "Actuación insertada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("tipo", strings[1]);
                    params.put("nombre", strings[2]);
                    params.put("latitud", strings[3]);
                    params.put("longitud", strings[4]);
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "insertarpatrullaActuacion.php":
                try {
                    correctResponse = "patrullaActuación insertada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("idPatrulla", strings[1]);
                    params.put("idActuacion", strings[2]);
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "actualizarLatLongUnidad.php":
                try {
                    correctResponse = "Latitud y longitud actualizada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("mcptt_id", strings[1]);
                    params.put("latitud", strings[2]);
                    params.put("longitud", strings[3]);
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "actualizarPatrulla.php":
                try {
                    correctResponse = "Patrulla actualizada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("patrulla", strings[1]);
                    if (strings[2].equals("Incendios")) {
                        params.put("incendio", "1");
                    } else if (strings[2].equals("Salvamentos")) {
                        params.put("salvamento", "1");
                    } else if (strings[2].equals("Asistencias")) {
                        params.put("asistencia", "1");
                    }
                    if(strings[3]!=null) {
                        params.put("operativa", strings[3]);
                        break;
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            case "insertarPatrulla.php":
                try {
                    correctResponse = "Patrulla insertada correctamente";
                    urlConnection.setRequestMethod("POST");
                    params.put("patrulla", strings[1]);
                    if (strings[2].equals("Incendios")) {
                        params.put("incendio", "1");
                    } else if (strings[2].equals("Salvamentos")) {
                        params.put("salvamento", "1");
                    } else if (strings[2].equals("Asistencias")) {
                        params.put("asistencia", "1");
                    }
                    break;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
        }
        if(urlConnection.getRequestMethod().equals("POST")) {
            try {

                DataOutputStream wr = null;
                  byte[] postData = getQuery(params).getBytes(Charset.forName("UTF-8"));
                int    postDataLength = postData.length;
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                //urlConnection.getOutputStream().write(postData);
                wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );
                wr.flush();
                wr.close();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
            try {
                urlConnection.connect();
                responseCode = urlConnection.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    if(strings[0].equals("selectActuaciones.php")) {
                        Utilidades.actuaciones.clear();
                        añadirActuaciones(sb.toString());
                    }
                    if(strings[0].equals("selectPatrullas.php")) {
                        Utilidades.patrullas.clear();
                        añadirPatrullas(sb.toString());
                    }
                    if(strings[0].equals("selectUnidades.php")) {
                        Utilidades.unidades.clear();
                        añadirUnidades(sb.toString());
                    }
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    private void añadirPatrullas(String result) {
        Utilidades.patrullas = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Patrulla patrulla = new Patrulla();
                patrulla.setId(jsonObject.getInt("id"));
                patrulla.setIncendio(jsonObject.getInt("incendio"));
                patrulla.setSalvamento(jsonObject.getInt("salvamento"));
                patrulla.setAsistencia(jsonObject.getInt("asistencia"));
                patrulla.setOperativa(jsonObject.getInt("operativa"));
                Utilidades.patrullas.add(patrulla);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        //Toast.makeText(context, correctResponse, Toast.LENGTH_SHORT).show();
        super.onPostExecute(s);
    }

    private void añadirActuaciones(String result) {
        Utilidades.actuaciones = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Actuacion actuacion = new Actuacion();
                actuacion.setId(jsonObject.getInt("id"));
                actuacion.setTipo(jsonObject.getString("tipo"));
                actuacion.setNombre(jsonObject.getString("nombre"));
                actuacion.setLatitud(Float.parseFloat(String.valueOf(jsonObject.getDouble("latitud"))));
                actuacion.setLongitud(Float.parseFloat(String.valueOf(jsonObject.getDouble("longitud"))));
                Utilidades.actuaciones.add(actuacion);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void añadirUnidades(String result) {
        Utilidades.unidades = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Unidad unidad = new Unidad();
                unidad.setMcptt_id(jsonObject.getString("mcptt_id"));
                unidad.setNombre(jsonObject.getString("nombre"));
                unidad.setEspecialidad(jsonObject.getString("especialidad"));
                unidad.setPatrulla(jsonObject.getInt("patrulla"));
                unidad.setLatitud(Float.parseFloat(String.valueOf(jsonObject.getDouble("latitud"))));
                unidad.setLongitud(Float.parseFloat(String.valueOf(jsonObject.getDouble("longitud"))));
                unidad.setFoto(jsonObject.getString("foto"));
                Utilidades.unidades.add(unidad);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

