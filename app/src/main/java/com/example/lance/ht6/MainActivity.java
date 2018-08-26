package com.example.lance.ht6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton settingsButton;
    Button startButton;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent enterWords = new Intent(MainActivity.this, NewWords.class);
                startActivity(enterWords);
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
