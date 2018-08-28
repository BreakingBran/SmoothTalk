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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Report extends AppCompatActivity {

    private static final String TAG = "Report";
    Button go_back_report;
    LineChart chart;

    private static Date today = new Date();
    private static String todayString = DatabaseUtilities.f.format(today);
    public static float initialTime = DatabaseUtilities.dateStringToFloat(todayString);

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

    /** Generates graph from a list of data points in form of ReportData objects. **/
    private static void generateGraph(LineChart chart, ArrayList<ReportData> items){
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        Random rand = new Random();
        LineData lineData;
        int randomColour;
        // Get the initial time
        if (items.size() > 0) {
            ArrayList<Posn> firstItem = items.get(0).getPosns();
            if (firstItem.size() > 0) {
                String firstDate = firstItem.get(0).getX();
                initialTime = DatabaseUtilities.dateStringToFloat(firstDate);
            }
        }

        for(ReportData item : items){
            List<Entry> entries = new ArrayList<Entry>();
            // Plot all points zero-ed at the first interval
            for (Posn data : item.getPosns()) {
                // turn your data into Entry objects
                entries.add(new Entry(DatabaseUtilities.dateStringToFloatRelative(
                        initialTime,
                        data.getX()),
                        data.getY()));
            }
            // Add entries to dataset
            LineDataSet dataSet = new LineDataSet(entries, item.getWord());
            randomColour = rand.nextInt();
            dataSet.setColor(randomColour);
            dataSet.setValueTextColor(randomColour);
            // Add dataset to all datasets
            dataSets.add(dataSet);
        }
        lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate(); // refresh

    }
}
