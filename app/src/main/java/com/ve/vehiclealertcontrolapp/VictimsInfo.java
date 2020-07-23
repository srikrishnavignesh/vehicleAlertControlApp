package com.ve.vehiclealertcontrolapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ve.vehiclealertcontrolapp.display_crimes.Crime_info;
import com.ve.vehiclealertcontrolapp.location_updates.GeoLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VictimsInfo extends AppCompatActivity implements View.OnClickListener {
    ConnectivityManager cm;
    FusedLocationProviderClient fp;
    LocationRequest lr;
    PendingIntent pi;
    boolean tracking_service;
    Button sign_out;
    Button start_tracking;
    Button get_activities;
    public static final int REQUEST_CHECK_SETTINGS=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victims_info);
        if(savedInstanceState!=null)
            tracking_service=savedInstanceState.getBoolean("tracking_service");
        //wire up all widgets
        wireUpProvidersAndManagers();

        //post a token that is unique to a account and a device to get push notifications from firebase
        postFcm();

        //to retrieve current location
        createLocationRequest();

        //permission for location
        getPermission();

    }
    /* these managers are requiredd to get the location data of the current user*/
    private void wireUpProvidersAndManagers() {
        cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        fp=LocationServices.getFusedLocationProviderClient(this);
        get_activities=this.findViewById(R.id.get_activities);
        get_activities.setOnClickListener(this);
        sign_out=this.findViewById(R.id.sign_out);
        sign_out.setOnClickListener(this);
        start_tracking=this.findViewById(R.id.start_tracking);
        start_tracking.setOnClickListener(this);
        if(tracking_service)
            start_tracking.setText("stop_tracking");
        else
            start_tracking.setText("start_tracking");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("tracking_service",tracking_service);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    /* the location is updates for every 10 to 15 minutes and if the user displaces a distance of 100 meters*/
    private void createLocationRequest() {
        lr=LocationRequest.create();
        lr.setInterval(900000);
        lr.setFastestInterval(300000);
        lr.setSmallestDisplacement(100);
        lr.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
    /*get all permissions required by the application*/
    //we need coarse location permission
    private void getPermission() {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

    }


    /*post fcm to server*/
    private void postFcm() {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                if(instanceIdResult!=null){
                    DatabaseReference dr= FirebaseDatabase.getInstance().getReference("Users/TrafficPersonnel/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/fcm");
                    dr.setValue(instanceIdResult.getToken());
                }
            }

        });
    }
    //location services permission
    private void enableLocation()
    {
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(lr);
        SettingsClient client=LocationServices.getSettingsClient(VictimsInfo.this);
        Task<LocationSettingsResponse> task=client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                checkConnection();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if( e instanceof ResolvableApiException)
                {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(VictimsInfo.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });

    }

    //check if network is connected and available
    private void checkConnection() {
        boolean network=cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected();
        if(!network)
            Toast.makeText(this,"Please Connect to a Network",Toast.LENGTH_LONG).show();
        else
            startReceivingUpdates();

    }
    //use a broadcast reciever that recieves the location updates as mentioned in location request
    private void startReceivingUpdates() {
        Intent intnt=new Intent(getApplicationContext(), GeoLocation.class);
        pi=PendingIntent.getBroadcast(getApplicationContext(),1,intnt,PendingIntent.FLAG_UPDATE_CURRENT);
        fp.requestLocationUpdates(lr,pi);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PERMISSION_DENIED)
        {
            View view=findViewById(R.id.layout);
            Snackbar snack=Snackbar.make(view,"you have to provide permission for access",Snackbar.LENGTH_INDEFINITE).setAction("PERMIT", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intnt=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri=Uri.fromParts("package",getPackageName(),null);
                        intnt.setData(uri);
                        startActivity(intnt);

                }
            });
            snack.setActionTextColor(Color.GREEN);
            snack.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View view=findViewById(R.id.layout);

        //check if user has grant permission
        switch(requestCode)
        {
            case 1:if(resultCode==RESULT_OK)
                        checkConnection();
                    else {
                            Snackbar snack = Snackbar.make(view, "You Have to Give Permission", Snackbar.LENGTH_INDEFINITE).setAction("PERMIT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableLocation();

                        }});
                        snack.setActionTextColor(Color.GREEN);
                        snack.show();
                        }
                        break;
        }

    }

    //click events
    //start tracking gathers location updates and send data to db
    //sign out clears users session
    //get activities lists all crimes that happened in your vicinity
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.start_tracking: if(!tracking_service) {
                                            enableLocation();
                                            tracking_service = true;
                                            createToastMessage("started tracking nearby theft's");
                                            ((Button)v).setText("Stop_Tracking");
                                        }
                                        else
                                        {
                                                tracking_service=false;
                                                ((Button)v).setText("Start_Tracking");
                                                createToastMessage("stoped_tracking");
                                                if(fp!=null && pi!=null)
                                                    fp.removeLocationUpdates(pi);
                                        }
                                        break;
            case R.id.sign_out: DatabaseReference dr= FirebaseDatabase.getInstance().getReference("Users/TrafficPersonnel/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/fcm");
                                dr.setValue("not available");
                                FirebaseAuth.getInstance().signOut();
                                Intent intnt=new Intent(this,MainActivity.class);
                                intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intnt);
                                if(pi!=null)
                                    fp.removeLocationUpdates(pi);
                                break;

            case R.id.get_activities:Intent intent=new Intent(this, Crime_info.class);
                                    startActivity(intent);
                                    break;
        }
    }

    //a function to toast message
    void createToastMessage(String message)
    {
        Toast t=Toast.makeText(this,"  "+message+"  ",Toast.LENGTH_LONG);
        View v=t.getView();
        v.setBackgroundColor(ContextCompat.getColor(this,R.color.bg_color));
        TextView txt=(TextView)v.findViewById(android.R.id.message);
        txt.setTextColor(ContextCompat.getColor(this,R.color.text_color));
        t.show();

    }
}

