package com.snippet.snippet.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Kyle on 12/3/2016.
 */

public final class FileDatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FileDatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class FileDatabase implements BaseColumns {
        public static final String TABLE_NAME = "Files";
        public static final String COLUMN_NAME_FILEPATH = "FilePath";
        public static final String COLUMN_NAME_AUTOTAGGED = "Auto-Tagged";
        //ID field included in the BaseColumns interface
    }
}