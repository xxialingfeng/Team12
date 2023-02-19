package edu.northeastern.group12;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button atYourServiceButton = findViewById(R.id.atYouServiceButton);
        atYourServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWebServiceActivity(view);
            }
        });

    }

    public void startWebServiceActivity(View view) {
        Intent intent = new Intent(this, WebServiceActivity.class);
        startActivity(intent);
    }

}
