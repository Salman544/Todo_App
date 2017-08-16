package com.pro.salman.todoproject.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Intent i = new Intent(context,ReminderBackground.class);
        if(bundle!=null)
        {
            i.putExtra("id",bundle.getInt("id"));
            i.putExtra("title",bundle.getString("title"));
        }

        context.startService(i);

    }
}
