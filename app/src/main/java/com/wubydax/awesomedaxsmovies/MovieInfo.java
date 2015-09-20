package com.wubydax.awesomedaxsmovies;

/**
 * Created by Anna Berkovitch on 20/09/2015.
 */
public class MovieInfo {
    private String mTitle, mSynopsis, mRating, mReleaseDate, mUrl;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSynopsis(String synopsis) {
        mSynopsis = synopsis;
    }

    public void setDate(String date) {
        mReleaseDate = date;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
