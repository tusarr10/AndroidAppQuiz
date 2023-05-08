package com.wrteam.quiz.service;


import android.content.Intent;
import android.util.Log;

import com.wrteam.quiz.Constant;
import com.wrteam.quiz.activity.CategoryActivity;
import com.wrteam.quiz.activity.MainActivity;
import com.wrteam.quiz.helper.MyNotificationManager;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.model.Question;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static ArrayList<Question> questionList;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Getting registration token

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + s);

        // Saving reg id to shared preferences
        Session.setDeviceToken(s, getApplicationContext());


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            try {
                JSONObject json = new JSONObject((remoteMessage.getData()));

                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //fireBase cloud messaging
    private void sendPushNotification(JSONObject json) {
        try {



            String language_id = json.getString("language_id");
            String typeId = json.getString("type_id");
            String message = json.getString("body");
            String type = json.getString("type");
            String no_of = json.getString("no_of");
            String title = json.getString("title");
            String imageUrl=json.getString("image");


            if (type.equalsIgnoreCase("default"))
                Session.setNCount((Session.getNCount(getApplicationContext()) + 1), getApplicationContext());


            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            //creating an intent for the notification
            Intent intent;
            if (type.equalsIgnoreCase("category")) {
                intent = new Intent(getApplicationContext(), CategoryActivity.class);
                intent.putExtra("cateId", typeId);
                intent.putExtra("quiz_type", Constant.REGULAR);
                intent.putExtra("no_of", no_of);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("cateId", typeId);
                intent.putExtra("type", "default");
                intent.putExtra("no_of", no_of);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }


            //if there is no image
            if (imageUrl.equals("no_image")) {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
