package com.ve.vehiclealertcontrolapp.location_updates;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoLocation extends BroadcastReceiver {
    FirebaseDatabase mfd;
    FirebaseAuth auth;
    @Override
    public void onReceive(Context context, Intent intent) {
        mfd=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        DatabaseReference dr=mfd.getReference("Users/TrafficPersonnel/"+auth.getUid()+"/location");
        LocationResult lr=LocationResult.extractResult(intent);
        if(lr!=null) {
            for (Location l : lr.getLocations()) {
                dr.setValue(new Loc(l.getLatitude(),l.getLongitude())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        }
    }
}
