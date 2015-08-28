package com.elijahyoon.popularmovies;

/**
 * Created by elijahyoon on 8/24/15.
 */
public class Movie {

    private String title;
    private String posterURL;
    private int rating;

    public Movie(String title, String poster) {
        this.title = title;
        this.posterURL = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return posterURL;
    }

    public void setPoster(String poster) {
        this.posterURL = poster;
    }
}
