package edu.northeastern.group12.fireBaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.group12.R;

public class StickerHistoryActivity extends AppCompatActivity{
    private List<Sticker> stickers;
    private String username;
    private DatabaseReference databaseReference;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_history);
        // Set the userName, databaseReference and stickers
        username = getSharedPreferences("login", MODE_PRIVATE).getString("username", "unknownUser");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        stickers = new ArrayList<>();
        // Query from database and update history
        update();
        queryFromDatabase();
    }

    /**
     * Update the history layout
     */
    private void update() {
        RecyclerView received = findViewById(R.id.receivedHistoryRV);
        received.setLayoutManager(new LinearLayoutManager(this));
        received.setAdapter(new StickerAdapterRecyclerView(stickers));

        TextView countView = (TextView) findViewById(R.id.count);
        countView.setText("# stickers sent: " + count);
    }

    /**
     * Query the history from firebase database and update the layout
     */
    private void queryFromDatabase() {
        stickers = new ArrayList<>();
        count = 0;
        databaseReference.child("stickers").get().addOnCompleteListener((t) -> {
            HashMap<String, HashMap<String, Object>> tempMap = (HashMap) t.getResult().getValue();
            if (tempMap == null) return;
            for (String Key : tempMap.keySet()) {
                String fromUser = (String) Objects.requireNonNull(tempMap.get(Key)).get("fromUser");
                String id = String.valueOf(Objects.requireNonNull(tempMap.get(Key)).get("imageId"));
                long sendTime = (long) tempMap.get(Key).get("sendTimeEpochSecond");
                String toUser = (String) Objects.requireNonNull(tempMap.get(Key)).get("toUser");
                if (toUser != null && toUser.equals(username)) {
                    stickers.add(new Sticker(Integer.parseInt(id), fromUser, toUser, sendTime));
                }
                if (fromUser.equals(username)) {
                    count += 1;
                }
            }
            stickers.sort(Collections.reverseOrder());
            update();
        });
    }
}
