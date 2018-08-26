package com.example.lance.ht6;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lance.ht6.schemas.CountsTableDbHelper;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    ImageButton settingsButton;
    Button startButton;
    ImageButton reportButton;
    DatabaseHelper myDb;
    private static final String TAG = "MainActivity";

    /* Database Definitions */
    private EventsTableDbHelper dbEventsHelper;
    private SQLiteDatabase dbEvents;
    private CountsTableDbHelper dbCountsHelper;
    private SQLiteDatabase dbCounts;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


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
//                Intent writeReport = new Intent(MainActivity.this, WriteReport.class);
                Intent reportIntent = new Intent(MainActivity.this, Report.class);
                startActivity(reportIntent);
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
