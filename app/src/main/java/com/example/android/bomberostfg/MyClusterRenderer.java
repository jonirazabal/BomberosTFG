package com.example.android.bomberostfg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import androidx.core.content.ContextCompat;

public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> implements GoogleMap.OnCameraIdleListener{
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private GoogleMap mMap;
    private float maxZoomLevel=12.0f;
    private ClusterManager mClusterManager;
    private final IconGenerator mClusterIconGenerator;
    private Context mContext;
    private float currentZoomLevel;
    public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        mMap = map;
        mContext = context;
        currentZoomLevel = mMap.getCameraPosition().zoom;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        imageView.setImageResource(R.mipmap.ic_unidad);
        markerWidth = 70;
        markerHeight = 70;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        iconGenerator.setContentView(imageView);
        mClusterManager = clusterManager;
// in constructor
        mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(mContext, R.mipmap.ic_patrulla_round));
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap( icon.createScaledBitmap(icon,100,100,false)));

    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onClusterRendered(Cluster<MyItem> cluster, Marker marker) {


    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
        return currentZoomLevel< 15.0f &&cluster.getSize()>=3;
    }


    @Override
    public void onCameraIdle() {
        currentZoomLevel = mMap.getCameraPosition().zoom;
    }
}
