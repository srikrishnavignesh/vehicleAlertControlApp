package com.ve.vehiclealertcontrolapp.display_crimes;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ve.vehiclealertcontrolapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//adapter to list all crimes
public class MyCrimeAdapter extends RecyclerView.Adapter<MyCrimeHolder> {
    ArrayList<Crime> list;
    Context context;
    int image_width=150;
    int image_height=200;
    public MyCrimeAdapter(Context context,ArrayList<Crime> list)
    {
        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public MyCrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.crime_info_list,parent,false);
        return new MyCrimeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCrimeHolder holder, int position) {

        //place image if available in image view
        final int pos=position;
        if(list.get(position).imageuri!=null) {
            Uri uri = Uri.parse(list.get(position).imageuri);
            Picasso.get().load(uri).fit().centerCrop().into(holder.imageuri);
        }
        else
            Picasso.get().load(R.drawable.no_image).fit().centerCrop().into(holder.imageuri);

        //vehicle details
        holder.details.setText(list.get(position).details);
        holder.reg_no.setText(list.get(position).registration_number);

        //handling click events
        holder.setOnItemClickListener(new CrimeClickListener() {

            @Override
            public void onCrimeItemClick() {
                FragmentManager fm=((AppCompatActivity)context).getSupportFragmentManager();
                Fragment f=fm.findFragmentByTag("crime_dialog");
                CrimeListDialog dialog=new CrimeListDialog();
                dialog.setCrime(list.get(pos));
                dialog.show(fm,"crime_dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
