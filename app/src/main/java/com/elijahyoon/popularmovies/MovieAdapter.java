package com.elijahyoon.popularmovies;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by elijahyoon on 8/24/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private String POSTER_SIZE = "w342";

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.movieTitle.setText(movie.getTitle());
        Picasso.with(getContext()).load(POSTER_BASE_URL+POSTER_SIZE+movie.getPoster()).into(viewHolder.moviePoster);

        return convertView;
    }

    private static class ViewHolder {
        private TextView movieTitle;
        private ImageView moviePoster;

        public ViewHolder(View view) {
            movieTitle = (TextView) view.findViewById(R.id.grid_item_text);
            moviePoster = (ImageView) view.findViewById(R.id.grid_item_icon);
        }
    }
}
