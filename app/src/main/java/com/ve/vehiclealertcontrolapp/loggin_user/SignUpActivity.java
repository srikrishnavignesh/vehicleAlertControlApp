package com.ve.vehiclealertcontrolapp.loggin_user;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.ve.vehiclealertcontrolapp.R;
import com.ve.vehiclealertcontrolapp.traffic_personnel.TrafficPersonnel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/* the activity implements DateFragment.DateSet interface declared in DateFragment class to get the date set by the user using
set date method
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DateFragment.DateSet {
    EditText memail;
    EditText mpassword;
    FirebaseAuth mauth;
    Button mcreate;
    TextView mresulttext;
    EditText mfname;
    EditText msname;
    RadioGroup mrg;
    TrafficPersonnel tp;
    FirebaseDatabase mfd;
    DatabaseReference mdr;
    Button dbtn;
    String date;
    EditText mgrade;
    ProgressBar mpb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        wireUpWidgets();
        wireUpListeners();

    }

    //create listeners for widgets
    private void wireUpListeners() {
        mcreate.setOnClickListener(this);
        dbtn.setOnClickListener(this);
        mauth=FirebaseAuth.getInstance();
        mdr=mfd.getReference("/Users");
    }

    //wiring up all widgets
    private void wireUpWidgets() {
        memail=(EditText)findViewById(R.id.email);
        mpassword=(EditText)findViewById(R.id.password);
        mcreate=(Button)findViewById(R.id.create_account);
        mresulttext=(TextView)findViewById(R.id.result_text);
        mfname=(EditText)findViewById(R.id.fname);
        msname=(EditText)findViewById(R.id.sname);
        mrg=(RadioGroup)findViewById(R.id.rg);
        mfd=FirebaseDatabase.getInstance();
        dbtn=(Button)findViewById(R.id.datbtn);
        mpb=(ProgressBar)findViewById(R.id.progress);
        mpb.setVisibility(View.INVISIBLE);
        mgrade=(EditText)findViewById(R.id.grade);

    }

    //two click events
    //one for validating user input and creating a new user
    //one for getting date of birth
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_account:
                                        if (!validateInput())
                                                return;
                                        createUser();
                                        break;
            case R.id.datbtn:/* create a date fragment */
                             if(getSupportFragmentManager().findFragmentByTag("date_setter")==null) {
                                    DialogFragment df = new DateFragment();
                             df.show(getSupportFragmentManager(),"date_setter");
                }

        }
    }

    private void createUser() {
        mpb.setVisibility(View.VISIBLE);
        mauth.createUserWithEmailAndPassword(memail.getText().toString(), mpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mpb.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    mauth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setUpNewAccount();                      /*if successfull setUpNewAccount for user*/

                        }
                    });
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) { /*check for duplicate accounts*/
                    mresulttext.setText("account already exists");
                    mresulttext.requestFocus();
                    mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
                } else {
                    mresulttext.setText("error_occured_please_try_again");  /*check for network error*/
                    mresulttext.requestFocus();
                    mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
                }
            }
        });

    }

    private void setUpNewAccount() {
        clearText();
        mdr=mdr.child("/TrafficPersonnel/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        /*check if creation is successfull*/
        mdr.setValue(tp).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mresulttext.setText("please verify the email sent to your email address and login");  /* intimate users to veify email sign_out the user from current session*/

                mresulttext.requestFocus();
                mresulttext.setTextColor(getResources().getColor(R.color.ok));

                //sign out current user to login once again after verifying email address
                if(FirebaseAuth.getInstance()!=null)
                    FirebaseAuth.getInstance().signOut();
            }
        }).addOnFailureListener(new OnFailureListener() {   /* if not sucessfull*/

            //error creating a user account
            @Override
            public void onFailure(@NonNull Exception e) {
                mresulttext.setText("Error Occurred Please check connection and try again");
                mresulttext.requestFocus();
                mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));

            }
        });

    }

    //validate all user inputs before creating a new user
    private boolean validateInput() {
        String fname,lname,gen,grade;
        if(memail==null || memail.length()==0 || !Patterns.EMAIL_ADDRESS.matcher(memail.getText().toString()).matches() )
        {
            mresulttext.setText("please enter a valid email");
            memail.requestFocus();
            mresulttext.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return false;
        }
        if(mpassword==null || mpassword.length()==0 ||mpassword.length()<6)
        {
            mresulttext.setText("please enter a Password that is atleast 6 characters long");
            mpassword.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return false;
        }
        if(mfname.length()==0 || mfname.getText().toString().trim().length()==0)
        {
            mresulttext.setText("please enter a valid first name");
            mpassword.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return false;
        }
        if(msname.length()==0 || msname.getText().toString().trim().length()==0)
        {
            mresulttext.setText("please enter a valid second name");
            mpassword.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return false;
        }
        switch(mrg.getCheckedRadioButtonId())
        {
            case R.id.male:gen="male";
                            break;
            case R.id.female:gen="female";
                            break;
            default:gen="unknown";
                    break;
        }
        grade=mgrade.getText().toString();
        if(date==null|| date.length()==0 ||grade==null || grade.length()==0)
            return false;
        fname=mfname.getText().toString();
        lname=msname.getText().toString();
        /* create a referencce to the object and return true*/
        tp=new TrafficPersonnel(fname,lname,gen,date,grade);
        return true;
    }

    void clearText()
    {
        memail.setText("");
        mpassword.setText("");
        mfname.setText("");
        msname.setText("");
        mgrade.setText("");
    }

    @Override
    public void setDate(int year, int month, int day) {
        date=day+"/"+month+"/"+year;
        dbtn.setText(date);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dbtn.setText(savedInstanceState.getString("DATE"));
        mresulttext.setText(savedInstanceState.getString("RESULT"));
        mresulttext.setTextColor(savedInstanceState.getInt("COLOR"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("DATE",dbtn.getText().toString());
        outState.putString("RESULT",mresulttext.getText().toString());
        outState.putInt("COLOR",mresulttext.getCurrentTextColor());
    }
}


