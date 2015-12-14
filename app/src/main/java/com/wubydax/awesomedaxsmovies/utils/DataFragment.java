package com.wubydax.awesomedaxsmovies.utils;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wubydax.awesomedaxsmovies.api.JsonResponse;
import com.wubydax.awesomedaxsmovies.api.Reviews;
import com.wubydax.awesomedaxsmovies.api.Videos;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private JsonResponse.Results movieData;
    private Bitmap posterBitmap;
    private List<JsonResponse.Results> movieDataList;
    private boolean isSearch, isLoading, isRefreshMenu, isFavourites, isExpanded;
    private List<Reviews.Result> reviewList;
    private List<Videos.Result> trailersList;
    private int pageNumber, totalPagesNumber, mScrollPosition;
    private long id, jsonMovieId;
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

    public boolean isFavourites() {
        return isFavourites;
    }

    public void setIsFavourites(boolean isFavourites) {
        this.isFavourites = isFavourites;
    }

    public void setDetailsData(JsonResponse.Results movieData, Bitmap poster) {
        this.movieData = movieData;
        posterBitmap = poster;

    }

    public void setCursorDetailsData(Long id, Bitmap poster) {
        this.id = id;
        posterBitmap = poster;
    }

    public void setMovieData(List<JsonResponse.Results> movieDataList) {
        this.movieDataList = movieDataList;

    }

    public long getMovieId() {
        if(id > 0) {
            return id;
        }
        return 0;
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

    public int getmScrollPosition() {
        return mScrollPosition;
    }

    public void setmScrollPosition(int mScrollPosition) {
        this.mScrollPosition = mScrollPosition;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public List<Reviews.Result> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Reviews.Result> reviewList) {
        this.reviewList = reviewList;
    }

    public List<Videos.Result> getTrailersList() {
        return trailersList;
    }

    public void setTrailersList(List<Videos.Result> trailersList) {
        this.trailersList = trailersList;
    }

    public long getJsonMovieId() {
        return jsonMovieId;
    }

    public void setJsonMovieId(long jsonMovieId) {
        this.jsonMovieId = jsonMovieId;
    }
}
