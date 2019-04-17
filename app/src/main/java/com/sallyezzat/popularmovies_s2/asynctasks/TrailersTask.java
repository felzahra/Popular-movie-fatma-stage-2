package com.sallyezzat.popularmovies_s2.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.GridView;

import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.adapters.TrailerAdapter;
import com.sallyezzat.popularmovies_s2.domains.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Soly on 12/23/2017.
 */

public class TrailersTask extends AsyncTask<String, String, List<MovieTrailer>> { //asyncTask for trailers fetching

    private final ProgressDialog dialog;
    private List<MovieTrailer> mTrailers = new ArrayList<>();
    private final String movieDb_api;
    private final TrailerAdapter mTrailerAdapter;
    private final GridView gridView;
    private final Context context;
    private String id;

    public TrailersTask(Context c, GridView gridView, TrailerAdapter mTrailerAdapter) {
        dialog = new ProgressDialog(c);
        context = c;
        movieDb_api = com.sallyezzat.popularmovies_s2.fragments.TrailersFragment.MOVIEDB_API_KEY;
        this.gridView = gridView;
        this.mTrailerAdapter = mTrailerAdapter;
    }

    @Override
    protected List<MovieTrailer> doInBackground(String... params) {
        // retrieved movies information as json from the api
        String trailerJsonStr;

        try {

            id = params[0];

            String urlTrailerPath = context.getString(R.string.trailer_url_1) + id + context.getString(R.string.trailer_url_2) + movieDb_api;  //to be like that http://api.themoviedb.org/3/movie/id/videos?api_key = MOVIEDB_API_KEY
            URL url = new URL(urlTrailerPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            publishProgress(context.getString(R.string.read_buffer_msg));
            trailerJsonStr = StreamToString(in);

            mTrailers = getTrailersDataFromJson(trailerJsonStr);

            in.close();
            // }
        } catch (Exception e) {
            publishProgress(context.getString(R.string.error_connect_api_msg));
        }

        return mTrailers;
    }


    @Override
    protected void onPostExecute(List<MovieTrailer> trailers) {
        super.onPostExecute(trailers);
         mTrailerAdapter.add(trailers);
        dialog.dismiss();
    }


    @Override
    protected void onPreExecute() {
        dialog.setMessage(context.getString(R.string.wait_msg));
        dialog.setIndeterminate(true);
        dialog.show();

    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    private List<MovieTrailer> getTrailersDataFromJson(String movieJsonStr)
            throws JSONException {
        JSONObject trailerJson = new JSONObject(movieJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray("results");
        mTrailers.clear(); //empty movies list
        MovieTrailer t;
        for (int i = 0; i < trailerArray.length(); i++) {
            t = new MovieTrailer();
            JSONObject triler = trailerArray.getJSONObject(i);
            t.setKey(triler.getString("key"));
            //setPosterPath(context.getString(R.string.img_root) + movie.getString("poster_path"));
            t.setId(triler.getString("id"));
            t.setName(triler.getString("name"));
            t.setType(triler.getString("type"));

            mTrailers.add(t);
        }
        return mTrailers;
    }

    private String StreamToString(InputStream inputStream) { //convert the input stream to string
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder Text = new StringBuilder();
        try {
            while ((line = bReader.readLine()) != null) {
                Text.append(line);
            }
            inputStream.close();
        } catch (Exception ignored) {
        }
        return Text.toString();
    }


    public float dpToPixels(int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}



