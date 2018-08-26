package com.example.lance.ht6.schemas;

import android.provider.BaseColumns;

public class ReportPerMinuteTableContract {
    private ReportPerMinuteTableContract() {
    }

    public static class ReportPerMinuteEntry implements BaseColumns {
        public static final String TABLE_NAME = "ReportMinutes";
        public static final String DATE_COLUMN = "date";
        public static final String MINUTE_COLUMN = "interval";
        public static final String WORD_COLUMN = "word";
        public static final String COUNT_COLUMN = "count";
        public static final String SESSION_COLUMN = "session";
    }
}
