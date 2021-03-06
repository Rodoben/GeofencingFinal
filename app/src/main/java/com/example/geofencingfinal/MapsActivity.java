package com.example.geofencingfinal;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.geofencingfinal.Constants.BAY_AREA_LANDMARKS;
import static com.example.geofencingfinal.Constants.GEOFENCE_RADIUS_IN_METERS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleMap mMap;
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    public static final String SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".SHARED_PREFERENCES_NAME";
    public static final String NEW_GEOFENCE_NUMBER = BuildConfig.APPLICATION_ID + ".NEW_GEOFENCE_NUMBER";
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LatLng ff = BAY_AREA_LANDMARKS.get("SFO");

        mGeofenceList = new ArrayList<Geofence>();
        buildGoogleApiClient();
        getGeofencePendingIntent();
        populateGeofenceList();

        addgeofence();
        // String value = Constants.getHmapCashType().get("A").toString()
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : BAY_AREA_LANDMARKS.entrySet()) {

            Toast.makeText(getApplicationContext(), "geofences popoullation entered", Toast.LENGTH_LONG).show();

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
            Toast.makeText(getApplicationContext(), "geofences  created", Toast.LENGTH_LONG).show();

        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getGeofencingRequest();
        addgeofence();
        populateGeofenceList();
        buildGoogleApiClient();
        // Add a marker in Sydney and move the camera
     //   LatLng sydney = new LatLng(12.933225, 77.605848);
     //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

      addMarker();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Toast.makeText(getApplicationContext(),"connected",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "suspended", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResult(@NonNull Status status) {

        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addgeofence() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),

                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            Toast.makeText(getApplicationContext(), "out", Toast.LENGTH_LONG).show();
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }

    }

    private void test_sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private void addMarker() {


        LatLng audienter = BAY_AREA_LANDMARKS.get("audienter");
        LatLng puc = BAY_AREA_LANDMARKS.get("puc");
        LatLng parking = BAY_AREA_LANDMARKS.get("parking");
        LatLng centralblock = BAY_AREA_LANDMARKS.get("centralblock");
        LatLng block1 = BAY_AREA_LANDMARKS.get("block1");

        LatLng block2 = BAY_AREA_LANDMARKS.get("block2");
        LatLng block3 = BAY_AREA_LANDMARKS.get("block3");

        LatLng block4 = BAY_AREA_LANDMARKS.get("block4");

        LatLng block2nd = BAY_AREA_LANDMARKS.get("block2nd");
LatLng pp=BAY_AREA_LANDMARKS.get("park");



        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(audienter));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(audienter));

        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(puc));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(puc));


//        mMap.moveCamera(CameraUpdateFactory.newLatLng(parking));
        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(pp));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pp));

        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(centralblock));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centralblock));

        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(block1));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(block1));

        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(block2));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(block2));
        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(block3));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(block3));

        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(block4));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(block4));
        mMap.addMarker(new MarkerOptions()
                // .title("G:" + String)
                .snippet("Click here if you want delete this geofence")
                .position(block2nd));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(block2nd));



        //creating circle around the marker for the geofencing
        mMap.addCircle(new CircleOptions()
                .center(pp)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(audienter)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(puc)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));



        mMap.addCircle(new CircleOptions()
                .center(centralblock)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(block1)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(block2)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(block3)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));

        mMap.addCircle(new CircleOptions()
                .center(block4)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
        mMap.addCircle(new CircleOptions()
                .center(block2nd)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));

    }

    public void retrieve_location(){



    }
}



