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
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatabaseUtilities {

    public static int COUNTER = 1;
    public static Map<String, Integer> map = new HashMap<String, Integer>();
    public static List<String> wordListHard = Arrays.asList(new String[]{"um", "like", "alice", "lance", "phil", "jerry"});

    public static void updateMap(List<String> wordList) {
        for (int i=0; i < wordList.size(); i++) {
            if (map.containsKey(wordList.get(i))) {
                map.put(wordList.get(i), map.get(wordList.get(i)) + 1);
            }
            else {
                map.put(wordList.get(i), 1);
            }
        }
    }


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
        String sqlQuery = "select interval, count from ReportMinutes where word = ?";
        String[] sqlQueryArguments = { word };
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
//                int x = rand.nextInt(1000);
                COUNTER += 1;
                int x = COUNTER;
                updateMap(wordListHard);
//                newValues = new Posn(Integer.toString(x), newRows.getInt(1));
                newValues = new Posn(Integer.toString(map.get(word)), newRows.getInt(1));
//                if (newRows.getString(0) == null){
//                    newValues = new Posn
//                }
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
        String newRowQuery = "select datetime((strftime('%s', timestamp) / 3600) * 3600, 'unixepoch') as interval, word, count(*) as count " +
                "from Events " +
                "GROUP BY interval, word " +
                "ORDER BY interval";

        // get new rows from events
        Cursor newRows = dbEvents.rawQuery(newRowQuery, new String[]{});

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
            dbReports.insert(
                    ReportPerMinuteEntry.TABLE_NAME,
                    null,
                    updatedTable.get(i));
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
