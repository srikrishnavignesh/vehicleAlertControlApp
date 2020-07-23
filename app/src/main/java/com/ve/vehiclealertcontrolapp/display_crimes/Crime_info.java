package com.ve.vehiclealertcontrolapp.display_crimes;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ve.vehiclealertcontrolapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

//this class creates a list view to display all crimes taht happened near your vicinity
public class Crime_info extends AppCompatActivity implements ChildEventListener {
    DatabaseReference policedr;
    DatabaseReference vdr;
    RecyclerView recycler;
    MyCrimeAdapter adapter;
    ProgressBar pb;
    StorageReference sr;
    SwipeRefreshLayout sl;
    boolean loading=false;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_info);
        wireUpListeners();
        createAdapters();
    }

    private void createAdapters() {
           recycler=(RecyclerView)findViewById(R.id.recycler);
           recycler.setItemAnimator(new DefaultItemAnimator());
           recycler.setHasFixedSize(true);
           recycler.setLayoutManager(new LinearLayoutManager(this));
           adapter=new MyCrimeAdapter(this,new ArrayList<Crime>());
           recycler.setAdapter(adapter);
           pb=(ProgressBar)findViewById(R.id.progress);
           sl=(SwipeRefreshLayout)findViewById(R.id.swipe);

           //refreshing the list
           sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
               @Override
               public void onRefresh() {
                   policedr.removeEventListener(Crime_info.this);
                   adapter.list.clear();
                   adapter.notifyDataSetChanged();
                   policedr.addChildEventListener(Crime_info.this);
                   sl.setRefreshing(false);
               }
           });
       }

    private void wireUpListeners() {
        String uid=FirebaseAuth.getInstance().getUid();
        policedr= FirebaseDatabase.getInstance().getReference("/Users/TrafficPersonnel/");
        policedr=policedr.child(uid+"/Victims");
        vdr=FirebaseDatabase.getInstance().getReference("/Users/Victims");
        policedr.addChildEventListener(this);
        sr=FirebaseStorage.getInstance().getReference();
    }

    //if any update happens in database update the list view
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               pb.setVisibility(View.VISIBLE);
               String victimid=(String)dataSnapshot.getValue();
               vdr.child("/"+victimid).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       Crime c=dataSnapshot.getValue(Crime.class);
                       adapter.list.add(c);
                       adapter.notifyDataSetChanged();
                       pb.setVisibility(View.INVISIBLE);


                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
                });


    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        policedr.removeEventListener(this);
    }
}
