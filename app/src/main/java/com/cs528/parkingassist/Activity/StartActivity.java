package com.cs528.parkingassist.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import com.cs528.parkingassist.BroadcastRecv.GeoFenceRecv;
import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Service.ActivityRec;
import com.cs528.parkingassist.Util.Constants;

import java.util.List;

public class StartActivity extends AppCompatActivity {

    private Handler handler;
    private ActivityRec activityrec;
    private ParkPersistance parkPersistance;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.i("start", "StartActivity");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        //register activity reco
        activityrec = ActivityRec.getInstance(StartActivity.this);
        activityrec.start(getGeofencePendingIntent());
        //initialize database if haven't
        parkPersistance = ParkPersistance.get_instance(StartActivity.this);
        List<Parking> parkingList = parkPersistance.getParkings();
        if(parkingList == null|| parkingList.isEmpty()){
            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(StartActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);
        }
        else{
            Log.e(Constants.APP_NAME,"Database is not empty, going display the list");
        }

    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceRecv.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
