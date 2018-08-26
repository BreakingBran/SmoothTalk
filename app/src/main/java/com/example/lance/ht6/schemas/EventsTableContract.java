package com.example.lance.ht6.schemas;

import android.provider.BaseColumns;
import android.util.EventLog;

public final class EventsTableContract {
    private EventsTableContract() {
    }

    public static class ReportPerMinuteEntry implements BaseColumns {
        public static final String TABLE_NAME = "Events";
        public static final String TIMESTAMP_COLUMN = "timestamp";
        public static final String WORD_COLUMN = "word";
        public static final String SESSION_COLUMN = "session";
    }
}