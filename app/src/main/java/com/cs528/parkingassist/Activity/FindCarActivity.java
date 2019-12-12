package com.cs528.parkingassist.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cs528.parkingassist.BroadcastRecv.GeoFenceRecv;
import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Service.GeoFencing;
import com.cs528.parkingassist.Util.Constants;
import com.cs528.parkingassist.Util.PictureUtils;
import com.cs528.parkingassist.Util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FindCarActivity extends AppCompatActivity implements OnMapReadyCallback{
    private TextView view_licence;
    private TextView view_location;
    private Button foundButton;
    private ImageView carImage;

    private GoogleMap mMap;
    private LatLng currenLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private ParkPersistance parkDB;
    List<Parking> parkingList;
    Parking parkingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_car);
        Log.i("car", "find car");
        parkDB = ParkPersistance.get_instance(FindCarActivity.this);
        view_licence = findViewById(R.id.carPlateNumber);
        view_location = findViewById(R.id.parkingLocation);
        foundButton = findViewById(R.id.foundButton);
        carImage = findViewById(R.id.carPhotoView);
        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogDemo();
            }
        });

        File mPhotoFile = ParkPersistance.get_instance(FindCarActivity.this).getPhotoFile();
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            Log.e(Constants.APP_NAME,"FAIL TO GET PHOTO");
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), FindCarActivity.this);
            carImage.setImageBitmap(bitmap);
        }

        parkingList = ParkPersistance.get_instance(FindCarActivity.this).getParkings();
        parkingInfo = parkingList.get(parkingList.size()-1);
        view_licence.setText(parkingInfo.getLicence());

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String s =null;
        try {
            addresses = geocoder.getFromLocation(parkingInfo.getLat(), parkingInfo.getLon(), 1);
            String address = addresses.get(0).getAddressLine(0);
            s =  address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        view_location.setText(s);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        view_licence.setText(parkingInfo.getLicence());

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

        LatLng parkLatLon = new LatLng(parkingInfo.getLat(), parkingInfo.getLon());
        googleMap.addMarker(new MarkerOptions().position(parkLatLon)
                .title("Your car Position."));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(parkLatLon));

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

    private void confirmDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Car found already?");
        builder.setMessage("You are about to delete all records of database. Do you really want to proceed ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You've choosen to delete all records and start over", Toast.LENGTH_SHORT).show();
                parkDB.removeParking();
                GeoFencing.getInstance(FindCarActivity.this).removeAllGeofence(getGeofencePendingIntent());
                startActivity(new Intent(FindCarActivity.this, MainActivity.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Continue finding your car.", Toast.LENGTH_SHORT).show();


            }
        });

        builder.show();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceRecv.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

}
