package com.example.lance.ht6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lance.ht6.utils.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Report extends AppCompatActivity {

    private static final String TAG = "Report";
    Button go_back_report;
    LineChart chart;

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

        chart = (LineChart) findViewById(R.id.chart);

        ArrayList<ReportData> items = WriteReport.getReportData();

        generateGraph(chart, items);

    }

    private static float dateStringToFloat(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long milliseconds = 0l;
        try {
            Date d = f.parse(date);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (float) milliseconds;
    }

    private static void generateGraph(LineChart chart, ArrayList<ReportData> items){
//        PHIL WILL MAKE A FUNCTION THAT WILL GIVE A LIST OF DATA POINTS
        Random rand = new Random();
        int randomColour;
        for(ReportData item : items){
            List<Entry> entries = new ArrayList<Entry>();
            for (Posn data : item.getPosns()) {
                // turn your data into Entry objects
                entries.add(new Entry(dateStringToFloat(data.x()), data.y()));
            }
            LineDataSet dataSet = new LineDataSet(entries, item.getWord()); // add entries to dataset
            randomColour = rand.nextInt();
            dataSet.setColor(randomColour);
            dataSet.setValueTextColor(randomColour);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate(); // refresh

        }

    }
}
