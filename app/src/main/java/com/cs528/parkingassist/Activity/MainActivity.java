package com.cs528.parkingassist.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;


import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cs528.parkingassist.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.cs528.parkingassist.Util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean canTakePhoto;

    private static final int REQUEST_PHOTO= 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("start", "MainActivity");
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = this.getPackageManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        mapFragment.getMapAsync(this);

        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.addParkingButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("button", "add new parking button clicked!");
                canTakePhoto = captureImage.resolveActivity(packageManager) != null;
                if (canTakePhoto) {
//                    Uri uri = FileProvider.getUriForFile(MainActivity.this,
//                            "com.cs528.parkingassist.provider", mPhotoFile);
//                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);


//                    List<ResolveInfo> cameraActivities =
//                            packageManager.queryIntentActivities(captureImage,
//                                    PackageManager.MATCH_DEFAULT_ONLY);
//
//                    for (ResolveInfo activity : cameraActivities) {
//                        MainActivity.this.grantUriPermission(activity.activityInfo.packageName,
//                                uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    }
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);
        showDefaultLocation();

    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }


    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();

        Location lastLocation = Utils.getBestLastKnownLocation(this);
        LatLng last = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(30);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };


    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

        }
    }

}
