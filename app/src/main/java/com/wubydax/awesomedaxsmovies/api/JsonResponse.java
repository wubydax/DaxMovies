package com.wubydax.awesomedaxsmovies.api;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonResponse {

    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private List<Results> results = new ArrayList<>();
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;

    /**
     * @return The page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return The results
     */
    public List<Results> getResults() {
        for(int i=0; i< results.size(); i++){
            Results result = results.get(i);
        }
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<Results> results) {
        this.results = results;
    }

    /**
     * @return The totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The total_pages
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return The totalResults
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * @param totalResults The total_results
     */
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public class Results {

        @SerializedName("adult")
        @Expose
        private boolean adult;
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = new ArrayList<Integer>();
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("original_language")
        @Expose
        private String originalLanguage;
        @SerializedName("original_title")
        @Expose
        private String originalTitle;
        @SerializedName("overview")
        @Expose
        private String overview;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        @SerializedName("popularity")
        @Expose
        private float popularity;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("video")
        @Expose
        private boolean video;
        @SerializedName("vote_average")
        @Expose
        private float voteAverage;
        @SerializedName("vote_count")
        @Expose
        private int voteCount;

        /**
         *
         * @return
         * The adult
         */
        public boolean isAdult() {
            return adult;
        }

        /**
         *
         * @param adult
         * The adult
         */
        public void setAdult(boolean adult) {
            this.adult = adult;
        }

        /**
         *
         * @return
         * The backdropPath
         */
        public String getBackdropPath() {
            return backdropPath;
        }

        /**
         *
         * @param backdropPath
         * The backdrop_path
         */
        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        /**
         *
         * @return
         * The genreIds
         */
        public List<Integer> getGenreIds() {
            return genreIds;
        }

        /**
         *
         * @param genreIds
         * The genre_ids
         */
        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        /**
         *
         * @return
         * The id
         */
        public int getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The originalLanguage
         */
        public String getOriginalLanguage() {
            return originalLanguage;
        }

        /**
         *
         * @param originalLanguage
         * The original_language
         */
        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        /**
         *
         * @return
         * The originalTitle
         */
        public String getOriginalTitle() {
            return originalTitle;
        }

        /**
         *
         * @param originalTitle
         * The original_title
         */
        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        /**
         *
         * @return
         * The overview
         */
        public String getOverview() {
            return overview;
        }

        /**
         *
         * @param overview
         * The overview
         */
        public void setOverview(String overview) {
            this.overview = overview;
        }

        /**
         *
         * @return
         * The releaseDate
         */
        public String getReleaseDate() {
            return releaseDate;
        }

        /**
         *
         * @param releaseDate
         * The release_date
         */
        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        /**
         *
         * @return
         * The posterPath
         */
        public String getPosterPath() {
            return posterPath;
        }

        /**
         *
         * @param posterPath
         * The poster_path
         */
        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        /**
         *
         * @return
         * The popularity
         */
        public float getPopularity() {
            return popularity;
        }

        /**
         *
         * @param popularity
         * The popularity
         */
        public void setPopularity(float popularity) {
            this.popularity = popularity;
        }

        /**
         *
         * @return
         * The title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @param title
         * The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         *
         * @return
         * The video
         */
        public boolean isVideo() {
            return video;
        }

        /**
         *
         * @param video
         * The video
         */
        public void setVideo(boolean video) {
            this.video = video;
        }

        /**
         *
         * @return
         * The voteAverage
         */
        public float getVoteAverage() {
            return voteAverage;
        }

        /**
         *
         * @param voteAverage
         * The vote_average
         */
        public void setVoteAverage(float voteAverage) {
            this.voteAverage = voteAverage;
        }

        /**
         *
         * @return
         * The voteCount
         */
        public int getVoteCount() {
            return voteCount;
        }

        /**
         *
         * @param voteCount
         * The vote_count
         */
        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }

    }
}