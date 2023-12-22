package com.example.httprequests;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationService extends IntentService {

    public static final String ACTION_REQUEST_NOTIFICATION_PERMISSION =
            "REQUEST_NOTIFICATION_PERMISSION_SERVICE";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public NotificationService() {
        super("Notification Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            double upcomingPrecipitationLevel = intent.getDoubleExtra("precipitation", 0.0);


            sendNotification(upcomingPrecipitationLevel);
        }
    }

    private void sendNotification(double precipitationLevel) {
        // Your notification logic here

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather_channel")
                .setSmallIcon(R.drawable.rain)
                .setContentTitle("Precipitation Alert")
                .setContentText("Expected precipitation of "+precipitationLevel+"mm in your area");


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Intent permissionIntent = new Intent();
            permissionIntent.setAction(ACTION_REQUEST_NOTIFICATION_PERMISSION);
            LocalBroadcastManager.getInstance(NotificationService.this).sendBroadcast(permissionIntent);
            Log.d("NotificationService","Requesting permissions");
        }
        notificationManager.notify(1, builder.build());
    }
}
