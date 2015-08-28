package com.elijahyoon.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

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
 * Created by elijahyoon on 8/28/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private ArrayAdapter<Movie> mAdapter;
    private Context mContext;
    private SharedPreferences pref;

    public FetchMoviesTask(ArrayAdapter<Movie> mAdapter, Context context) {
        this.mAdapter = mAdapter;
        this.mContext = context;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Override
    protected List<Movie> doInBackground(String... params) {
        String sortBy = pref.getString(mContext.getString(R.string.pref_sort_key), mContext.getString(R.string.pref_sort_popularity));
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
