package com.ve.vehiclealertcontrolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ve.vehiclealertcontrolapp.loggin_user.SignUpActivity;
//the class handles sign up and sign in \
//when the user presses sign up we direct them to a new activity SignUpActivity
//in case of sign in we first check that user has verified the mail sent to his email address

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener {
    Button msignup;
    Button mloginbtn;
    FirebaseAuth mauth;
    EditText memail;
    EditText mpassword;
    TextView mresulttext;
    ProgressBar mpb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        wireUpWidgets();
        wireUpListeners();
    }

    private void wireUpListeners() {
        msignup.setOnClickListener(new View.OnClickListener() {  /* if not a new user then sign_up*/
            @Override
            public void onClick(View v) {           /*procced th e user to sign_up acitivity*/
                /* this is taken care by the signUpActivity*/
                Intent intnt=new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intnt);
            }
        });
        mloginbtn.setOnClickListener(this);
    }

    private void wireUpWidgets()
    {
        msignup=(Button) findViewById(R.id.sign_up);
        memail=(EditText)findViewById(R.id.login_email);
        mpassword=(EditText)findViewById(R.id.login_pass);
        mloginbtn=(Button)findViewById(R.id.login_btn);     /*wireup all widgets*/
        mresulttext=(TextView)findViewById(R.id.result);
        mauth=FirebaseAuth.getInstance();
        mpb=(ProgressBar)findViewById(R.id.progress);
        mpb.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            Log.d("mytest","logged_in");
        mauth.addAuthStateListener(this);
    }


    //when user presses sign in
    @Override
    public void onClick(View v) {   /* validate all input before logging_in*/
        if(memail==null || memail.length()==0 || !Patterns.EMAIL_ADDRESS.matcher(memail.getText().toString()).matches() )
        {
            mresulttext.setText("please enter a valid email");
            memail.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return;
        }
        if(mpassword==null || mpassword.length()==0 ||mpassword.length()<6)
        {
            mresulttext.setText("please enter a password");
            mpassword.requestFocus();
            mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
            return;
        }
        loginUser();
    }

    private void loginUser() {
        /* acitivty is not called explicitly we use authstate listener for loggin_in*/
        mpb.setVisibility(View.VISIBLE);
        mauth.signInWithEmailAndPassword(memail.getText().toString(),mpassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                mpb.setVisibility(View.INVISIBLE);                  /* even if email and password matches check if user has the sent email been verified*/
                if(!mauth.getCurrentUser().isEmailVerified())
                {
                    mresulttext.setText("please verify email the email sent to your email address and try again");
                    mresulttext.requestFocus();
                    mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
                    return;
                }
                clearText();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mpb.setVisibility(View.INVISIBLE);
                mresulttext.setText("Error occured please try again");
                mresulttext.requestFocus();
                mresulttext.setTextColor(getResources().getColor(R.color.alertcolor));
                return;
            }
        });
    }

    void clearText()
    {
        memail.setText("");
        mpassword.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser()!=null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            Log.d("test","in auth state changed");
            Intent intnt = new Intent(this, VictimsInfo.class);
            intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intnt);
        }
        else
        {

        }

    }
}
