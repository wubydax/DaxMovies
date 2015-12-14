package com.wubydax.awesomedaxsmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Anna Berkovitch on 17/11/2015.
 */
public class MovieContentProvider extends ContentProvider {
    private MyDbHelper dbHelper;
    private static final UriMatcher uriMatcher;
    private static final int MOVIES = 58;
    private static final int MOVIES_ID = 46;
    private static final int MOVIES_TITLE = 29;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieEntry.TABLE_NAME, MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIES_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieEntry.TABLE_NAME + "/*", MOVIES_TITLE);

    }

    private static SQLiteDatabase db;
    private static ContentResolver cr;


    @Override
    public boolean onCreate() {
        dbHelper = new MyDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cr = getContext().getContentResolver();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                break;
            case MOVIES_ID:
                queryBuilder.appendWhere(MovieContract.MovieEntry.TMDB_ID + "=" + uri.getLastPathSegment());
                break;
            case MOVIES_TITLE:
                queryBuilder.appendWhere((MovieContract.MovieEntry.MOVIE_TITLE_COLUMN + " LIKE '%" + uri.getLastPathSegment() + "%'"));
                break;
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = MovieContract.MovieEntry.MOVIE_TITLE_COLUMN;
        }

        Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(cr, uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                return MovieContract.CONTENT_TYPE;
            case MOVIES_ID:
                return MovieContract.CONTENT_ITEM_TYPE;
            case MOVIES_TITLE:
                return MovieContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri insertedItemUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, rowId);
            cr.notifyChange(insertedItemUri, null);
            return insertedItemUri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletedItemsCount;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                deletedItemsCount = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                deletedItemsCount = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.TMDB_ID + "=" +
                                uri.getLastPathSegment() +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case MOVIES_TITLE:
                deletedItemsCount = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.MOVIE_TITLE_COLUMN +
                                " LIKE '%" + uri.getLastPathSegment() + "%'" +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        cr.notifyChange(uri, null);
        return deletedItemsCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updatedItemsCount;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                updatedItemsCount = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIES_ID:
                updatedItemsCount = db.update(MovieContract.MovieEntry.TABLE_NAME, values, MovieContract.MovieEntry.TMDB_ID + "=" +
                                uri.getLastPathSegment() +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case MOVIES_TITLE:
                updatedItemsCount = db.update(MovieContract.MovieEntry.TABLE_NAME, values, MovieContract.MovieEntry.MOVIE_TITLE_COLUMN +
                                " LIKE '%" + uri.getLastPathSegment() + "%'" +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        cr.notifyChange(uri, null);
        return updatedItemsCount;
    }
}
