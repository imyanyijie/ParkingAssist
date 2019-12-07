package com.cs528.parkingassist.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;

import java.util.List;


public class FindCarActivity extends AppCompatActivity{
    private TextView view_licence;
    private TextView view_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_car);
        Log.i("car", "find car");
        view_licence = findViewById(R.id.carPlateNumber);
        view_location = findViewById(R.id.parkingLocation);


        List<Parking> parkingList = ParkPersistance.get_instance(FindCarActivity.this).getParkings();
        Parking parkingInfo = parkingList.get(parkingList.size()-1);
        view_licence.setText(parkingInfo.getLicence());
        view_location.setText(parkingInfo.getLat()+","+parkingInfo.getLon());




//        view_l.setText(p.getLicence());
//        String lat = String.valueOf(p.getLat());
//        String lon = String.valueOf(p.getLon());
        //TODO: get last location lat long
//        String s = "( " + lat + " " + lon + ")";
//        view_loc.setText(s);

    }



}
