package com.ve.vehiclealertcontrolapp.display_crimes;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ve.vehiclealertcontrolapp.R;
import com.squareup.picasso.Picasso;


//on pressing an item in list view this dialog fragment appears
//that displays complete details of your vicinity
public class CrimeListDialog extends DialogFragment {
    private TextView nametxt;
    private TextView regno;
    private TextView contactno;
    private TextView details;
    private ImageView carimage;
    int image_width=250;
    int image_height=250;
    Crime c;
    public CrimeListDialog() {
        super();
    }

    public void setCrime(Crime c)
    {
        this.c=c;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.crime_dialog,container,false);

        nametxt=(TextView)view.findViewById(R.id.name);
        nametxt.setText(c.name);
        regno=(TextView)view.findViewById(R.id.reg_no);
        regno.setText(c.registration_number);
        contactno=(TextView)view.findViewById(R.id.contact_no);
        contactno.setText(c.contact_no);
        details=(TextView)view.findViewById(R.id.details);
        details.setText(c.details);
        carimage=(ImageView)view.findViewById(R.id.car_img);

        //check for image availability
        if(c.imageuri!=null)
            Picasso.get().load(Uri.parse(c.imageuri)).fit().into(carimage);
        else
            Picasso.get().load(R.drawable.no_image).fit().into(carimage);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dismiss();
    }
}
