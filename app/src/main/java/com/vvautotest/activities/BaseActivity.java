package com.vvautotest.activities;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.vvautotest.R;
import com.vvautotest.model.Site;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private LocationManager locationManager;
    private String provider;
    public int minTimeUpdate = 2000;
    public int minDistance = 0;
    LocationListener locationListener;

    private final static int ALL_PERMISSIONS_RESULT = 101;

    BaseClassListener baseClassListener;
    public LatLng currentLatLong;
    private NetworkStateReceiver stateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stateReceiver = new NetworkStateReceiver();
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(ACCESS_BACKGROUND_LOCATION);
        }*/

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                startLocationTrack();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                permissionsRejected = new ArrayList();
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    startLocationTrack();
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @SuppressLint("MissingPermission")
    public void startLocationTrack() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

     //
     //   baseClassListener.onLocationChange(new LatLng(location.getLatitude(), location.getLongitude()));

        provider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(provider, minTimeUpdate, minDistance, locationListener);
       /* this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
    //    provider = locationManager.getBestProvider(criteria, false);
        provider = LocationManager.GPS_PROVIDER; // We want to use the GPS
// Initialize the location fields
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);*/


/*
        locationTrack = new LocationTrack(HomeActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
        //    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }
*/
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        return true;
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider,  minTimeUpdate, minDistance, locationListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(stateReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(stateReceiver != null) {
            unregisterReceiver(stateReceiver);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
        //    latlongTV.setText("Lat:" + lng + ", Long:" + lat);
            L.printError("Lat:" + lng + ", Long:" + lat);
         //   LatLng latLng = new LatLng(lat, lng);
            AppUtils.setLatitude(BaseActivity.this, lat + "");
            AppUtils.setLongitude(BaseActivity.this, lng + "");
                LatLng latLng = new LatLng(25.994317, 82.848424);
            currentLatLong = latLng;
            if(baseClassListener != null) {
                baseClassListener.onLocationChange(latLng);
            }
            //    GeofencingControls(lat, lng);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    public void setBaseListener(BaseClassListener baseClassListener){
        this.baseClassListener = baseClassListener;

    }

    public void getLastUpdatedLocation(){
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        baseClassListener.onLocationChange(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public interface BaseClassListener{
        void onLocationChange(LatLng latLng);
    }

    public LatLng getCurrentLatLong()
    {
        return currentLatLong;
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if(currentLatLong != null)
        {
            savedInstanceState.putDouble("lat",currentLatLong.latitude);
            savedInstanceState.putDouble("longi",currentLatLong.longitude);
        }
        super.onSaveInstanceState(savedInstanceState);
        //Put your spinner values to restore later...
    }

    public class NetworkStateReceiver extends BroadcastReceiver {
        private boolean online = true;  // we expect the app being online when starting
        public  final String TAG = NetworkStateReceiver.class.getSimpleName();

        public void onReceive(Context context, Intent intent) {
        //    Log.d(TAG,"Network connectivity change");
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission")
            NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();
            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Wifi Connected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Wifi Not Connected!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
