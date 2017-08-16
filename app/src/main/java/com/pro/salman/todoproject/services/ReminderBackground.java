package com.pro.salman.todoproject.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.ui.MainActivity;


public class ReminderBackground extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int id = -1;
        String title = "";
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            id = bundle.getInt("id");
            title = bundle.getString("title");
        }

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,id,i,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Todo")
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_stat_list)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager)(getSystemService(NOTIFICATION_SERVICE));
        manager.notify(id,builder.build());

        return START_NOT_STICKY;
    }

}
