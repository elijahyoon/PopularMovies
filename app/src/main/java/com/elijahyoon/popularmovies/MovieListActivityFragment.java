package com.elijahyoon.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment {

    private ArrayAdapter<Movie> mAdapter;
    private SharedPreferences pref;

    public MovieListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

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
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
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
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        @Override
        protected List<Movie> doInBackground(String... params) {
            String sortBy = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            String apiKey = "";

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieData = null;

            try {
                // Construct the URL for the movie db query
                String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                String SORT_BY_PARAM = "sort_by";
                String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortBy)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to movie db, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieData = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(movieData);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private List<Movie> getMoviesFromJson(String movieData)  throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String BACKDROP = "poster_path";
            final String TITLE = "title";
            final String RESULTS = "results";
            final String VOTE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String PLOT = "overview";

            JSONObject movieJSON = new JSONObject(movieData);
            JSONArray movieArray = movieJSON.getJSONArray(RESULTS);

            List<Movie> resultMovies = new ArrayList<>();
            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the current movie
                JSONObject currentMovie = movieArray.getJSONObject(i);

                //Create movie object
                Movie movieObject = new Movie(currentMovie.getString(TITLE), currentMovie.getString(BACKDROP),
                        currentMovie.getString(VOTE), currentMovie.getString(RELEASE_DATE), currentMovie.getString(PLOT));

                resultMovies.add(movieObject);
            }

            return resultMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                mAdapter.clear();
                mAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }
        }


    }

}
