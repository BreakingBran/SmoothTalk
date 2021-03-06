package com.example.lance.ht6.schemas;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.example.lance.ht6.schemas.CountsTableContract.CountsEntry;


public class CountsTableDbHelper extends SQLiteOpenHelper {
    Context context;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CountsEntry.TABLE_NAME + " (" +
                    CountsEntry._ID + " INTEGER PRIMARY KEY," +
                    CountsEntry.WORD_COLUMN + " TEXT," +
                    CountsEntry.COUNT_COLUMN + " INTEGER," +
                    CountsEntry.SESSION_COLUMN + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CountsEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Counts.db";

    public CountsTableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

