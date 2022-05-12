package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAvailablesActivity extends AppCompatActivity {
    private RecyclerView availablesList;
    FirebaseRecyclerAdapter<UserClass, ListAvailablesActivity.myViewHolder> adapter;

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

        adapter = new FirebaseRecyclerAdapter<UserClass, ListAvailablesActivity.myViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull UserClass model) {
                        Log.i("holder", "saving data");
                        holder.name.setText(model.getName());
                        holder.email.setText(model.getEmail());
                        holder.location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("Location Button", "Lat "+model.getLatitude()+" long "+model.getLongitude());
                                if (model.getLatitude() == null) {
                                    Toast.makeText(ListAvailablesActivity.this, "User does not have location", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ListAvailablesActivity.this, "User has location", Toast.LENGTH_SHORT).show();
                                    /*Intent intent = new Intent(ListAvailablesActivity.this, MapUserActivity.class);
                                    intent.putExtra("lat", model.getLatitude());
                                    intent.putExtra("lng", model.getLongitude());
                                    startActivity(intent);*/

                                    //Create LatLng object for the user
                                    LatLng userLocation = new LatLng(model.getLatitude(), model.getLongitude());


                                    Intent intent = new Intent(ListAvailablesActivity.this, mapAndMenu.class);
                                    intent.putExtra("name", model.getName());
                                    intent.putExtra("userLocation", userLocation);
                                    startActivity(intent);
                                }
                            }
                        });

                        if (model.getImage() != null) {
                            Glide.with(holder.img.getContext())
                                    .load(model.getImage())
                                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                                    .circleCrop()
                                    .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                                    .into(holder.img);
                        } else {
                            Glide.with(holder.img.getContext())
                                    .load(R.drawable.user)
                                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                                    .circleCrop()
                                    .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                                    .into(holder.img);
                        }
                    }

                    @NonNull
                    @Override
                    public ListAvailablesActivity.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                        return new myViewHolder(view);
                    }
                };

        availablesList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name, email;
        Button location;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.userImage);
            name = (TextView) itemView.findViewById(R.id.userName);
            email = (TextView) itemView.findViewById(R.id.userEmail);
            location = (Button) itemView.findViewById(R.id.userLocation);
        }
    }
}