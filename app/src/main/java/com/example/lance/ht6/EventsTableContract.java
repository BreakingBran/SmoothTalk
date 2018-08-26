package com.example.lance.ht6;

import android.provider.BaseColumns;
import android.util.EventLog;

public final class EventsTableContract {
    private EventsTableContract() {
    }

    public static class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Events";
        public static final String TIMESTAMP_COLUMN = "timestamp";
        public static final String WORD_COLUMN = "word";
    }
}