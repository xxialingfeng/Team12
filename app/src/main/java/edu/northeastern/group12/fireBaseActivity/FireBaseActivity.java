package edu.northeastern.group12.fireBaseActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.northeastern.group12.R;


public class FireBaseActivity extends AppCompatActivity {

    private static final int STICKER_ID_MIN = 1;
    private static final int STICKER_ID_MAX = 10;
    private DatabaseReference databaseReference;
    private ChildEventListener newStickerEventListener;

    private Map<Integer, TextView> stickerNumberSentTextViews;

    private String loggedInUser;
    private int selectedStickerId;
    private String selectedReceiver;
    private Map<Integer, Integer> sentCount;
    private List<String> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sentCount = new HashMap<>();
        users = new ArrayList<>();
            }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("loggedInUser", loggedInUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newStickerEventListener != null) {
            databaseReference.child("inbox").child(loggedInUser).removeEventListener(newStickerEventListener);
        }
    }

    private void loadUsers() {

        databaseReference.child("users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.err.println("Error");
            } else {
                Set<String> allUsers = ((Map<String, Object>) task.getResult().getValue()).keySet();
                for (String user : allUsers) {
                    if (!user.equals(loggedInUser)) {
                        users.add(user);
                    }
                }
                Spinner spinner = findViewById(R.id.spinnerSendTo);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, users);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedReceiver = (String) adapterView.getItemAtPosition(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }
        });
    }

    private void loadSentCount() {
        for (int i = STICKER_ID_MIN; i <= STICKER_ID_MAX; i++) {
            int finalI = i;
            databaseReference
                    .child("count")
                    .child(loggedInUser)
                    .child(String.valueOf(i))
                    .get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            System.err.println("Error");
                        } else {
                            Integer value = task.getResult().getValue(Integer.class);
                            value = value == null ? 0 : value;
                            sentCount.put(finalI, value);
                            updateStickerNumberSentText(finalI, value);
                        }
                    });
        }
    }

    private void updateStickerNumberSentText(int stickerId, int count) {
        stickerNumberSentTextViews.get(stickerId).setText(getResources().getString(R.string.sent_indicator_text) + count);
    }


    public void sendClicked(View view) {
        sendSticker(selectedReceiver, selectedStickerId);
    }

    private void sendSticker(String receiver, int stickerId) {
        System.out.println("Sending " + stickerId + " to " + receiver);

        sentCount.put(stickerId, sentCount.get(stickerId) + 1);
        updateStickerNumberSentText(stickerId, sentCount.get(stickerId));

        databaseReference
                .child("count")
                .child(loggedInUser)
                .child(String.valueOf(stickerId)).setValue(sentCount.get(stickerId));

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", loggedInUser);
        payload.put("stickerId", stickerId);
        payload.put("ts", Instant.now().getEpochSecond());

        databaseReference
                .child("inbox")
                .child(receiver)
                .push().setValue(payload);

    }

}
