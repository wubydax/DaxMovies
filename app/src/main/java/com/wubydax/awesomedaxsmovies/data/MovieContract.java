package com.wubydax.awesomedaxsmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.File;

/**
 * Created by Anna Berkovitch on 26/10/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.wubydax.awesomedaxsmovies.Movies";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + File.pathSeparator + CONTENT_AUTHORITY + File.pathSeparator + MovieEntry.TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + File.pathSeparator + CONTENT_AUTHORITY + File.pathSeparator + MovieEntry.TABLE_NAME;



    public static final class MovieEntry implements BaseColumns{

        public static final String DATABASE_NAME = "Movies";
        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_TITLE_COLUMN = "TITLE";
        public static final String MOVIE_RELEASE_DATE_COLUMN = "DATE";
        public static final String MOVIE_SYNOPSIS_COLUMN = "SYNOPSIS";
        public static final String MOVIE_POPULARITY_COLUMN = "POPULARITY";
        public static final String MOVIE_RATING_COLUMN = "RATING";
        public static final String MOVIE_GENRE_COLUMN = "GENRE";
        public static final String MOVIE_POSTER_BITMAP_COLUMN = "POSTER_PATH";
        public static final String TMDB_ID = "_id";

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(TABLE_NAME).build();

        public Uri getContentUriWithId (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public Uri getContentUriWithTitle (String title){
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }


    }
}
