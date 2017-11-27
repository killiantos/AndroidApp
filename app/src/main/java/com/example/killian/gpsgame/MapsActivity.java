package com.example.killian.gpsgame;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ArrayList<Location> MarkerPoints;
    Location origin = new Location("Start point");
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    int range = 0;
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        origin.setLatitude(53.3053);
        origin.setLongitude(-6.2207); //UCD
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();

        // getting our range
        String text = intent.getStringExtra("range");
        if(text.equals("1")){
            range = 1;
        }
        if(text.equals("2")){
            range = 2;
        }
        if(text.equals("5")){
            range = 5;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // loop to create the 3 random markers
        for(int i = 0; i< 3; i++) {
            double randomMarkerLat = calcLat (origin, range);
            double randomMarkerLng = calcLng(origin, range);
            Location Goal = new Location("Goal point");
            Goal.setLongitude(randomMarkerLng);
            Goal.setLatitude(randomMarkerLat);
            LatLng latLng1 = new LatLng(randomMarkerLat, randomMarkerLng);
            MarkerPoints.add(i,Goal);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng1);
            markerOptions.title("Goal Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
        }

        //loop to check if the user has reached a goal
        for(int i = 0; i< MarkerPoints.size(); i++){
            Location test = MarkerPoints.get(i);
            // removes the markers if the current position marker comes within 10 metres of the goal.
            if((test.getLatitude() > (mLastLocation.getLatitude() -0.00008983) && test.getLatitude() < mLastLocation.getLatitude() + 0.00008983) && (test.getLongitude() > (mLastLocation.getLongitude() -0.00008983) && test.getLongitude() < mLastLocation.getLongitude() + 0.00008983)){
                MarkerPoints.remove(i);
                Intent intent = new Intent("Service counter");
                intent.putExtra("counter", counter);
                counter++;
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                Toast.makeText(this, "GOAL REACHED!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Toast.makeText(this, markerOptions.getPosition().toString(), Toast.LENGTH_SHORT).show();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    private double calcLng (Location location, int range) { // calculates the longitude for our random markers
        //Longitude: 1 deg = 111.320*cos(latitude)km 1km = 0.00898311175 2km = 0.01796622349 5km = 0.04491555874
        mLastLocation = location;
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        double newLng = 0;
        double newLat = 0;
        Random r = new Random();

        if(range == 1){
            newLat = (lat- 0.0090) + ((lat + 0.0090) - (lat- 0.0090)) * r.nextDouble();
            newLng = (lng - (0.00898 * Math.cos(newLat))) + ((lng + (0.00898 * Math.cos(newLat))) - (lng - (0.00898 * Math.cos(newLat)))) * r.nextDouble();
        }
        if(range == 2){
            newLat = (lat- 0.01808) + ((lat + 0.01808) - (lat- 0.01808)) * r.nextDouble();
            newLng = (lng - (0.01796 * Math.cos(newLat))) + ((lng + (0.01796 * Math.cos(newLat))) - (lng - (0.01796 * Math.cos(newLat)))) * r.nextDouble();
        }
        if(range == 5){
            newLat = (lat- 0.04521) + ((lat + 0.04521) - (lat- 0.04521)) * r.nextDouble();
            newLng = (lng - (0.04491 * Math.cos(newLat))) + ((lng + (0.04491 * Math.cos(newLat))) - (lng - (0.04491 * Math.cos(newLat)))) * r.nextDouble();
        }

        return newLng;
    }
    private double calcLat(Location location, int range) { // calculates the latitude for our random markers
        //Latitude: 1 deg = 110.574 km  1km = 0.00904371733 2km = 0.0180874347  5km = 0.0452185866
        mLastLocation = location;
        double lat = location.getLatitude();
        double newLat = 0;
        Random r = new Random();

        if(range == 1){
          //newLat = (lat- 0.000090) + ((lat + 0.000090) - (lat- 0.000090)) * r.nextDouble();
           newLat = (lat- 0.0090) + ((lat + 0.0090) - (lat- 0.0090)) * r.nextDouble();
        }
        if(range == 2){
            newLat = (lat- 0.01808) + ((lat + 0.01808) - (lat- 0.01808)) * r.nextDouble();
        }
        if(range == 5){
            newLat = (lat- 0.0452) + ((lat + 0.0452) - (lat- 0.0452)) * r.nextDouble();
        }
        return newLat;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                return;
    }
}