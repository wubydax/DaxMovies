package com.wubydax.awesomedaxsmovies.api;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenreResponse {

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();

    /**
     *
     * @return
     * The genres
     */
    public List<Genre> getGenres() {
        return genres;
    }


    public class Genre {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

}
