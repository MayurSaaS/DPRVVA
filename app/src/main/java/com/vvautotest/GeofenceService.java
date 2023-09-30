package com.vvautotest;

import static com.vvautotest.activities.HomeActivity.GEOFENCE_ID;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.vvautotest.utils.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GeofenceService extends IntentService {

    private static final String TAG = "GeoIntentService";
    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (!geofencingEvent.hasError()) {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(GEOFENCE_ID)) {
                L.printError("You are inside Rlogical (Geofence Location)");
            } else {
                L.printError("You are outside Rlogical (Geofence Location)");
            }

// Fetch Entering / Exiting Detail
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(transaction, geofences);
            sendNotification(geofenceTransitionDetails);
        }
    }

    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
// get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting ";

        return status + TextUtils.join(", ", triggeringGeofencesList);
    }

    private void sendNotification(String msg) {

// Intent to start the main Activity
        Intent notificationIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

// Creating and sending Notification
        long when = Calendar.getInstance().getTimeInMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

// Configure the notification channel.
            NotificationChannel notifChannel = new NotificationChannel(msg, msg, NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.GREEN);
            notifChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notifChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notifChannel);
        }

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, "CH_ID")
                .setTicker(msg).setContentTitle(msg).setOngoing(false).setAutoCancel(true).setWhen(when)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100}).setSmallIcon(R.mipmap.ic_launcher_round);

        notificationManager.notify((int) when, noBuilder.build());
    }

}
