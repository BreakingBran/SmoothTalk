package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

    public static void updateEvents(SQLiteDatabase dbEvents,
                             String text,
                             List<String> wordList) {
        ContentValues newEvent = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        if (wordList.contains(text)) {
            // insert database record
            newEvent.put(EventsEntry.TIMESTAMP_COLUMN, date);
            newEvent.put(EventsEntry.WORD_COLUMN, text);
            dbEvents.insert(EventsEntry.TABLE_NAME, null, newEvent);
        }
    }

    public static void updateCounts(SQLiteDatabase dbCounts,
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

    /** Return plot data for one word **/
    public static ReportData generatePlotData(SQLiteDatabase dbReports,
                                 String word,
                                 int sessionId){

        String sqlQuery = "select interval, count from ReportsMinute where word = ? and session = ?";
        String[] sqlQueryArguments = { word, Integer.toString(sessionId) };
        Cursor resultRows = dbReports.rawQuery(sqlQuery, sqlQueryArguments);
        return new ReportData(word, getNewValuesPosn(resultRows));
    }

    /** Generates <word, count> list from queried rows. **/
    private static ArrayList<Posn> getNewValuesPosn(Cursor newRows) {
        ArrayList<Posn> retVal = new ArrayList<>();
        Posn newValues;
        if(newRows.moveToFirst()) {
            do {
                newValues = new Posn(newRows.getString(0), newRows.getInt(1));
                retVal.add(newValues);
            } while(newRows.moveToNext());
        }
        return retVal;
    }

    private ArrayList<ContentValues> getNewValues(Cursor newRows) {
        ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
        ContentValues newValues;
        if(newRows.moveToFirst()) {
            do {
                newValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(newRows, newValues);
                retVal.add(newValues);
            } while(newRows.moveToNext());
        }
        return retVal;
    }


    /** Updates Reports table from Events Table **/
    public static void createReportPerMinute(SQLiteDatabase dbEvents,
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

        // update rows where session matches the session_id
        String selection = ReportPerMinuteEntry.SESSION_COLUMN + " LIKE ?";
        String[] selectionArgs = { Integer.toString(sessionId) };

        // loop through the new rows
        dbReports.beginTransaction();

        ArrayList<ContentValues> updatedTable = new ArrayList<ContentValues>();
        ContentValues newValues;

        if(newRows.moveToFirst()) {
            do {
                newValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(newRows, newValues);
                updatedTable.add(newValues);
            } while(newRows.moveToNext());
        }

        for (int i = 0; i < updatedTable.size(); i++) {
            dbReports.update(
                    ReportPerMinuteEntry.TABLE_NAME,
                    updatedTable.get(i),
                    selection,
                    selectionArgs);
        }

        dbReports.setTransactionSuccessful();
        dbReports.endTransaction();
    }

    public static int getSessionId(SQLiteDatabase dbEvents) {
        // get most recent session
        String sqlQuery = "select session from Events";
        Cursor result = dbEvents.rawQuery(sqlQuery, new String[]{});
        if (result.moveToLast()) {
            return result.getInt(0);
        }
        return 0;
    }

    public static void resetTables(SQLiteDatabase dbEvents,
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
