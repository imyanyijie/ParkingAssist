package com.cs528.parkingassist.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cs528.parkingassist.BroadcastRecv.GeoFenceRecv;
import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Service.GeoFencing;
import com.cs528.parkingassist.Service.ImageReco;
import com.cs528.parkingassist.Util.Constants;
import com.cs528.parkingassist.Util.PictureUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CarInfoActivity extends AppCompatActivity{
    private ImageView imageView;
    private TextView view_color;
    private TextView view_make;
    private TextView view_model;
    private TextView view_l;
    private TextView view_location;
    private TextView view_description;
    private double lat =0.0;
    private double lon =0.0;
    private Bitmap carImage;
    private GeoFencing geoFencing;

    private Parking parking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_info);
        Log.i("car", "CarInfoActivity");
        geoFencing = GeoFencing.getInstance(CarInfoActivity.this);
        view_color = findViewById(R.id.colorTextView);
        view_make = findViewById(R.id.makeTextView);
        view_model = findViewById(R.id.modelTextView);
        view_l = findViewById(R.id.licenseTextView);
        view_location = findViewById(R.id.locationTextView);
        view_description = findViewById(R.id.descriptionTextView);
        imageView = (ImageView) findViewById(R.id.carPhoto);
        Button button = (Button)findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        carImage = intent.getParcelableExtra("image");
        boolean recognizeFlag = intent.getBooleanExtra("recognized",false);
        lon = intent.getDoubleExtra("longitude",0.0);
        lat = intent.getDoubleExtra("latitude",0.0);
        imageView.setImageBitmap(carImage);


        if(recognizeFlag){
            parking = ImageReco.getInstance().getCarInfo();
            view_l.setText(parking.getLicence());
            view_model.setText(parking.getModel());
            view_make.setText(parking.getMaker());
            view_color.setText(parking.getColor());
        }
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String s = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0);
            s =  address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        view_location.setText(s);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maker = view_make.getText().toString();
                String color = view_color.getText().toString();
                String model = view_model.getText().toString();
                String description = view_description.getText().toString();
                String licence = view_l.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                Parking p = new Parking(maker, model, color, licence, lat, lon, description,currentTime.toString());
                LatLng latLng = new LatLng(lat,lon);
                geoFencing.addGeofence(licence, latLng);
                ParkPersistance.get_instance(CarInfoActivity.this).addParking(p);
                File photoFile = ParkPersistance.get_instance(CarInfoActivity.this).addPhotoFile();
                try {
                    FileOutputStream fOut = new FileOutputStream(photoFile);
                    carImage.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                }catch (IOException ex){
                    Log.e(Constants.APP_NAME,"FATEL ERROR WHEN STORING PIC"+ex);
                }

                geoFencing.startGeo(getGeofencePendingIntent());
                startActivity(new Intent(CarInfoActivity.this, FindCarActivity.class));
            }
        });

    }
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceRecv.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }



}
