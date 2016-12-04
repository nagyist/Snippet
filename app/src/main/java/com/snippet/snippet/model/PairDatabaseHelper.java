package com.snippet.snippet.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kyle on 12/3/2016.
 */

public class PairDatabaseHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PairDatabaseContract.PairDatabase.TABLE_NAME + " (" +
                    PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    " PRIMARY KEY (" + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + COMMA_SEP + PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + ")" + COMMA_SEP +
                    " FOREIGN KEY " + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " REFERENCES " + FileDatabaseContract.FileDatabase.TABLE_NAME + "(" + FileDatabaseContract.FileDatabase._ID + ")" + " ON UPDATE CASCADE" + COMMA_SEP +
                    " FOREIGN KEY " + PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + " REFERENCES " + TagDatabaseContract.TagDatabase.TABLE_NAME + "(" + TagDatabaseContract.TagDatabase._ID + ")" + " ON UPDATE CASCADE" + COMMA_SEP +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TagDatabaseContract.TagDatabase.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tags.db";

    public PairDatabaseHelper(Context context) {
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