package com.example.android.bomberostfg;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class fotoGrande extends DialogFragment {
    private Bitmap foto;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                // Aqui puedes capturar el OnBackPressed
                dismiss();
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public fotoGrande() {
    }

    public static fotoGrande newInstance(Bundle arguments) {
        Bundle args = arguments;
        fotoGrande fragment = new fotoGrande();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
        Bundle arguments = getArguments();
        foto = arguments.getParcelable("foto");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.foto_grande, container, false);

        ImageView ivImage = (ImageView)view.findViewById(R.id.fotoGrande);
        if(foto!=null) {
            ivImage.setImageBitmap(foto);
        }
        return view;
    }
}
