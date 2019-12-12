package com.cs528.parkingassist.Service;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ActivityRec {
    private Context context;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private ActivityTransitionRequest request;


    private static ActivityRec instance = null;

    public static ActivityRec getInstance(Context context) {
        if (instance == null)
            instance = new ActivityRec(context);
        return instance;
    }

    public ActivityRec(Context context) {
        this.context = context;
        List<ActivityTransition> transitions = new ArrayList<>();
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        mActivityRecognitionClient = ActivityRecognition.getClient(context);
        request = new ActivityTransitionRequest(transitions);
    }

    public void start(PendingIntent pendingIntent){
        Task<Void> task = mActivityRecognitionClient.requestActivityTransitionUpdates(
                request,
                pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(context,
                        "Activity Recognition Enabled",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Error", "Not enabled");
                Toast.makeText(context,
                        "Not enabled",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
