package com.wubydax.awesomedaxsmovies;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private Results movieData;
    private Bitmap posterBitmap;
    private List<Results> movieDataList;
    private List<String> ids;
    private boolean isSearch;
    private int pageNumber;

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    private boolean isLoading;

    public String getQuery() {
        if (mQuery == null) {
            return "";
        }
        return mQuery;
    }

    public void setQuery(String query) {
        this.mQuery = query;
    }

    private String mQuery;


    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setDetailsData(Results movieData, Bitmap poster) {
        this.movieData = movieData;
        posterBitmap = poster;

    }

    public void setMovieData(List<Results> movieDataList) {
        this.movieDataList = movieDataList;

    }

    public void setIdsData(List<String> ids) {
        this.ids = ids;
    }

    public void setSearchBoolean(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public List<Results> getMovieDataList() {
        return movieDataList;
    }

    public List<String> getIdsList() {
        return ids;
    }

    public Results getMovieData() {
        return movieData;
    }


    public Bitmap getBitmap() {
        return posterBitmap;
    }

    public boolean getSearchStatus() {
        return isSearch;
    }


    public int getPageNumber() {
        if(pageNumber==0){
            return 1;
        }
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
