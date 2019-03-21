package com.example.smnc.androidonlinequizapp.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.media.app.NotificationCompat;

import com.example.smnc.androidonlinequizapp.MainActivity;
import com.example.smnc.androidonlinequizapp.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notificationIntent=new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.support.v4.app.NotificationCompat.Builder builder=new android.support.v4.app.NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Online Quiz App").setContentText("Hey! Don't You Forget Solve The Questions Today").setSound(alarmSound).setAutoCancel(true).setWhen(when).setContentIntent(pendingIntent).setVibrate(new long[] {1000,1000,1000,1000,1000});
        notificationManager.notify(0,builder.build());
    }
}
