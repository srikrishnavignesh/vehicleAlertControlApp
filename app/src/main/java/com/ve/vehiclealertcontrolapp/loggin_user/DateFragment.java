package com.ve.vehiclealertcontrolapp.loggin_user;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    interface DateSet
    {
        public void setDate(int year, int month, int day);
    }
    DateSet ds;

    public DateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ds=(DateSet) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar cl= Calendar.getInstance();
        int year=cl.get(Calendar.YEAR);
        int month=cl.get(Calendar.MONTH);
        int day=cl.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ds.setDate(year,month+1,dayOfMonth);
    }
}
