package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.login.databinding.ActivityMainMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class mapAndMenu extends AppCompatActivity {

    //User
    FirebaseAuth mAuth;
    FirebaseUser loggedUser;
    LatLng userLocation;
    //Realtime db
    FirebaseDatabase database;
    DatabaseReference reference;
    //Markers
    Marker dbPosition;
    LatLng dbLocation;

    ArrayList<LatLng> locationsLatLng = new ArrayList<>();
    ArrayList<String> locationsName = new ArrayList<>();

    String otherName = null;
    LatLng otherLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_menu);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loggedUser = mAuth.getCurrentUser();

        if (loggedUser != null) { //If user is already logged in
            Toast.makeText(mapAndMenu.this, loggedUser.getUid(), Toast.LENGTH_LONG).show();
        }
        loadUsers();
        readJsonFile();


        //Listen changes on available users
        /*reference = database.getReference("users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserClass myUser = snapshot.getValue(UserClass.class);
                if (myUser.isAvailable()) {
                    Log.i("ChildEvent", "User is now available");
                    Toast.makeText(mapAndMenu.this, "Data: " + myUser.getName() + " available: " + String.valueOf(myUser.isAvailable()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        otherName = getExtra();
        if (otherName != null) {
            otherLocation = getLocation();
            /*FragmentManager fm = getSupportFragmentManager();
            MapsFragment fragment = (MapsFragment) fm.findFragmentById(R.id.map);
            fragment.setClickedUserPosition(otherLocation,otherName);*/
        }
    }

    public String getOtherName(){
        return otherName;
    }

    public LatLng getOtherLocation(){
        return otherLocation;
    }

    private LatLng getLocation() {
        LatLng otherLocation = null;
        if (getIntent().hasExtra("userLocation")) {
            otherLocation = getIntent().getParcelableExtra("userLocation");
        }
        return otherLocation;
    }

    private String getExtra() {
        String newName = null;
        if (getIntent().hasExtra("name")) {
            newName = getIntent().getStringExtra("name");
            Log.i("Extra", newName);
            System.out.println("Extra: " + newName);
            Toast.makeText(mapAndMenu.this, "Extra: " + newName, Toast.LENGTH_LONG).show();
        }
        return newName;
    }

    public LatLng getDbPosition() {
        return dbLocation;
    }

    public ArrayList<LatLng> getLocationsLatLng() {
        return locationsLatLng;
    }

    public ArrayList<String> getLocationsName() {
        return locationsName;
    }

    private void readJsonFile() {
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset());
            JSONArray jsonArray = jsonObject.getJSONArray("locationsArray");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject locationData = jsonArray.getJSONObject(i);
                locationsLatLng.add(new LatLng(locationData.getDouble("latitude"), locationData.getDouble("longitude")));
                locationsName.add(locationData.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int j = 0; j < locationsLatLng.size(); j++) {
            Log.i("Get json data",
                    j + ": lat " + locationsLatLng.get(j).latitude +
                            ", long " + locationsLatLng.get(j).longitude +
                            ", name " + locationsName.get(j));
            //mMap.addMarker(new MarkerOptions().position(locationsLatLng.get(j)).title(locationsName.get(j)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
    }

    private String JsonDataFromAsset() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("locations.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
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
                            Toast.makeText(mapAndMenu.this, "User does not have location", Toast.LENGTH_LONG).show();
                        } else {
                            userLocation = new LatLng(myUser.getLatitude(), myUser.getLongitude());
                            dbLocation = userLocation;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.menuLogOut) {
            logout();
        } else if (itemClicked == R.id.menuAvailable){
            //Set user as available
            setUserAvailable();
        } else if (itemClicked == R.id.menuListAvailables){
            //Show available users
            //Toast.makeText(mapAndMenu.this, "list available", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mapAndMenu.this, ListAvailablesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(mapAndMenu.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setUserAvailable() {
        reference = database.getReference("users/"+loggedUser.getUid());
        if (reference.child("available").equals(true)) {
            Toast.makeText(mapAndMenu.this, "User is already available", Toast.LENGTH_LONG).show();
        } else {
            reference.child("available").setValue(true);
            Toast.makeText(mapAndMenu.this, "User is now available", Toast.LENGTH_LONG).show();
        }
    }
}