package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class sigin extends AppCompatActivity {
    EditText mail, name, lname, passwd, iduser;
    Button sigin;

    //create Firebase connection
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);

        mail = findViewById(R.id.mail);
        name = findViewById(R.id.name);
        lname = findViewById(R.id.lastName);
        passwd = findViewById(R.id.password);
        iduser = findViewById(R.id.idNumber);
        sigin = findViewById(R.id.signinSecond);

        sigin.setOnClickListener(v -> {
            Toast.makeText(sigin.this, "Signing in...", Toast.LENGTH_SHORT).show();
            String mailS = mail.getText().toString();
            String nameS = name.getText().toString();
            String lnameS = lname.getText().toString();
            String passwdS = passwd.getText().toString();
            String iduserS = iduser.getText().toString();

            if (mailS.isEmpty() || nameS.isEmpty() || lnameS.isEmpty() || passwdS.isEmpty() || iduserS.isEmpty()) {
                Toast.makeText(sigin.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.createUserWithEmailAndPassword(mailS, passwdS).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Go to map activity
                        Intent intent = new Intent(sigin.this, mainMapsActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(sigin.this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}