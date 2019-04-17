package com.sallyezzat.popularmovies_s2.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.GridView;

import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.adapters.ReviewAdapter;
import com.sallyezzat.popularmovies_s2.domains.MovieReview;

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
 * Created by Soly on 12/25/2017.
 */


public class ReviewsTask extends AsyncTask<String, String, List<MovieReview>> { //inner class for asyncTask
    // private final String LOG_TAG = MoviesTask.class.getSimpleName();
    private final ProgressDialog dialog;
    private List<MovieReview> mReviews = new ArrayList<>();
    private final String movieDb_api;
    private final ReviewAdapter mReviewAdapter;
    private final GridView gridView;
    private final Context context;

    public ReviewsTask(Context c, String api_key, GridView gridView, ReviewAdapter mReviewAdapter) {
        dialog = new ProgressDialog(c);
        context = c;
        movieDb_api = api_key;
        this.gridView = gridView;
        this.mReviewAdapter = mReviewAdapter;
    }


    @Override
    protected List<MovieReview> doInBackground(String... params) {
        // retrieved movies information as json from the api
        String movieJsonStr;

        try {

            String id = params[0];

            String urlReviewsPath = context.getString(R.string.trailer_url_1) + id + context.getString(R.string.review_url_2) + movieDb_api;  //to be like that http://api.themoviedb.org/3/movie/reviews?api_key = MOVIEDB_API_KEY

            URL url = new URL(urlReviewsPath);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            publishProgress(context.getString(R.string.read_buffer_msg));
            movieJsonStr = StreamToString(in);
            mReviews = getReviewsDataFromJson(movieJsonStr);

            in.close();
            // }
        } catch (Exception e) {
            publishProgress(context.getString(R.string.error_connect_api_msg));
        }

        return mReviews;
    }


    @Override
    protected void onPostExecute(List<MovieReview> reviews) {

        super.onPostExecute(reviews);

        mReviewAdapter.add(reviews);

        gridView.setAdapter(mReviewAdapter);

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

    private List<MovieReview> getReviewsDataFromJson(String reviewJsonStr)
            throws JSONException {
        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");
        mReviews.clear(); //empty reviews list
        MovieReview r;
        for (int i = 0; i < reviewArray.length(); i++) {
            r = new MovieReview();
            JSONObject review = reviewArray.getJSONObject(i);
            r.setId(review.getString("id"));
            r.setAuthor(review.getString("author"));
            r.setContent(review.getString("content"));

            mReviews.add(r);
        }
        return mReviews;
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
}
