package com.example.android.bomberostfg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;

public class EnviarFotoDialog extends DialogFragment {
    private ListView listaUsuarios;
    private AdaptadorLista adaptador;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_usuario,null);
        View dialog = inflater.inflate(R.layout.enviarfotodialog,null);
        ArrayList<Unidad> lista = new ArrayList<Unidad>();
        // Use the Builder class for convenient dialog construction
        listaUsuarios = (ListView) dialog.findViewById(R.id.listaUsuarios);
        adaptador = new AdaptadorLista(getContext(), null);
        listaUsuarios.setAdapter(adaptador);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("¿A quién quieres enviar la foto?")
                .setView(dialog)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
