package com.ve.vehiclealertcontrolapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.ve.vehiclealertcontrolapp.display_crimes.Crime_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//this class receives push notification from server
public class MyFireBaseInstanceIdService extends FirebaseMessagingService {
    String user;
    NotificationManagerCompat nm;


    //receive any message from firebase
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        nm= NotificationManagerCompat.from(this);
        createNotification(remoteMessage.getData().get("message"));


    }
    private void createNotification(String title) {
        String body="New crime reported in your vicinity";
        Intent intnt=new Intent(this, Crime_info.class);
        intnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi=PendingIntent.getActivity(this,1,intnt,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification build= new NotificationCompat.Builder(this,"2").setColor(ContextCompat.getColor(this,R.color.main_color)).setSmallIcon(R.drawable.common_full_open_on_phone).setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.logo)).setAutoCancel(true).setContentTitle(title).setContentText(body).setContentIntent(pi).build();
        createNotificationChannel();
        nm.notify(1,build);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O)
        {
            String name="FirebaseNotification";
            String desc="Crime_Report";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel nc=new NotificationChannel("2",name,importance);
            Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            nc.setDescription(desc);
            nc.setSound(sounduri,null);
            nm.createNotificationChannel(nc);
        }
    }

    ///update new token if generatd to database
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return;
        user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        sendToServer(s);
    }

    void sendToServer(String curr)
    {
        DatabaseReference dr= FirebaseDatabase.getInstance().getReference("Users/TrafficPersonnel/"+user+"/fcm");
        dr.setValue(curr);
    }
}
