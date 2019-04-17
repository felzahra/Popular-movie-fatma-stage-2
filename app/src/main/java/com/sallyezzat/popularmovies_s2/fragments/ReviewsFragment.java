package com.sallyezzat.popularmovies_s2.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sallyezzat.popularmovies_s2.MovieDetailActivity;
import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.adapters.ReviewAdapter;
import com.sallyezzat.popularmovies_s2.asynctasks.ReviewsTask;
import com.sallyezzat.popularmovies_s2.domains.MovieReview;

import java.util.ArrayList;
import java.util.List;


public class ReviewsFragment extends Fragment {
    private final List<MovieReview> mReviews = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;
    private Integer mMovieId;

    private static final String MOVIEDB_API_KEY = "e4f5a649dfc6f56a0d9a7d8deacf3608";

    private GridView reviewGridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        mMovieId = activity != null ? activity.getMovieId() : null;
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        reviewGridview = view.findViewById(R.id.reviewgridview);
        mReviewAdapter = new ReviewAdapter(getActivity(), mReviews, reviewGridview);
        if (savedInstanceState != null) { //check state bundle to load from if not null
            ArrayList<MovieReview> items = savedInstanceState.getParcelableArrayList("reviewAdapter");
            mReviewAdapter.add(items);
            reviewGridview.setAdapter(mReviewAdapter);
        } else
            runReviewTask(mMovieId.toString()); //run the task that will fetch reviews


        return view;

    }


    private void runReviewTask(String id) {
        ReviewsTask reviewTask = new ReviewsTask(getActivity(), MOVIEDB_API_KEY, reviewGridview, mReviewAdapter);
        reviewTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("reviewAdapter", (ArrayList<? extends Parcelable>) mReviewAdapter.getmReviews()); //save reviews before destroy

    }

}
