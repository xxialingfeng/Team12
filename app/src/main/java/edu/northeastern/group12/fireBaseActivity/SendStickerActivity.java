package edu.northeastern.group12.fireBaseActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import edu.northeastern.group12.R;

public class SendStickerActivity extends AppCompatActivity {
    Button send; // button for send sticker
    TextView recipient; // textView for user who receives a sticker
    TextView sender; // textView for user who sends a sticker
    Spinner spinnerRecipientUsername; // spinner for recipient username
    String recipientUsername; // user who receives a sticker
    String senderUsername; // user who sends a sticker
    String sticker; // sticker to be sent
    int stickerId; // sticker id
    SharedPreferences globalLoginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        globalLoginData = getSharedPreferences("login", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null) {
            String recipientUsername = intent.getStringExtra("recipient");
            if (recipientUsername != null) {
                recipient.setText(recipientUsername);
            }
        }
        assert intent != null;
        stickerId = intent.getIntExtra("stickerId", 0);
        if (stickerId != 0) {
            sticker = getString(stickerId);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSticker();
            }
        });
    }

    /**
     * Send sticker
     */
    private void sendSticker() {
        recipientUsername = recipient.getText().toString();
        senderUsername = sender.getText().toString();

        if (recipientUsername.isEmpty() || senderUsername.isEmpty() || sticker.isEmpty()) {
            return;
        }

        // update sender's sent count and send history

        // update recipient's receive history

        addDataToDb(senderUsername, recipientUsername, sticker);
        Intent intent = new Intent(this, FCMActivity.class);
        startActivity(intent);
    }

    /**
     * Add data to firebase
     */
    public void addDataToDb(String sender, String recipient, String sticker) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> data = new HashMap<>();
        data.put("sender", sender);
        data.put("recipient", recipient);
        data.put("sticker", sticker);
        reference.child("chat").push().setValue(data);
    }
}
