package com.wubydax.awesomedaxsmovies.utils;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wubydax.awesomedaxsmovies.api.JsonResponse;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private JsonResponse.Results movieData;
    private Bitmap posterBitmap;
    private List<JsonResponse.Results> movieDataList;
    private boolean isSearch;
    private boolean isLoading;
    private boolean isRefreshMenu;
    private int pageNumber, totalPagesNumber;
    private HashMap<Integer, String> hashMapGenres;
    private String mQuery;


    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setDetailsData(JsonResponse.Results movieData, Bitmap poster) {
        this.movieData = movieData;
        posterBitmap = poster;

    }

    public void setMovieData(List<JsonResponse.Results> movieDataList) {
        this.movieDataList = movieDataList;

    }


    public void setSearchBoolean(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public List<JsonResponse.Results> getMovieDataList() {
        return movieDataList;
    }


    public JsonResponse.Results getMovieData() {
        return movieData;
    }


    public Bitmap getBitmap() {
        return posterBitmap;
    }

    public boolean getSearchStatus() {
        return isSearch;
    }


    public int getPageNumber() {
        if (pageNumber == 0) {
            return 1;
        }
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public int getTotalPagesNumber() {
        return totalPagesNumber;
    }

    public void setTotalPagesNumber(int totalPagesNumber) {
        this.totalPagesNumber = totalPagesNumber;
    }

    public HashMap<Integer, String> getHashMapGenres() {
        return hashMapGenres;
    }

    public void setHashMapGenres(HashMap<Integer, String> hashMapGenres) {
        this.hashMapGenres = hashMapGenres;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public String getQuery() {
        if (mQuery == null) {
            return "";
        }
        return mQuery;
    }

    public void setQuery(String query) {
        this.mQuery = query;
    }

    public boolean isRefreshMenu() {
        return isRefreshMenu;
    }

    public void setIsRefreshMenu(boolean isRefreshMenu) {
        this.isRefreshMenu = isRefreshMenu;
    }
}
