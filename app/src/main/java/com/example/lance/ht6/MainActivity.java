package com.example.lance.ht6;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.lance.ht6.schemas.CountsTableDbHelper;
import com.example.lance.ht6.schemas.EventsTableDbHelper;
import com.example.lance.ht6.schemas.ReportPerMinuteDbHelper;
import com.example.lance.ht6.utils.DatabaseUtilities;

public class MainActivity extends AppCompatActivity {

    ImageButton settingsButton;
    Button startButton;
    Button clearButton;
    ImageButton reportButton;
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

        clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase dbEvents = new EventsTableDbHelper(getContext()).getWritableDatabase();
                SQLiteDatabase dbCounts = new CountsTableDbHelper(getContext()).getWritableDatabase();
                SQLiteDatabase dbReports = new ReportPerMinuteDbHelper(getContext()).getWritableDatabase();
                DatabaseUtilities.resetTables(dbEvents, dbCounts, dbReports);
            }
        });
    }

    public Context getContext() {
        return this.getApplicationContext();
    }

}
