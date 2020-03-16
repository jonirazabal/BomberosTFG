package com.example.android.bomberostfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorLista extends BaseAdapter {
    private Context context;
    private ArrayList<Unidad> lista;

    public AdaptadorLista(Context context, ArrayList<Unidad> lista) {
        this.context = context;

    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Unidad usuario = lista.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_usuario, null);
        ImageView foto = (ImageView) view.findViewById(R.id.fotousuario);
        TextView nombre = (TextView) view.findViewById(R.id.nombreusuario);
        TextView location = (TextView) view.findViewById(R.id.location);
        return null;
    }
}
