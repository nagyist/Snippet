package com.snippet.snippet.model;

import android.provider.BaseColumns;

/**
 * Created by Kyle on 12/3/2016.
 */

public final class TagDatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TagDatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class TagDatabase implements BaseColumns {
        public static final String TABLE_NAME = "Tags";
        public static final String COLUMN_NAME_TAGNAME = "TagName";
    }
}
