package com.ve.vehiclealertcontrolapp.display_crimes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ve.vehiclealertcontrolapp.R;

public class MyCrimeHolder extends RecyclerView.ViewHolder{
    TextView reg_no;
    TextView details;
    ImageView imageuri;
    CrimeClickListener listener;
    public MyCrimeHolder(@NonNull View itemView) {
        super(itemView);
        reg_no=(TextView) itemView.findViewById(R.id.reg_no);
        details=(TextView)itemView.findViewById(R.id.car_details);
        imageuri=(ImageView)itemView.findViewById(R.id.car_image);

        //click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCrimeItemClick();
            }
        });
    }
    public void setOnItemClickListener(CrimeClickListener listener)
    {
        this.listener=listener;
    }
}
