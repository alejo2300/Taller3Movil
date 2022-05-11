package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

            //Get user position and set marker
            mapAndMenu mapActivity = (mapAndMenu) getActivity();
            LatLng dbPosition = mapActivity.getDbPosition();
            if (dbPosition != null) {
                googleMap.addMarker(new MarkerOptions().position(dbPosition).title("Marker user in DB"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(dbPosition));
                Toast.makeText(getContext(), "User position set", Toast.LENGTH_SHORT).show();
            }

            //Put markers readed in Json(First read in mapAndMenu and we send it to this fragment)
            ArrayList<LatLng> markersPositions = mapActivity.getLocationsLatLng();
            ArrayList<String> markersNames = mapActivity.getLocationsName();
            for (int i = 0; i < markersPositions.size(); i++) {
                if(markersNames.get(i) != null && markersPositions.get(i) != null) {
                    googleMap.addMarker(new MarkerOptions().position(markersPositions.get(i)).title(markersNames.get(i)));
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}