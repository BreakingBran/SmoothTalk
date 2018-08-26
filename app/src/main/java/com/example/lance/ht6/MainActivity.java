package com.example.lance.ht6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    ImageButton settingsButton;
    Button startButton;
    ImageButton reportButton;
    DatabaseHelper myDb;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");
        settingsButton = findViewById(R.id.settings_button);
        reportButton = findViewById(R.id.report_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked the go to create new words (settings) button");
                Intent settingsIntent = new Intent(MainActivity.this, NewWords.class);
                startActivity(settingsIntent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked the go to reports button");
                Intent writeReport = new Intent(MainActivity.this, WriteReport.class);
                startActivity(writeReport);
            }
        });

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startRecording = new Intent(MainActivity.this, Recording.class);
                startActivity(startRecording);
            }
        });
        myDb = new DatabaseHelper(this);
    }

}
