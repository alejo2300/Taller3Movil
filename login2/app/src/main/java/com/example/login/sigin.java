package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class sigin extends AppCompatActivity {
    private static final int IMAGE_PICKER_REQUEST = 3;
    EditText mail, name, lname, passwd, iduser;
    Button siginBtn, selectPPBtn;
    ImageView profilePic;

    //create Firebase connection
    //Authentication
    FirebaseAuth mAuth;
    //Realtime db
    FirebaseDatabase database;
    DatabaseReference reference;
    //Storage
    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageProfilePicRef;
    //To get actual location
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);

        mail = findViewById(R.id.mail);
        name = findViewById(R.id.name);
        lname = findViewById(R.id.lastName);
        passwd = findViewById(R.id.password);
        iduser = findViewById(R.id.idNumber);
        profilePic = findViewById(R.id.profilePic);
        siginBtn = findViewById(R.id.signinSecond);
        selectPPBtn = findViewById(R.id.selectPP);

        mAuth = FirebaseAuth.getInstance(); //Get current instance of db from fb
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        if (mAuth.getCurrentUser() != null) { //If user is already logged in
            Intent intent = new Intent(sigin.this, mainMapsActivity.class);
            startActivity(intent);
        }

        selectPPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(sigin.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(sigin.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Without this permission we can not access to files", IMAGE_PICKER_REQUEST);
                } else {
                    chooseImage();
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(sigin.this);
        if (ActivityCompat.checkSelfPermission(sigin.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(sigin.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        siginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailS = mail.getText().toString().trim(); //Trim delete spaces before and after string
                String nameS = name.getText().toString().trim();
                String lnameS = lname.getText().toString().trim();
                String passwdS = passwd.getText().toString().trim();
                String iduserS = iduser.getText().toString().trim();

                if (TextUtils.isEmpty(mailS)) {
                    mail.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(nameS)) {
                    name.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(lnameS)) {
                    lname.setError("Last Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(passwdS)) {
                    passwd.setError("Password is required.");
                    return;
                }
                if (TextUtils.isEmpty(iduserS)) {
                    iduser.setError("Id number is required.");
                    return;
                }
                if (passwdS.length() < 6) {
                    passwd.setError("Password must be >= 6 characters.");
                    return;
                }

                Toast.makeText(sigin.this, "Signing in...", Toast.LENGTH_SHORT).show();
                UserClass userClass;
                if (currentLocation == null){
                    userClass = new UserClass(mailS, nameS, lnameS, passwdS, iduserS);
                } else {
                    userClass = new UserClass(mailS, nameS, lnameS, passwdS, iduserS, currentLocation.latitude, currentLocation.longitude);
                }

                UserClass finalUserClass = userClass;
                mAuth.createUserWithEmailAndPassword(mailS, passwdS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Save data in realtime database
                            FirebaseUser user = mAuth.getCurrentUser();
                            reference.child(user.getUid()).setValue(finalUserClass);
                            //Save profile pic
                            storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("profile_pic");
                            uploadProfileImage(user);

                            Toast.makeText(sigin.this, "User created", Toast.LENGTH_SHORT).show();
                            //Go to map activity
                            Intent intent = new Intent(sigin.this, mainMapsActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(sigin.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    public void requestPermission(Activity context, String permission, String justification, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                // Show an explanation to the user *asynchronously*
                Toast.makeText(context, justification, Toast.LENGTH_LONG).show();
            }
            // request the permission.
            ActivityCompat.requestPermissions(sigin.this,
                    new String[]{permission},
                    requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER_REQUEST) {
            if(resultCode == RESULT_OK){
                try {
                    imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    profilePic.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == IMAGE_PICKER_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(sigin.this, "You can not choose image from gallery without this permission", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 44) { //Fine location permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(sigin.this, "You can not save latitude and longitude without this permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void chooseImage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
            pickGalleryImage.setType("image/*");
            startActivityForResult(pickGalleryImage, IMAGE_PICKER_REQUEST);
        }
    }

    private void uploadProfileImage(FirebaseUser user) {
        if(imageUri != null) {
            final StorageReference fileRef = storageProfilePicRef.child(user.getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image", myUri);

                        reference.child(user.getUid()).updateChildren(userMap);
                    }
                }
            });
        } else {
            Toast.makeText(sigin.this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }
}