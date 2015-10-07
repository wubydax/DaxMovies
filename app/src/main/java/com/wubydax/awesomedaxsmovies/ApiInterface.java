package com.wubydax.awesomedaxsmovies;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Anna Berkovitch on 04/10/2015.
 */
public interface ApiInterface {
    @GET("/3/discover/movie")
    Call<JsonResponse> getDataList(@Query("primary_release_date.gte") String dateFrom,
                            @Query("primary_release_date.lte") String dateTo,
                            @Query("language") String language,
                            @Query("sort_by") String sortBy,
                            @Query("api_key") String apiKey,
                            @Query("page") String pageNumber);

    @GET("/3/search/movie")
    Call<JsonResponse> getSearchDataList(@Query("query") String query,
                                         @Query("api_key") String apiKey);
}
