package com.wubydax.awesomedaxsmovies.api;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public interface ApiInterface {
    @GET("/3/movie/{sort}")
    Call<JsonResponse> getDataList(@Path("sort") String path,
                            @Query("api_key") String apiKey,
                            @Query("page") String pageNumber);

    @GET("/3/search/movie")
    Call<JsonResponse> getSearchDataList(@Query("query") String query,
                                         @Query("api_key") String apiKey,
                                         @Query("page") String pageNumber);

    @GET("/3/genre/movie/list")
    Call<GenreResponse> getGenreScheme(@Query("api_key") String apiKey);
}
