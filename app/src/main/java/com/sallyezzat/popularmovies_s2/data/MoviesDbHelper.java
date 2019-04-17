package com.sallyezzat.popularmovies_s2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sallyezzat.popularmovies_s2.data.MovieContract.MoviesEntry.TABLE_NAME;

/**
 * Created by sahmed on 12/27/2017.
 */

class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //ONLY 2 functions
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + TABLE_NAME + " (" +
                        MovieContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MoviesEntry.COLUMN_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                        MovieContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                        MovieContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                        MovieContract.MoviesEntry.COLUMN_RELEASE_DATE + " DATE, " +
                        MovieContract.MoviesEntry.COLUMN_POSTER_IMG + " BLOB, " +
                        MovieContract.MoviesEntry.COLUMN_TITLE + " TEXT, " +
                        MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL" + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL(DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
