package com.snippet.snippet.model;

import android.provider.BaseColumns;

/**
 * Created by Kyle on 12/3/2016.
 */

public final class PairDatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PairDatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class PairDatabase implements BaseColumns {
        public static final String TABLE_NAME = "Pairs";
        public static final String COLUMN_NAME_TAGID = "TagID";
        public static final String COLUMN_NAME_FILEID = "FileID";
    }
}
