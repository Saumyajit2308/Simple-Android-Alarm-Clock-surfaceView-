package com.example.h.analogclock.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent arg1) {
        final Toast toast= Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 5000);
        Intent i=new Intent(context,RingtonePlayer.class);
        context.startService(i);
    }
}
