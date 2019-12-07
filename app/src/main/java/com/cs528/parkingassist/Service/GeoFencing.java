package com.cs528.parkingassist.Service;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cs528.parkingassist.Util.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class GeoFencing {
    private static GeoFencing instance = null;
    private Context context;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<Geofence>();

    public static GeoFencing getInstance(Context context) {
        if (instance == null)
            instance = new GeoFencing(context);
        return instance;
    }

    public GeoFencing(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addGeofence(String geoID, LatLng latLng ) {
        geofenceList.add(createGeofence(geoID, latLng, Constants.GEOFENCE_RADIUS));
    }

    public void startGeo(PendingIntent pendingIntent){
        Log.e("Geo", "startGeofence request");
        geofencingClient.addGeofences(getGeofencingRequest(), pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Toast.makeText(context,
                                "Geo Fence Enabled",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(Constants.APP_NAME,"GeoFence not  successful"+e);
                        // Failed to add geofences
                        Log.w("Error", "Geo fence Not enabled");
                        Toast.makeText(context,
                                "Geo Fence Not enabled",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    // Create a Geofence
    private Geofence createGeofence(String geoID, LatLng latLng, float radius ) {
        Log.e("Geo", "createGeofence");
        return new Geofence.Builder()
                .setRequestId(geoID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(-1)
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_DWELL )
                .setLoiteringDelay (15000)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        Log.e("Geo", "createGeofence request");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }
}
