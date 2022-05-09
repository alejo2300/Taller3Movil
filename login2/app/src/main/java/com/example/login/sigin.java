package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sigin extends AppCompatActivity {
    EditText mail, name, lname, passwd, iduser;
    Button siginBtn;

    //create Firebase connection
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);

        mail = findViewById(R.id.mail);
        name = findViewById(R.id.name);
        lname = findViewById(R.id.lastName);
        passwd = findViewById(R.id.password);
        iduser = findViewById(R.id.idNumber);
        siginBtn = findViewById(R.id.signinSecond);

        mAuth = FirebaseAuth.getInstance(); //Get current instance of db from fb
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        if(mAuth.getCurrentUser() != null) { //If user is already logged in
            Intent intent = new Intent(sigin.this, mainMapsActivity.class);
            startActivity(intent);
        }

        siginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailS = mail.getText().toString().trim(); //Trim delete spaces before and after string
                String nameS = name.getText().toString().trim();
                String lnameS = lname.getText().toString().trim();
                String passwdS = passwd.getText().toString().trim();
                String iduserS = iduser.getText().toString().trim();

                if (TextUtils.isEmpty(mailS)){
                    mail.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(nameS)){
                    name.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(lnameS)){
                    lname.setError("Last Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(passwdS)){
                    passwd.setError("Password is required.");
                    return;
                }
                if (TextUtils.isEmpty(iduserS)){
                    iduser.setError("Id number is required.");
                    return;
                }
                if (passwdS.length() < 6) {
                    passwd.setError("Password must be >= 6 characters.");
                    return;
                }
                Toast.makeText(sigin.this, "Signing in...", Toast.LENGTH_SHORT).show();
                UserClass userClass = new UserClass(mailS, nameS, lnameS, passwdS, iduserS);

                mAuth.createUserWithEmailAndPassword(mailS, passwdS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            reference.child(user.getUid()).setValue(userClass);
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
}