package com.example.lance.ht6.schemas;

import android.provider.BaseColumns;

public class CountsTableContract {
    private CountsTableContract() {
    }

    public static class CountsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Counts";
        public static final String WORD_COLUMN = "word";
        public static final String COUNT_COLUMN = "count";
    }
}
