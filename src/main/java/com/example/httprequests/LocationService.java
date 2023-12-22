package com.example.httprequests;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    public static final String ACTION_REQUEST_LOCATION_PERMISSION =
            "REQUEST_LOCATION_PERMISSION_SERVICE";

    public static final String LOCATION_UPDATE =
            "LOCATION_UPDATE";
    private final IBinder mBinder = new LocalBinder();
    private GoogleApiClient mGoogleApiClient;
    private com.google.android.gms.location.LocationRequest mLocationRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        createLocationRequest();

        Log.d("LocationService","Service Started!");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Intent permissionIntent = new Intent();
            permissionIntent.setAction(ACTION_REQUEST_LOCATION_PERMISSION);
            LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(permissionIntent);
            Log.d("LocationService","Requesting permissions");
        }
        Log.d("LocationService","started google api");
        mGoogleApiClient.connect();
        return START_STICKY;
    }


    private void createLocationRequest() {
        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(10000); // 10 seconds
        mLocationRequest.setFastestInterval(5000); // 5 seconds
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Intent locationIntent = new Intent();
        locationIntent.setAction(LOCATION_UPDATE);
        locationIntent.putExtra(
                "longitude",longitude
        );
        locationIntent.putExtra(
                "latitude",latitude
        );
        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(locationIntent);
        Log.d("LocationService", "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService","start location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this::onLocationChanged);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("LocationService","Connection was Suspended");
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this::onLocationChanged);
    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
}
