package com.cs528.parkingassist.Service;

import android.content.Context;
import android.util.JsonReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class ImageReco {
    private static final String api = "https://dev.sighthoundapi.com/v1/recognition?objectType=vehicle,licenseplate";
    private static String imageUrl = "https://www.example.com/path/to/image.jpg";

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

    public void connectServer(){
        JsonObject jsonImage = Json.createObjectBuilder().add("image", imageUrl).build();
        URL apiURL = new URL(api);
        HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Access-Token", accessToken);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        byte[] body = jsonImage.toString().getBytes();
        connection.setFixedLengthStreamingMode(body.length);
        OutputStream os = connection.getOutputStream();
        os.write(body);
        os.flush();
        int httpCode = connection.getResponseCode();
        if ( httpCode == 200 ){
            JsonReader jReader = Json.createReader(connection.getInputStream());
            JsonObject jsonBody = jReader.readObject();
            System.out.println(jsonBody);
        } else {
            JsonReader jReader = Json.createReader(connection.getErrorStream());
            JsonObject jsonError = jReader.readObject();
            System.out.println(jsonError);
        }
    }

}
