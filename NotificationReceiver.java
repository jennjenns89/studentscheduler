package com.example.studentscheduler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationReceiver extends BroadcastReceiver {
    static int notificationID;
    String channel_id="test";
    String title;
    String body;
    int id;
    public final static String TAG = "Notification Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        body = intent.getStringExtra("body");
        title = intent.getStringExtra("title");
        Log.d(TAG, "body is: " + body + "\n title is: " + title);
        createNotificationChannel(context,channel_id);

        Notification n= new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(body)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                        .build();

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++,n);


    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
