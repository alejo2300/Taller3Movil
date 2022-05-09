package com.example.login;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.login.databinding.ActivityMainMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mainMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMainMapsBinding binding;
    //User
    FirebaseAuth mAuth;
    FirebaseUser loggedUser;
    LatLng userLocation;
    //Realtime db
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loggedUser = mAuth.getCurrentUser();
        if (loggedUser != null) { //If user is already logged in
            Toast.makeText(mainMapsActivity.this, loggedUser.getUid(), Toast.LENGTH_LONG).show();
        }
        loadUsers();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void loadUsers() {
        reference = database.getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    UserClass myUser = singleSnapshot.getValue(UserClass.class);
                    Log.i("LoadUser", "Encontró usuario: " + myUser.getName());
                    if (myUser.getEmail().equals(loggedUser.getEmail())){
                        if (myUser.getLatitude() == null && myUser.getLongitude() == null){
                            Toast.makeText(mainMapsActivity.this, "User does not have location", Toast.LENGTH_LONG).show();
                        } else {
                            userLocation = new LatLng(myUser.getLatitude(), myUser.getLongitude());
                            setNewPosition(userLocation);
                            Toast.makeText(mainMapsActivity.this, userLocation.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LoadUser", "error en la consulta", databaseError.toException());
            }
        });
    }

    private  void setNewPosition(LatLng curPos){
        //Clear old markers
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(curPos).title("Posición actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
    }

}