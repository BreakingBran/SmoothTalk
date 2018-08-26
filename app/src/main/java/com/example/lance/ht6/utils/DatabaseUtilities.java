package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.lance.ht6.schemas.EventsTableContract.EventsEntry;
import com.example.lance.ht6.schemas.ReportPerMinuteTableContract;
import com.example.lance.ht6.schemas.ReportPerMinuteTableContract.ReportPerMinuteEntry;
import com.example.lance.ht6.schemas.CountsTableContract.CountsEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseUtilities {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        // build query
        String newRowQuery = "select datetime((strftime('%s', time) / 3600) * 3600, 'unixepoch') interval, word, count(*) as count " +
                "from Events " +
                "where session = ? " +
                "GROUP BY interval, word " +
                "ORDER BY interval";

        // get new rows from events
        Cursor newRows = dbEvents.rawQuery(newRowQuery, new String[]{ Integer.toString(sessionId) });
        ContentValues newValues;
        ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
        if(newRows.moveToFirst()) {
            do {
                newValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(newRows, newValues);
                retVal.add(newValues);
            } while(newRows.moveToNext());
        }

        // update rows
        String selection = ReportPerMinuteEntry.SESSION_COLUMN + " LIKE ?";
        String[] selectionArgs = { Integer.toString(sessionId) };

        dbReports.beginTransaction();

        for (int i = 0; i < retVal.size(); i++) {
            dbReports.update(
                    ReportPerMinuteEntry.TABLE_NAME,
                    retVal.get(i),
                    selection,
                    selectionArgs);
        }

        dbReports.setTransactionSuccessful();
        dbReports.endTransaction();
    }

    public void resetTables(SQLiteDatabase dbEvents,
                            SQLiteDatabase dbCounts,
                            SQLiteDatabase dbReports) {
        dbEvents.execSQL("delete from " + EventsEntry.TABLE_NAME);
        dbCounts.execSQL("delete from " + CountsEntry.TABLE_NAME);
        dbReports.execSQL("delete from " + ReportPerMinuteEntry.TABLE_NAME);
    }
}
