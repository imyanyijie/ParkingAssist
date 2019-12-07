package com.cs528.parkingassist.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Service.ImageReco;
import com.cs528.parkingassist.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.cs528.parkingassist.Util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean canTakePhoto;
    private Bitmap imageBitmap;
    private LatLng currenLocation;

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
        currenLocation = last;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
        mMap.setMinZoomPreference(15);
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(20);
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
                    currenLocation = new LatLng(location.getLatitude(),location.getLatitude());
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
            imageBitmap = (Bitmap) extras.get("data");
            AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(imageBitmap);
            asyncTaskRunner.execute();


        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        private Bitmap imageBitmap;
        ProgressDialog progressDialog;

        public AsyncTaskRunner(Bitmap imageBitmap) {
            this.imageBitmap = imageBitmap;
        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ImageReco imageReco = ImageReco.getInstance();
                boolean result = imageReco.detectCar(byteArray);
                resp = ""+result;

            }  catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            if(result.equalsIgnoreCase("True")){
                Log.e(Constants.APP_NAME,"recognition successful");
                //go to the correct activity that will display the data.
                Intent intent = new Intent(MainActivity.this, CarInfoActivity.class);
                intent.putExtra("image ",imageBitmap);
                intent.putExtra("latitude",currenLocation.latitude);
                intent.putExtra("longitude",currenLocation.longitude);
                intent.putExtra("recognized",true);
                startActivity(intent);

            }
            else {
                Log.e(Constants.APP_NAME,"recognition not  successful");
                //go to the correct activity that will display the data.
                Toast.makeText(MainActivity.this, "Image could not be recognized! Please try again!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CarInfoActivity.class);
                intent.putExtra("image",imageBitmap);
                intent.putExtra("latitude",currenLocation.latitude);
                intent.putExtra("longitude",currenLocation.longitude);
                intent.putExtra("recognized",false);

                startActivity(intent);
            }
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "ProgressDialog",
                    "Waiting to Recognize the Image");
        }
    }

}
