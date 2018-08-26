package com.example.lance.ht6;

import android.provider.BaseColumns;
import android.util.EventLog;

public final class ReportsTableContract {
    private ReportsTableContract() {
    }

    public static class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Reports";
        public static final String DATE_COLUMN = "date";
        public static final String WORD_COLUMN = "word";
        public static final String HOUR_COLUMN = "hour";
        public static final String COUNT_COLUMN = "count";
    }
}