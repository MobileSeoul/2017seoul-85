package com.hour24.landmark.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;

public class GoogleMapFragment extends BaseFragment implements OnMapReadyCallback {

    private MainActivity activity;

    private View rootView;

    private EntityAnnotation records;

    public GoogleMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        initLayout(view);
        initVariable();
    }

    private void initLayout(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.viewer_map);
        mapFragment.getMapAsync(this);

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    private void initVariable() {
    }

    public void setRecords(EntityAnnotation records) {
        this.records = records;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        double latitude = 37.566312;
        double longitude = 126.977953;
        if (records != null) {
            latitude = records.getLocations().get(0).getLatLng().getLatitude();
            longitude = records.getLocations().get(0).getLatLng().getLongitude();
        }

        LatLng latlng = new LatLng(latitude, longitude);

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        googleMap.addMarker(new MarkerOptions().position(latlng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

    }
}
