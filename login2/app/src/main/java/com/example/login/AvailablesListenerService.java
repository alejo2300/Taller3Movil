package com.example.login;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AvailablesListenerService extends IntentService {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser loggedUser;
    public static String CHANNEL_ID = "MyApp";
    int notificationId = 0;

    /*// Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.login.action.FOO";
    private static final String ACTION_BAZ = "com.example.login.action.BAZ";

    // Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.login.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.login.extra.PARAM2";*/

    public AvailablesListenerService() {
        super("AvailablesListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("AvailablesListenerService", "Servicio en ejecución" );

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loggedUser = mAuth.getCurrentUser();
        //Listen changes on available users
        reference = database.getReference("users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserClass myUser = snapshot.getValue(UserClass.class);
                if (myUser.isAvailable()) {
                    Log.i("ChildEvent", "User is now available");
                    Log.i("ChildEventDataChanged", "Data: " + myUser.getName() + " available: " + String.valueOf(myUser.isAvailable()));
                    createNotificationChannel();
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AvailablesListenerService.this, CHANNEL_ID);
                    mBuilder.setSmallIcon(R.drawable.ic_baseline_notification_important);
                    mBuilder.setContentTitle("User " + myUser.getName() + " " + myUser.getLastname() + " is now available!");
                    mBuilder.setContentText("Click here to watch its location!");
                    mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    Intent intent;
                    if(loggedUser != null){
                        Log.i("Notification", "Going to show map");
                        intent = new Intent(AvailablesListenerService.this, MainActivity.class);
                    } else {
                        intent = new Intent(AvailablesListenerService.this, MainActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(AvailablesListenerService.this, 0, intent, 0);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca

                    notificationId = 001;
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(AvailablesListenerService.this);
                    // notificationId es un entero unico definido para cada notificacion que se lanza
                    notificationManager.notify(notificationId, mBuilder.build());

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
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel descrip2on";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other no8fica8on behaviors aTer this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}