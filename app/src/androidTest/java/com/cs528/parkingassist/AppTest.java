package com.cs528.parkingassist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import com.cs528.parkingassist.Activity.CarInfoActivity;
import com.cs528.parkingassist.Activity.FindCarActivity;
import com.cs528.parkingassist.Activity.MainActivity;
import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.Service.ImageReco;

import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.ByteArrayOutputStream;



@RunWith(AndroidJUnit4.class)
@LargeTest

public class AppTest {

    @Test
    public void carInfAddTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Parking parking = new Parking("m","m","m","l",0.0,0.0,"m","t");
        ParkPersistance.get_instance(appContext).addParking(parking);
        assert (!ParkPersistance.get_instance(appContext).getParkings().isEmpty());
    }

    @Test
    public void carInfoDeleteTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ParkPersistance.get_instance(appContext).removeParking();
        assert (ParkPersistance.get_instance(appContext).getParkings().isEmpty());
    }

    @Test
    public void carInfoTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ParkPersistance.get_instance(appContext).removeParking();
        assert (ParkPersistance.get_instance(appContext).getParkings().isEmpty());
    }

    @Test
    public void carImageTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Bitmap imageBitmap = BitmapFactory.decodeResource(appContext.getResources(),
                R.drawable.car12);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        boolean result = ImageReco.getInstance().detectCar(byteArray);
        Parking parking = ImageReco.getInstance().getCarInfo();
        assert (parking!= null);
    }

}
