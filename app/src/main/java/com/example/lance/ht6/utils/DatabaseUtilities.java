package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lance.ht6.schemas.EventsTableContract.EventsEntry;
import com.example.lance.ht6.schemas.ReportPerMinuteTableContract;
import com.example.lance.ht6.schemas.ReportPerMinuteTableContract.ReportPerMinuteEntry;
import com.example.lance.ht6.schemas.CountsTableContract.CountsEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseUtilities {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    public void updateEvents(SQLiteDatabase dbEvents,
                             String text,
                             List<String> wordList) {
        ContentValues newEvent = new ContentValues();
        String date = df.format(Calendar.getInstance().getTime());
        if (wordList.contains(text)) {
            // insert database record
            newEvent.put(EventsEntry.TIMESTAMP_COLUMN, date);
            newEvent.put(EventsEntry.WORD_COLUMN, text);
            dbEvents.insert(EventsEntry.TABLE_NAME, null, newEvent);
        }
    }

    public void updateCounts(SQLiteDatabase dbCounts,
                             String text,
                             List<String> wordList) {
        int count = 0;
        ContentValues newCount = new ContentValues();
        // Get existing count
        if (wordList.contains(text)){
            Cursor result = dbCounts.rawQuery("SELECT count from Counts where word = ?", new String[]{text});
            if (result.moveToFirst()) {
                count = result.getInt(0) + 1;
            }
        }
        // insert database record
        newCount.put(CountsEntry.WORD_COLUMN, text);
        newCount.put(CountsEntry.COUNT_COLUMN, count);
        dbCounts.insert(CountsEntry.TABLE_NAME, null, newCount);
    }

    /** Updates Reports table from Events Table **/
    public void createReportPerMinute(SQLiteDatabase dbEvents,
                                      SQLiteDatabase dbReports,
                                      List<String> wordList,
                                      int sessionId) {
        // update dbReports
    }

    public void resetTables(SQLiteDatabase dbEvents,
                            SQLiteDatabase dbCounts,
                            SQLiteDatabase dbReports) {
        dbEvents.execSQL("delete from " + EventsEntry.TABLE_NAME);
        dbCounts.execSQL("delete from " + CountsEntry.TABLE_NAME);
        dbReports.execSQL("delete from " + ReportPerMinuteEntry.TABLE_NAME);
    }

    public static List<String> getWordList(File filesDir) {
        List<String> wordList = new ArrayList<>();
        File keywords = new File(filesDir, "keywords.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(keywords));
            String line;

            while ((line = br.readLine()) != null) {
                wordList.add(line.split("\\s+")[0]);
            }
            br.close();
        }
        catch (IOException e) {
        }
        return wordList;
    }
}
