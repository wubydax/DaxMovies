package com.wubydax.awesomedaxsmovies;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private MovieData movieData;
    private Bitmap posterBitmap;
    private List<MovieData> movieDataList;
    private List<String> ids;
    private boolean isSearch;


    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setDetailsData(MovieData movieData, Bitmap poster) {
        this.movieData =  movieData;
        posterBitmap = poster;

    }

    public void setMovieData(List<MovieData> movieDataList) {
        this.movieDataList = movieDataList;

    }

    public void setIdsData(List<String> ids){
        this.ids = ids;
    }

    public void setSearchBoolean(boolean isSearch){
        this.isSearch = isSearch;
    }

    public List<MovieData> getMovieDataList() {
        return movieDataList;
    }

    public List<String> getIdsList(){
        return ids;
    }

    public MovieData getMovieData() {
        return movieData;
    }



    public Bitmap getBitmap() {
        return posterBitmap;
    }

    public boolean getSearchStatus(){
        return isSearch;
    }


}
