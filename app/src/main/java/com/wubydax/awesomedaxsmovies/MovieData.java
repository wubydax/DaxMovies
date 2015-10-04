package com.wubydax.awesomedaxsmovies;

/**
 * Created by Anna Berkovitch on 03/10/2015.
 */
public class MovieData {
    private String mId, mTitle, mDate, mSynopsis, mPosterPath, mVoteAverage, mVoteCount;

    public void setId(String id){
        mId = id;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setDate(String date){
        mDate = date;
    }

    public void setSynopsis(String synopsis){
        mSynopsis = synopsis;
    }

    public void setPosterPath(String posterPath){
        mPosterPath = posterPath;
    }

    public void setVoteAverage(String voteAverage){
        mVoteAverage = voteAverage;
    }

    public void setVoteCount(String voteCount){
        mVoteCount = voteCount;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getId(){
        return mId;
    }

    public String getDate(){
        return mDate;
    }

    public String getSynopsis(){
        return mSynopsis;
    }

    public String getPosterPath(){
        return mPosterPath;
    }

    public String getVoteAverage(){
        return mVoteAverage;
    }

    public String getVoteCount(){
        return mVoteCount;
    }
}
