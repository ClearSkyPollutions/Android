package com.example.android.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.android.activities.MainActivity;
import com.example.android.activities.R;

public class NotificationHelper {

    private static final int NOTIFICATION_AQI_ID = 3515;
    private static final int PENDING_INTENT_ID = 4568;
    private static final String CLEARSKY_NOTIFICATION_CHANNEL_ID = "clearSky_notification_id";


    public static void createNotificationAQI(Context context, String label, int value){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CLEARSKY_NOTIFICATION_CHANNEL_ID,
                        "ClearSky",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent launchMainActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    PENDING_INTENT_ID, launchMainActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new Notification.Builder(context, CLEARSKY_NOTIFICATION_CHANNEL_ID);
            } else {
                builder = new Notification.Builder(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                }
            }
            builder.setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.notification_title))
                    .setContentIntent(pendingIntent)
                    .setContentText(context
                            .getResources()
                            .getString(R.string.notification_desc)
                            + label + " (" + value + ")");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                notificationManager.notify(NOTIFICATION_AQI_ID, builder.build());
            }
        }

    }
}
