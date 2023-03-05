package edu.northeastern.group12.fireBaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;

import edu.northeastern.group12.R;

public class StickerHistoryActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private StickerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mDatabaseRef;
    private ValueEventListener mReceivedStickersListener;

    private User mCurrentUser;
    private ArrayList<Sticker> mReceivedStickersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_history);


        // get current user from intent
        Intent intent = getIntent();
        mCurrentUser = (User) intent.getSerializableExtra("currentUser");

        // set up recycler view
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // set up sticker adapter
        mReceivedStickersList = new ArrayList<>();
        mAdapter = new StickerAdapter(mReceivedStickersList);
        mRecyclerView.setAdapter(mAdapter);


        // set up database reference and listener for received stickers
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("messages");
        mReceivedStickersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReceivedStickersList.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Sticker sticker = messageSnapshot.getValue(Sticker.class);
                    if (sticker != null && sticker.getToUser().equals(mCurrentUser.getUsername())) {
                        mReceivedStickersList.add(sticker);
                    }
                }
                Collections.sort(mReceivedStickersList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StickerHistoryActivity.this, "Failed to retrieve received stickers.", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseRef.addValueEventListener(mReceivedStickersListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseRef.removeEventListener(mReceivedStickersListener);
    }


}
