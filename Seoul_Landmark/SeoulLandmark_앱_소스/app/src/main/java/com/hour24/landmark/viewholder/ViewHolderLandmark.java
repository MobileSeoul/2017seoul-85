package com.hour24.landmark.viewholder;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.fragment.GoogleMapFragment;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;

public class ViewHolderLandmark implements OnMapReadyCallback {

    private MainActivity activity;
    private View view;
    private FragmentManager fragmentManager;
    private SupportMapFragment mapFragment;

    private View touchViewMap;
    private TextView description;
    private GoogleMap googleMap;

    private EntityAnnotation data;


    public ViewHolderLandmark(MainActivity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.view = view;
        this.fragmentManager = fragmentManager;

        initLayout();
    }

    private void initLayout() {
        touchViewMap = (View) view.findViewById(R.id.touch_view_map);
        description = (TextView) view.findViewById(R.id.description);
    }

    public void bind(int position, VisionModel record) {

        mapFragment = (SupportMapFragment) fragmentManager.findFragmentByTag("mapFragment");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.map_container, mapFragment, "mapFragment");
            ft.commit();
        }

        mapFragment.getMapAsync(this);


        data = record.getLandmarkAnnotation();
        description.setText(data.getDescription());

        touchViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleMapFragment fragment = new GoogleMapFragment();
                fragment.setRecords(data);
                Utils.replaceFragment(fragmentManager, fragment);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        double latitude = data.getLocations().get(0).getLatLng().getLatitude();
        double longitude = data.getLocations().get(0).getLatLng().getLongitude();
        LatLng latlng = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions().position(latlng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));

    }
}
