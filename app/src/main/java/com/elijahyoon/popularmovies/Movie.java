package com.elijahyoon.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by elijahyoon on 8/24/15.
 */
public class Movie implements Parcelable {

    private String title;
    private String posterURL;
    private String vote;
    private String releaseDate;
    private String plot;

    public Movie(String title, String posterURL, String vote, String releaseDate, String plot) {
        this.title = title;
        this.posterURL = posterURL;
        this.vote = vote;
        this.releaseDate = releaseDate;
        this.plot = plot;
    }

    private Movie(Parcel in) {
        title = in.readString();
        posterURL = in.readString();
        vote = in.readString();
        releaseDate = in.readString();
        plot = in.readString();
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

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterURL);
        dest.writeString(vote);
        dest.writeString(releaseDate);
        dest.writeString(plot);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
