package com.sallyezzat.popularmovies_s2.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.GridView;

import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.adapters.MovieAdapter;
import com.sallyezzat.popularmovies_s2.data.MovieContract;
import com.sallyezzat.popularmovies_s2.domains.Movie;

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
 * Created by Soly on 12/22/2017.
 */

public class MoviesTask extends AsyncTask<String, String, List<Movie>> { //class for movies asyncTask
    private final ProgressDialog dialog;
    private List<Movie> mMovies = new ArrayList<>();
    private final String movieDb_api;
    private final MovieAdapter mMovieAdapter;
    private final GridView gridView;
    private final Context context;

    public MoviesTask(Context c, GridView gridView, MovieAdapter mMovieAdapter) {
        dialog = new ProgressDialog(c);
        context = c;
        movieDb_api = com.sallyezzat.popularmovies_s2.MainGridActivity.MOVIEDB_API_KEY;
        this.gridView = gridView;
        this.mMovieAdapter = mMovieAdapter;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(context.getString(R.string.wait_msg));
        dialog.setIndeterminate(true);
        dialog.show();

    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        // retrieved movies information as json from the api
        String movieJsonStr;

        try {

            String sortPart="";
            switch (params[0]) {
                case "P":
                    sortPart = context.getString(R.string.sort_part_popular); //popular
                    break;
                case "R":
                    sortPart = context.getString(R.string.sort_part_rated); //top rated
                    break;
                case "F":
                    sortPart = null; //db case
                    break;
                default:
                    sortPart = context.getString(R.string.sort_part_default); //default
                    break;
            }
            if (sortPart != null) {

                String urlPath = context.getString(R.string.url_1) + sortPart + context.getString(R.string.url_2) + movieDb_api;  //to be like that http://api.themoviedb.org/3/movie/popular?api_key = MOVIEDB_API_KEY
                URL url = new URL(urlPath);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                publishProgress(context.getString(R.string.read_buffer_msg));
                movieJsonStr = StreamToString(in);
                mMovies = getMovieDataFromJson(movieJsonStr);
                in.close();
            } else {//db case
                Cursor allFromDb = getAllFromDb();
                int count = allFromDb.getCount();
                mMovies = cursorToArray(allFromDb);

            }

        } catch (Exception e) {
            publishProgress(context.getString(R.string.error_connect_api_msg));
        }

        return mMovies;
    }


    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        //mMovieAdapter = new MovieAdapter(context, mMovies);
        mMovieAdapter.add(movies);
        gridView.setAdapter(mMovieAdapter);
        dialog.dismiss();
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    private List<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        mMovies.clear(); //empty movies list
        Movie m;
        for (int i = 0; i < movieArray.length(); i++) {
            m = new Movie();
            JSONObject movie = movieArray.getJSONObject(i);
            m.setPosterPath(context.getString(R.string.img_root) + movie.getString("poster_path"));
            m.setId(movie.getInt("id"));
            m.setOverview(movie.getString("overview"));
            m.setTitle(movie.getString("title"));
            m.setReleaseDate(movie.getString("release_date"));
            m.setVoteAverage(Double.valueOf(movie.getString("vote_average")));

            mMovies.add(m);
        }
        return mMovies;
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

    private Cursor getAllFromDb() {
        return context.getContentResolver().query(MovieContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MoviesEntry.COLUMN_DATE);
    }

    private ArrayList<Movie> cursorToArray(Cursor mCursor) {
        ArrayList<Movie> mArrayList = new ArrayList<>();
        mCursor.moveToFirst();
        Movie m;
        while (!mCursor.isAfterLast()) {
            m = new Movie();

            m.setId((mCursor.getInt(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_MOVIE_ID))));
            m.setTitle(mCursor.getString(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_TITLE)));
            m.setOverview(mCursor.getString(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_OVERVIEW)));
            m.setReleaseDate(mCursor.getString(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE)));
            m.setVoteAverage(mCursor.getDouble(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
            m.setPosterImg(mCursor.getBlob(mCursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_POSTER_IMG)));
            mArrayList.add(m);

            mCursor.moveToNext();

        }
        return mArrayList;
    }

 /*   public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }*/
}

