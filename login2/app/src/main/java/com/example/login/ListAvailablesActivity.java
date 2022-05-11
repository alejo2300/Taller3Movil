package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ListAvailablesActivity extends AppCompatActivity {
    private RecyclerView availablesList;
    AvailablesAdapter availablesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_availables);

        availablesList = (RecyclerView) findViewById(R.id.availablesList);
        availablesList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<UserClass> options =
                new FirebaseRecyclerOptions.Builder<UserClass>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("available").equalTo(true), UserClass.class)
                        .build();

        availablesAdapter = new AvailablesAdapter(options);
        availablesList.setAdapter(availablesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        availablesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        availablesAdapter.stopListening();
    }
}