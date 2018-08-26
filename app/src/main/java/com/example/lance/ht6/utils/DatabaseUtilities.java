package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lance.ht6.schemas.EventsTableContract.ReportPerMinuteEntry;
import com.example.lance.ht6.schemas.CountsTableContract.CountsEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            newEvent.put(ReportPerMinuteEntry.TIMESTAMP_COLUMN, date);
            newEvent.put(ReportPerMinuteEntry.WORD_COLUMN, text);
            dbEvents.insert(ReportPerMinuteEntry.TABLE_NAME, null, newEvent);
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
                            SQLiteDatabase dbCounts) {
        dbEvents.execSQL("delete from " + ReportPerMinuteEntry.TABLE_NAME);
        dbCounts.execSQL("delete from " + CountsEntry.TABLE_NAME);
    }
}
