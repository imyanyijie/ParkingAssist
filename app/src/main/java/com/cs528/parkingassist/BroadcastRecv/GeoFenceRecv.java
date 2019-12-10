package com.cs528.parkingassist.BroadcastRecv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cs528.parkingassist.Activity.MainActivity;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Util.Constants;

import androidx.core.app.NotificationCompat;

public class GeoFenceRecv extends BroadcastReceiver {
    private final static String default_notification_channel_id = "default" ;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id);
        builder.setContentTitle("Did you just parked your car?");
        builder.setContentText("Please don't forget to record where you parked!");
        builder.setSmallIcon(R.drawable.logo_small);
        builder.setAutoCancel(true);
        builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
        assert notificationManager != null;
        notificationManager.notify(1, builder.build());
    }
}
