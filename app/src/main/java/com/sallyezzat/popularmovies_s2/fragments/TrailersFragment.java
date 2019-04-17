package com.sallyezzat.popularmovies_s2.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sallyezzat.popularmovies_s2.MovieDetailActivity;
import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.adapters.TrailerAdapter;
import com.sallyezzat.popularmovies_s2.asynctasks.TrailersTask;
import com.sallyezzat.popularmovies_s2.domains.MovieTrailer;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
//import android.support.v4.app.Fragment;

//getActivity().getApplicationContext()     give context in fragment from container activity

public class TrailersFragment extends Fragment {


    private TrailerAdapter mTrailerAdapter;
    public static final String MOVIEDB_API_KEY = "e4f5a649dfc6f56a0d9a7d8deacf3608";
    private List<MovieTrailer> mTrailers = new ArrayList<>();
    private GridView trailGridview;
    private Integer mMovieId;
    GridView reviewGridview;

    private String trailerPathString;


    public interface passTrailerEventListener {
        public void passTrailerEvent(String s);
    }

    passTrailerEventListener passTrailerEventListener;




    public TrailersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        mMovieId = activity.getMovieId();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailers, container, false);
        trailGridview = view.findViewById(R.id.trailgridview);

        mTrailerAdapter = new TrailerAdapter(getActivity(), mTrailers, trailGridview);
        if (savedInstanceState != null) { //load trailers from state bundle if not null
            mTrailers = savedInstanceState.getParcelableArrayList("trailerAdapter");
            mTrailerAdapter.add(mTrailers);


        } else
            runTrailerTask(mMovieId.toString()); //run the task that will fetch trailers runs fragment first


        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        // else
        trailGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                playTrailer(position);
                Log.d(TAG, "--> onItemClick listener..."); // call intent to play video

            }
        });

    }


    private void playTrailer(int position) {
        Log.d(TAG, "--> onItemClick call" + position); // call intent to play video
        trailerPathString = getString(R.string.youtube_video_path) + mTrailerAdapter.getmTrailers().get(position).getKey();
        Uri uri = Uri.parse(trailerPathString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)//check if there is a player
            startActivity(intent);
        else
            Toast.makeText(getActivity(), R.string.no_player_available_msg, Toast.LENGTH_LONG).show();



    }

    private void runTrailerTask(String id) {


        if (isApiKeyFound())
            if (isConnected()) { //check if connected to internet
                TrailersTask trailerTask = new TrailersTask(getActivity(), trailGridview, mTrailerAdapter);
                trailerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
            }

    }



    private boolean isConnected() {//to alert if there is no internet connection instead of the not understood blank page
        NetworkInfo netInfo = ((ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)) != null ? ((ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() : null;

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            //      Toast.makeText(MainGridActivity.this, R.string.no_connection_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isApiKeyFound() { //to alert if API key not set

        if (MOVIEDB_API_KEY.equals("")) {
            //   Toast.makeText(MainGridActivity.this, R.string.api_ky_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("trailerAdapter", (ArrayList<? extends Parcelable>) mTrailerAdapter.getmTrailers()); //save trailers before destroy

    }


}
