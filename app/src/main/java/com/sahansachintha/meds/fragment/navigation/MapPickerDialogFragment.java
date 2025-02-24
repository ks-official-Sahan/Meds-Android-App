// MapPickerDialogFragment.java
package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sahansachintha.meds.R;

public class MapPickerDialogFragment extends DialogFragment implements OnMapReadyCallback {

    public interface OnLocationSelectedListener {
        void onLocationSelected(LatLng latLng);
    }

    private GoogleMap gMap;
    private OnLocationSelectedListener listener;
    private LatLng initialLocation;
    private Marker marker;

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    public void setInitialLocation(LatLng latLng) {
        this.initialLocation = latLng;
    }

    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_map_picker, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        view.findViewById(R.id.btn_select).setOnClickListener(v -> {
            if (listener != null && marker != null) {
                listener.onLocationSelected(marker.getPosition());
            }
            dismiss();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        if (initialLocation == null) {
            initialLocation = new LatLng(37.4219999, -122.0840575);
        }
        marker = gMap.addMarker(new MarkerOptions().position(initialLocation).draggable(true));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
        gMap.setOnMapClickListener(latLng -> marker.setPosition(latLng));
    }
}
