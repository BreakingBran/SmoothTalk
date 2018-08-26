package com.example.lance.ht6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Report extends AppCompatActivity {

    private static final String TAG = "Report";
    Button go_back_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        go_back_report = (Button) findViewById(R.id.go_back_report);

        go_back_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked btnNavToMain");
                Intent intent = new Intent(Report.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
