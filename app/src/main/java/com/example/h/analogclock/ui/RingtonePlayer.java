package com.example.h.analogclock.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;


public class RingtonePlayer extends Service {
    private Ringtone r;
    private Vibrator vibrator;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(RingtonePlayer.this, notification);
        vibrator = (Vibrator)getApplicationContext()
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        r.play();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        r.stop();
        vibrator.cancel();
        super.onDestroy();
    }
}
