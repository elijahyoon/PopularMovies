package com.elijahyoon.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by elijahyoon on 8/25/15.
 */
public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MovieSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MovieDetailFragment extends Fragment {

        private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        private Movie movie;

        public MovieDetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movie")) {
                String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                String POSTER_SIZE = "w780";
                movie = intent.getParcelableExtra("movie");
                ((TextView) rootView.findViewById(R.id.details_title)).setText(movie.getTitle());
                Picasso.with(getActivity()).
                        load(POSTER_BASE_URL+POSTER_SIZE+movie.getPoster()).
                        error(R.drawable.camera).
                        into((ImageView) rootView.findViewById(R.id.poster_image));
                ((TextView) rootView.findViewById(R.id.body_plot_synopsis)).setText(movie.getPlot());
                ((TextView) rootView.findViewById(R.id.body_release_date)).setText(movie.getReleaseDate());
                ((TextView) rootView.findViewById(R.id.body_rating)).setText(movie.getVote());
            }

            return rootView;
        }
    }
}
