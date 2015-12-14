package com.wubydax.awesomedaxsmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anna Berkovitch on 26/10/2015.
 */
public class MyDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = MovieContract.MovieEntry.DATABASE_NAME;
    static final String CREATE_DB = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry.TMDB_ID + " INTEGER PRIMARY KEY NOT NULL UNIQUE, " +
            MovieContract.MovieEntry.MOVIE_TITLE_COLUMN + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.MOVIE_POPULARITY_COLUMN + " REAL NOT NULL, " +
            MovieContract.MovieEntry.MOVIE_GENRE_COLUMN + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.MOVIE_POSTER_BITMAP_COLUMN + " BLOB, " +
            MovieContract.MovieEntry.MOVIE_RATING_COLUMN + " REAL NOT NULL," +
            MovieContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.MOVIE_SYNOPSIS_COLUMN + " TEXT NOT NULL );";

    public MyDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
