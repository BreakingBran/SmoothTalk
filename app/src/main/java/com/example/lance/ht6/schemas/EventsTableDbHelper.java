package com.example.lance.ht6.schemas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.lance.ht6.schemas.EventsTableContract.EventsEntry;

public class EventsTableDbHelper extends SQLiteOpenHelper {
    Context context;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventsTableContract.EventsEntry.TABLE_NAME + " (" +
                    EventsEntry._ID + " INTEGER PRIMARY KEY," +
                    EventsEntry.TIMESTAMP_COLUMN + " TEXT," +
                    EventsEntry.WORD_COLUMN + " TEXT," +
                    EventsEntry.SESSION_COLUMN + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventsEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Events.db";

    public EventsTableDbHelper(Context context) {
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
