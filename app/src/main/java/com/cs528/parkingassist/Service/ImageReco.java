package com.cs528.parkingassist.Service;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.Util.Constants;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageReco {
    private static ImageReco instance = null;
    private Parking carInfo;

    public static ImageReco getInstance() {
        if (instance == null)
            instance = new ImageReco();
        return instance;
    }

    public ImageReco() {
    }

    public boolean detectCar(byte[] image_Byte){

        try {
            URL apiURL = new URL(Constants.RECOGNITION_API);
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestProperty("Content-Type", "application/octet-stream");
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
                JsonReader jReader = Json.createReader(connection.getInputStream());
                JsonObject jsonBody = jReader.readObject();
                Log.e("The return message is",""+jsonBody);
                Parking carInfo = detect_CarPlate(jsonBody);
                this.carInfo = carInfo;
                connection.disconnect();
                if(carInfo == null){
                    return false;
                }
                else{
                    return true;
                }
            } else {
                Log.e("The Error has Occurred","The http code "+httpCode);
                Log.e("The Error has Occurred",""+connection.getErrorStream());
                connection.disconnect();
                return false;
            }

        }catch (Exception ex){
            Log.e("The Error has Occurred","Exception: "+ex);
            return false;

        }

    }

    public Parking detect_CarPlate(JsonObject result){
        Parking carInfo = null;
        if(result!=null){
            JsonArray objects = result.getJsonArray("objects");
            if (!objects.isEmpty()){
                JsonObject vehicleAnnotation = objects.getJsonObject(0).getJsonObject("vehicleAnnotation");
                JsonObject attributes = vehicleAnnotation.getJsonObject("attributes");
                JsonObject carSystem = attributes.getJsonObject("system");
                JsonObject carColor = carSystem.getJsonObject("color");
                String color  = carColor.getString("name");
                JsonObject carMake = carSystem.getJsonObject("make");
                String make  = carMake.getString("name");
                JsonObject carModel = carSystem.getJsonObject("model");
                String model  = carModel.getString("name");
                Log.e("The the car info is",""+color+make+model);
                JsonObject licenseplate = vehicleAnnotation.getJsonObject("licenseplate");
                JsonObject licenseBounding = licenseplate.getJsonObject("bounding");
                JsonObject licenseAttributes = licenseplate.getJsonObject("attributes");
                JsonObject licenseSystem = licenseAttributes.getJsonObject("system");
                JsonObject licenseSystemString = licenseSystem.getJsonObject("string");
                String licenseSystemStringName = licenseSystemString.getString("name");
                Log.e("The the car license is",""+licenseSystemStringName);
                carInfo = new Parking(make,model,color,licenseSystemStringName);
            }
            else{
                Log.e("The the car info is","empty");
            }

        }
        return carInfo;
    }

    public Parking getCarInfo(){
        return carInfo;
    }

}
