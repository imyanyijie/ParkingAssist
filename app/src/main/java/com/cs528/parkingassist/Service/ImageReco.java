package com.cs528.parkingassist.Service;

import android.content.Context;
import android.util.Log;

import com.cs528.parkingassist.Util.Constants;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ImageReco {
    private static final String api = "https://dev.sighthoundapi.com/v1/recognition?objectType=vehicle,licenseplate";
    private static ImageReco instance = null;
    private Context context;
    public static ImageReco getInstance(Context context) {
        if (instance == null)
            instance = new ImageReco(context);
        return instance;
    }

    public ImageReco(Context context) {
        this.context = context;
    }

    public void detectCar(byte[] image_Byte){

        try {
            URL apiURL = new URL(api);
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Access-Token", Constants.API_Token);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(image_Byte.length);
            OutputStream os = connection.getOutputStream();
            os.write(image_Byte);
            os.flush();
            int httpCode = connection.getResponseCode();
            if (httpCode == 200) {
                Log.e("The return json is",""+connection.getInputStream());
            } else {
                Log.e("The Error has Occurred",""+connection.getErrorStream());
            }
        }catch (Exception ex){
            Log.e("The Error has Occurred","Exception: "+ex);

        }
    }

}
