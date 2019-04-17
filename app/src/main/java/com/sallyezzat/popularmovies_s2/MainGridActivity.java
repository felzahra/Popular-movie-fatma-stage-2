package com.sallyezzat.popularmovies_s2;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sallyezzat.popularmovies_s2.adapters.MovieAdapter;
import com.sallyezzat.popularmovies_s2.asynctasks.MoviesTask;
import com.sallyezzat.popularmovies_s2.domains.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainGridActivity extends Activity {
    private MovieAdapter mMovieAdapter;
    public static final String MOVIEDB_API_KEY = "e4f5a649dfc6f56a0d9a7d8deacf3608";
    private final List<Movie> mMovies = new ArrayList<>();
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        gridview = findViewById(R.id.gridview);
        mMovieAdapter = new MovieAdapter(this, mMovies);
        if (savedInstanceState != null) {
            ArrayList<Movie> items = savedInstanceState.getParcelableArrayList("myAdapter");
            mMovieAdapter.add(items);
            gridview.setAdapter(mMovieAdapter);

        } else {
            String sortType = "N";
            runMovieTask(sortType); //run the task that will fetch movies
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {//grid items click listener
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                openDetailActivity(position); //opens MoveDetailActivity to show details of selected movie
            }
        });


    }

    private void openDetailActivity(int position) {
        Intent myIntent = new Intent(MainGridActivity.this, MovieDetailActivity.class);  //intent to pass movie data when click on poster image
        myIntent.putExtra("movie", mMovieAdapter.getmMovies().get(position)); //pass the whole object of movie so implemented Parcable on class
        MainGridActivity.this.startActivity(myIntent); //open the detail Activity

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.popular: {

                runMovieTask("P"); //sort by popularity

                return true;


            }
            case R.id.rate: {
                runMovieTask("R"); //sort by rated
                return true;
            }
            case R.id.favorite: {
                runMovieTask("F"); //view favorite movies from DB
                return true;


            }
            default:
                return false;

        }

    }


    private void runMovieTask(String sortType) { //run the async task to get movies according to parameter
        if (isApiKeyFound(sortType))
            if (isConnected(sortType)) { //check if connected to internet
                MoviesTask movieTask = new MoviesTask(MainGridActivity.this, gridview, mMovieAdapter);
                movieTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sortType);
            }
    }


    private boolean isConnected(String sortTp) {//to alert if there is no internet connection instead of the not understood blank page
        NetworkInfo netInfo = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)) != null ? ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() : null;

        if ((netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) && !sortTp.equals("F")) {
            Toast.makeText(MainGridActivity.this, R.string.no_connection_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isApiKeyFound(String sortType) { //to alert if API key not set

        if (MOVIEDB_API_KEY.equals("") && !sortType.equals("F")) {
            Toast.makeText(MainGridActivity.this, R.string.api_ky_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("myAdapter", (ArrayList<? extends Parcelable>) mMovieAdapter.getmMovies());
        //  outState.putInt("gridPos",gridview.getFirstVisiblePosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // gridview.smoothScrollToPosition(savedInstanceState.getInt("gridPos"));
    }

}









