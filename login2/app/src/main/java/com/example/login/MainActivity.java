package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etUserName, etPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = findViewById(R.id.username);
        etPassword = findViewById(R.id.userpassword);
        btnSignIn = findViewById(R.id.signin);

        btnSignIn.setOnClickListener(v -> {
            String username = etUserName.getText().toString();
            String password = etPassword.getText().toString();

            if(validateUser(username) && validatePassword(password)){
                Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }

            Toast.makeText(this, "Username: " + username + " Password: " + password, Toast.LENGTH_LONG).show();

        });
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