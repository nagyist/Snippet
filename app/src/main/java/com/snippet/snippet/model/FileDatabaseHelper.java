package com.snippet.snippet.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kyle on 12/3/2016.
 */

public class FileDatabaseHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FileDatabaseContract.FileDatabase.TABLE_NAME + " (" +
                    FileDatabaseContract.FileDatabase._ID + " INTEGER PRIMARY KEY" + " AUTOINCREMENT" + COMMA_SEP +
                    FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + BOOLEAN_TYPE + NOT_NULL + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FileDatabaseContract.FileDatabase.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Files.db";

    public FileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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