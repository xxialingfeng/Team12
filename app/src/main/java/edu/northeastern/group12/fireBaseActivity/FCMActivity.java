package edu.northeastern.group12.fireBaseActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FCMActivity extends AppCompatActivity {
    private String clientToken;
    private String serverToken; // from Firebase console
    private String baseURL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        // retrieve the client token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    clientToken = task.getResult();
                    sendMessage(clientToken);
                } else {
                    return;
                }
            }
        });
    }

    private void sendMessage(String clientRegistrationToken) {
        JSONObject payloadData = new JSONObject();

        // send data
        JSONObject messageData = new JSONObject();

        // display a notification
        JSONObject notificationData = new JSONObject();

        try {
            notificationData.put("title", "New Sticker Received!");
            notificationData.put("body", "Click to see the new sticker!");
            notificationData.put("url", url); // URL for sticker image
            notificationData.put("sound", "default");
            notificationData.put("badge", "1");

            messageData.put("title", "title");
            messageData.put("content", "data");

            payloadData.put("to", clientToken);
            payloadData.put("priority", "high");
            payloadData.put("notification", notificationData);
            payloadData.put("data", messageData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            // set up HTTP connection to FCM
            HttpURLConnection connection = (HttpURLConnection) new URL(baseURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", serverToken);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // send payload to FCM
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(payloadData.toString().getBytes("UTF-8"));
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
