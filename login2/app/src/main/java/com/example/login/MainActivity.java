package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
    EditText etUserName, etPassword;
    Button buttonLgn, butttonSignIn;

    //open firebase database
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = findViewById(R.id.username);
        etPassword = findViewById(R.id.userpassword);
        buttonLgn = findViewById(R.id.login);
        butttonSignIn = findViewById(R.id.signin);

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){ //check if user is logged in
            Intent intent = new Intent(MainActivity.this, mainMapsActivity.class);
        }else {

            butttonSignIn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, sigin.class);
                startActivity(intent);
            });

            buttonLgn.setOnClickListener(v -> {
                Toast.makeText(this, "Login", Toast.LENGTH_LONG).show();
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();

                if (validateUser(username) && validatePassword(password)) {
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, mainMapsActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                Toast.makeText(this, "Username: " + username + " Password: " + password, Toast.LENGTH_LONG).show();

            });
        }
    }

    private void updateUI(FirebaseUser userreg) {
        if(userreg != null){
            Intent intent = new Intent(MainActivity.this, mainMapsActivity.class);
            startActivity(intent);
        }else {
            etUserName.setText("");
            etPassword.setText("");
        }
    }

    private boolean validatePassword(String password) {
        if(password.isEmpty()) {
            etPassword.setError("Password is required");
            Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show();
            return false;
        }else{
            if(password.length() >= 6){
                return true;
            }return false;
        }
    }

    private boolean validateUser(String username) {
        if(username.isEmpty()) {
            etUserName.setError("Username is required");
            Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show();
            return false;
        }else{
            if(username.contains("@") && username.contains(".")){
                return true;
            }return false;
        }
    }
}