package com.example.mygarden;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //recycler view reference
    private RecyclerView recyclerView;
    //adapter specifies the layout of recycler view
    private RecyclerView.Adapter adapter;
    //array list of sensors read from the database
    private ArrayList<Sensor> sensors = new ArrayList<>();
    //constant string specifying the Tag of the activity
    final private String TAG = "MainActivity";
    //gets an instance of the realtime database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //gets the reference of the database at the url
    DatabaseReference myRef = database.getReferenceFromUrl("https://ratemytreater.firebaseio.com/Sensors");
    //Takes a snapshot of the current state of the database
    DataSnapshot newDataSnapshot;
    //token for FCM
    private String FCMtoken;
    private static final int PERMISSION_REQUEST_CODE = 1;


    //Method called when the activity has been created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets the firebase token
        FCMtoken = FirebaseInstanceId.getInstance().getToken();
        //gets the associated xml file
        setContentView(R.layout.activity_main);
        //calls method which attaches listener to database reference
        basicReadWrite();
        //Not really sure what this does, something related to the layout
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(sensors, this);
        recyclerView.setAdapter(adapter);
        //setting up the on click listener for the fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refreshes data on click
                Log.w(TAG, "onClick:" + sensors.get(0).getValue());
                //in the rare instance where the data is not refreshed automatically
                adapter = new Adapter(sensors, getApplicationContext());
                //recycler view will update on click
                recyclerView.setAdapter(adapter);
                //logs the token as well
                Log.w(TAG, "Token: " + FCMtoken);
            }
        });
    }

    public void basicReadWrite(){

        //ads a listener to the sensors in the database
        myRef.addValueEventListener(new ValueEventListener() {
            //this method returns a snapshot of the db whenever there's a change
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //replace the old pic with the new one
                newDataSnapshot = dataSnapshot;
                //calls method which takes info and creates a new sensor object or updates old one
                showData(dataSnapshot);
            }
            //in case there is an error reading data
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //method called when data is updated
    private void showData(DataSnapshot dataSnapshot) {
        //loops through the children of the root node
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //checks to see if the entry exists in the array list
            if(hasPosition(sensors, ds.getKey()) != null) {
                //updates the moisture level if the entry already exists
                Sensor x = hasPosition(sensors, ds.getKey());
                //sets the moisture level to the new content
                x.setValue(ds.child("Moisture").getValue(Integer.class));
            }
            //else adds the new sensor to the card view
            else{
                //creates a new sensor object
                Sensor x = new Sensor(ds.getKey(), ds.child("Moisture").getValue(Integer.class), ds.child("Image").getValue(String.class));
                //adds the sensor to the array list
                sensors.add(x);
            }
        }
        //updates the recycler view adapter
        adapter = new Adapter(sensors, this);
        //sets the recycler view to the new adapter
        recyclerView.setAdapter(adapter);
    }

    //method to see if list contains a sensor with a specific name
    private Sensor hasPosition(ArrayList<Sensor> s, String position){
        //if the array list is empty, the string is non existant
        if(s.isEmpty()){return null;}
        //looks for the string
        else{
            //loops through all the sensors
            for(Sensor x : s){
                //returns the correct sensor object when string is found
                if(x.getPosition().equals(position)){return x;} }
        }
        //otherwise returns null
        return null;
    }

   // @SuppressWarnings("NewAPI")
    public static void sendNotification(String position){
//
//        NotificationChannel channel = new NotificationChannel("Garden", "My Garden", NotificationManager.IMPORTANCE_HIGH);
//        channel.enableVibration(true);
        Log.w("MainActivity", "notification sent");
        Intent intent = new Intent(MyApplication.getAppContext(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getAppContext())
                .setSmallIcon(R.drawable.ic_launcher_noteicon)
                .setContentTitle(position + " Status Update!")
                .setContentText("The " + position + " has been finished being watered")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

//        NotificationManager notificationManager = MyApplication.getAppContext().getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);

        NotificationManagerCompat noteManager = NotificationManagerCompat.from(MyApplication.getAppContext());

        // notificationId is a unique int for each notification that you must define
        noteManager.notify(4, builder.build());
    }

}
