package com.vvautotest.utils;


import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

import static java.util.stream.Collectors.toList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;


public class GeofenceManager {

    public static final int CUSTOM_REQUEST_CODE_GEOFENCE = 1001;
    public static final String CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION";
    Context context;
    String TAG = "GeofenceManager";
    GeofencingClient client;
    public HashMap<String, Geofence> geofenceList = new HashMap<String, Geofence>();
    float  radius =100.0f;
    long expirationTime = 30*60*1000;

    public GeofenceManager(Context context) {
        this.context = context;
        client = LocationServices.getGeofencingClient(context);
    }

    public PendingIntent geofencingPendingIntent() {
        return PendingIntent.getBroadcast(
                context,
                CUSTOM_REQUEST_CODE_GEOFENCE,
                new Intent(CUSTOM_INTENT_GEOFENCE), Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_CANCEL_CURRENT :
                        PendingIntent.FLAG_MUTABLE);
    }

    public void addGeofence(String key,
                            Location location) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
   //     geofenceList[key] = createGeofence(key, location, radiusInMeters, expirationTimeInMillis)
        geofenceList.put("key", createGeofence(key, location, radius, expirationTime));
    }
    public void removeGeofence(String key) {
        geofenceList.remove(key);
    }

    @SuppressLint("MissingPermission")
    public void registerGeofence() {
        try{
            client.addGeofences(createGeofencingRequest(), geofencingPendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    L.printError("...................Success geofences Added.................................");
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void deregisterGeofence() {
        client.removeGeofences(geofencingPendingIntent());
        geofenceList.clear();
    }

    private GeofencingRequest createGeofencingRequest()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new GeofencingRequest.Builder().
                setInitialTrigger(GEOFENCE_TRANSITION_ENTER)
                    .addGeofences(geofenceList.values().stream().collect(toList()))
            .build();
        }else
        {
           return null;
        }
    }

    private Geofence createGeofence(
            String key,
            Location location,
            Float radiusInMeters,
            Long expirationTimeInMillis
            )  {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), radiusInMeters)
                .setExpirationDuration(expirationTimeInMillis)
                .setTransitionTypes(GEOFENCE_TRANSITION_ENTER | GEOFENCE_TRANSITION_EXIT)
                .build();
    }

}