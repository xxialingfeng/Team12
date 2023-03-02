package edu.northeastern.group12;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.group12.fireBaseActivity.FireBaseActivity;
import edu.northeastern.group12.fireBaseActivity.LoginActivity;

public class MainActivity extends AppCompatActivity {
    Button button1;
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

        Button FireBaseButton = findViewById(R.id.FireBaseButton);
        FireBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFireBaseActivity(v);
            }
        });

        Button AboutButton = findViewById(R.id.AboutButton);
        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutActivity(v);
            }
        });

    }

    public void startWebServiceActivity(View view) {
        Intent intent = new Intent(this, WebServiceActivity.class);
        startActivity(intent);
    }

    public void startFireBaseActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startAboutActivity(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

}
