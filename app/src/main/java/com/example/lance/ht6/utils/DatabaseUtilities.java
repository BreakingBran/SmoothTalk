package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lance.ht6.schemas.EventsTableContract.EventsEntry;
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

    public void resetTables(SQLiteDatabase dbEvents,
                            SQLiteDatabase dbCounts) {
        dbEvents.execSQL("delete from " + EventsEntry.TABLE_NAME);
        dbCounts.execSQL("delete from " + CountsEntry.TABLE_NAME);
    }
}
