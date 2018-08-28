package com.example.lance.ht6.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.lance.ht6.schemas.EventsTableContract.EventsEntry;
import com.example.lance.ht6.schemas.ReportPerMinuteTableContract.ReportPerMinuteEntry;
import com.example.lance.ht6.schemas.CountsTableContract.CountsEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DatabaseUtilities {

    public static SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /** Updates the events table **/
    public static void updateEvents(SQLiteDatabase dbEvents,
                                    String text,
                                    List<String> wordList,
                                    int sessionId) {
        ContentValues newEvent = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        if (wordList.contains(text)) {
            // insert database record
            newEvent.put(EventsEntry.TIMESTAMP_COLUMN, date);
            newEvent.put(EventsEntry.WORD_COLUMN, text);
            newEvent.put(EventsEntry.SESSION_COLUMN, sessionId);
            dbEvents.insert(EventsEntry.TABLE_NAME, null, newEvent);
        }
    }

    /** Updates the total counts table. **/
    public static void updateCounts(SQLiteDatabase dbCounts,
                                    String text,
                                    List<String> wordList,
                                    int sessionId) {
        int count = 1;
        ContentValues newCount = new ContentValues();
        // Get existing count
        if (wordList.contains(text)){
            Cursor result = dbCounts.rawQuery("SELECT count from Counts where word = ? and session = ?", new String[]{text});
            if (result.moveToFirst()) {
                count = result.getInt(0) + 1;
            }
        }
        // insert database record
        newCount.put(CountsEntry.WORD_COLUMN, text);
        newCount.put(CountsEntry.COUNT_COLUMN, count);
        newCount.put(CountsEntry.SESSION_COLUMN, sessionId);
        dbCounts.insert(CountsEntry.TABLE_NAME, null, newCount);
    }

    /** Return plot data for one word **/
    public static ReportData generatePlotData(SQLiteDatabase dbReports,
                                 String word,
                                 int sessionId){

        String sqlQuery = "select interval, count from ReportMinutes where word = ?";
        String[] sqlQueryArguments = { word};
        Cursor resultRows = dbReports.rawQuery(sqlQuery, sqlQueryArguments);
        if (resultRows.moveToFirst()) {
            return new ReportData(word, getNewValuesPosn(resultRows, word));
        }
        ArrayList<Posn> x = new ArrayList<Posn>();
//        x.add(new Posn(1, 2));
        return new ReportData("test", x);
    }

    /** Generates <word, count> list from queried rows. **/
    private static ArrayList<Posn> getNewValuesPosn(Cursor newRows, String word) {
        Random rand = new Random();
        ArrayList<Posn> retVal = new ArrayList<>();
        Posn newValues;
        if(newRows.moveToFirst()) {
            do {
//                updateMap(wordListHard);
                if (newRows.getString(0) == null) {
                    throw new RuntimeException("Interval string in Report is null");
                }
                newValues = new Posn(newRows.getString(0), newRows.getInt(1));
//                newValues = new Posn(Integer.toString(map.get(word)), newRows.getInt(1));
                retVal.add(newValues);
            } while(newRows.moveToNext());
        }
        return retVal;
    }

    /** Gets the table values from the rows queried from a table. **/
    private static ArrayList<ContentValues> getNewValues(Cursor newRows) {
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
        String newRowQuery = "select datetime((strftime('%s', timestamp) / 60) * 60, 'unixepoch') as interval, word, count(*) as count, session " +
                "from Events " +
                "where session = ?" +
                "GROUP BY interval, word, session " +
                "ORDER BY interval";

        // get new rows from events
        Cursor newRows = dbEvents.rawQuery(newRowQuery, new String[]{ Integer.toString(sessionId) });

        // loop through the new rows
        dbReports.beginTransaction();

        ArrayList<ContentValues> updatedTable = getNewValues(newRows);

        for (int i = 0; i < updatedTable.size(); i++) {
            dbReports.insert(
                    ReportPerMinuteEntry.TABLE_NAME,
                    null,
                    updatedTable.get(i));
        }

        dbReports.setTransactionSuccessful();
        dbReports.endTransaction();
    }

    /** Gets the session id from the Events table **/
    public static int getLastSessionId(SQLiteDatabase dbEvents) {
        // get most recent session
        String sqlQuery = "select session from Events";
        Cursor result = dbEvents.rawQuery(sqlQuery, new String[]{});
        if (result.moveToLast() && result.getInt(0) >= 0) {
            return result.getInt(0);
        }
        return 0;
    }


    /** Deletes all the entries in all tables. **/
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

    /** Converts passed in YYYY-MM-DD hh:mm:ss string to a julian date (float). **/
    public static float dateStringToFloat(String startDate) {
        long milliseconds = 0l;
        try {
            Date d = f.parse(startDate);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (float) milliseconds;

    }

    /** Converts the YYYY-MM-DD hh:mm:ss string to a float for graphing relative to an initial. **/
    public static float dateStringToFloatRelative(float startDate, String date) {
        long milliseconds = 0l;
        try {
            Date d = f.parse(date);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((float) milliseconds) - startDate;
    }
}
