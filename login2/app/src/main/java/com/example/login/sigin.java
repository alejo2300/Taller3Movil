package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class sigin extends AppCompatActivity {
    EditText mail, name, lname, passwd, iduser;
    Button sigin;

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
            String mailS = mail.getText().toString();
            String nameS = name.getText().toString();
            String lnameS = lname.getText().toString();
            String passwdS = passwd.getText().toString();
            String iduserS = iduser.getText().toString();

            if (mailS.isEmpty() || nameS.isEmpty() || lnameS.isEmpty() || passwdS.isEmpty() || iduserS.isEmpty()) {
                Toast.makeText(sigin.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            }else{

            }
        });
    }
}