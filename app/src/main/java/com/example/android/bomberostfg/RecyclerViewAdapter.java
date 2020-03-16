package com.example.android.bomberostfg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> nombres;
        private ArrayList<Integer> imagenes;
        private static final String TAG = "RecyclerViewAdapter";
        private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> nombres, ArrayList<Integer> imagenes, Context mContext) {
        this.nombres = nombres;
        this.imagenes = imagenes;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergencia_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.icono.setImageResource(imagenes.get(position));
        holder.descripcion.setText(nombres.get(position));
        holder.icono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0){
                    Toast.makeText(mContext, "Orden de evacuacion mandada", Toast.LENGTH_SHORT).show();
                    sonarEmergencia("Evacuacion inmediata",MainActivity.patrulla);}
                if(position == 1){
                    Toast.makeText(mContext, "Orden de inmobilización mandada", Toast.LENGTH_SHORT).show();
                    sonarEmergencia("Inmobilización general",MainActivity.patrulla);}
                if(position == 2){
                    Toast.makeText(mContext, "Orden de informe mandada", Toast.LENGTH_SHORT).show();
                    sonarEmergencia("Informe de situación", MainActivity.patrulla);}
            }
        });
        System.out.println(MainActivity.patrulla);
    }

    @Override
    public int getItemCount() {
        return nombres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView descripcion;
        private ImageView icono;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            descripcion = (TextView) itemView.findViewById(R.id.textoEmergencia);
            icono = (ImageView) itemView.findViewById(R.id.imageEmergencia);
        }

    }
    public String sonarEmergencia(String evacuacion, int patrulla) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);
        String key = FirebaseActivity.getDatabase(mContext).insertarEmergencia(evacuacion,patrulla ,fecha);
        return key;
    }
}
