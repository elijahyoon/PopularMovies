package com.elijahyoon.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment {

    private static final String MOVIE_LIST_KEY = "movieList";
    private ArrayAdapter<Movie> mAdapter;
    private ArrayList<Movie> movies;

    public MovieListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
            movies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            mAdapter = new MovieAdapter(getActivity(), movies);
        } else {
            mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
            updateMovies();
        }

        View rootview = inflater.inflate(R.layout.fragment_movie_list, container, false);

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview_movies);
        gridview.setAdapter(mAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mAdapter.getItem(position);
                Intent detailsIntent = new Intent(getActivity(), MovieDetailActivity.class).putExtra("movie", movie);
                startActivity(detailsIntent);
            }
        });

        return rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState );
         outState.putParcelableArrayList(MOVIE_LIST_KEY, movies);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask(mAdapter, getActivity());
        try {
            movies = (ArrayList<Movie>) movieTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
