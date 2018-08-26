package com.example.lance.ht6;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "words.db";
    public static final String TABLE_NAME = "words_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "WORDS";
    public static final String COL_3 = "COUNT";
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table words_table (ID INTEGER PRIMARY KEY AUTOINCREMENT, WORD TEXT,COUNT INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertWordData(String word, Integer count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("WORD",word);
        contentValues.put("COUNT",count);
        Log.d(TAG, "insertWordData: Called with word " + word + " and count " + count.toString());
        
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
}
