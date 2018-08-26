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

import com.example.lance.ht6.schemas.ReportPerMinuteDbHelper;
import com.example.lance.ht6.schemas.EventsTableDbHelper;
import com.example.lance.ht6.utils.DatabaseUtilities;
import com.example.lance.ht6.utils.ReportData;

import java.util.ArrayList;
import java.util.List;

public class WriteReport extends AppCompatActivity {

    private EventsTableDbHelper dbEventsHelper;
    private SQLiteDatabase dbEvents;
    private ReportPerMinuteDbHelper dbReportsHelper;
    private SQLiteDatabase dbReports;

    public static ArrayList<ReportData> allReportData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbEventsHelper = new EventsTableDbHelper(getContext());
        dbReportsHelper = new ReportPerMinuteDbHelper(getContext());
        dbEvents = dbEventsHelper.getWritableDatabase();
        dbReports = dbReportsHelper.getWritableDatabase();

        List<String> wordList = DatabaseUtilities.getWordList(getContext().getFilesDir());

//        int sessionId = DatabaseUtilities.getSessionId(dbEvents);
        int sessionId = 0;

        setContentView(R.layout.activity_write_report);

        DatabaseUtilities.createReportPerMinute(dbEvents,
                dbReports,
                wordList,
                sessionId);

        for (int i=0; i < wordList.size(); i++) {
            allReportData.add(DatabaseUtilities.generatePlotData(dbReports, wordList.get(i), sessionId));
        }

        Intent showReport = new Intent(WriteReport.this, Report.class);
        startActivity(showReport);

    }

    public Context getContext() {
        return this.getApplicationContext();
    }

    public static ArrayList<ReportData> getReportData() {
        return allReportData;
    }
}
