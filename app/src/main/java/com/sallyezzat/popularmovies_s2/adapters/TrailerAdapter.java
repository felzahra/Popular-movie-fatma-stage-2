package com.sallyezzat.popularmovies_s2.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sallyezzat.popularmovies_s2.MovieDetailActivity;
import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.domains.MovieTrailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soly on 12/23/2017.
 */

public class TrailerAdapter extends BaseAdapter {

    private final List<MovieTrailer> mTrailers = new ArrayList<>();
    private final Context mContext;
    private final GridView trailGridview;


    public TrailerAdapter(Context c, List<MovieTrailer> trailersList, GridView grid) {
        mContext = c;
        trailGridview = grid;
        mTrailers.addAll(trailersList);

    }

    public int getCount() {
        return getmTrailers().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView trailerImageView;
        TextView trailerTextView;
        if (convertView == null) { //bind item page
            //Inflate the XML based Layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.trailer_item, parent, false);
            }
        }
        trailerTextView = (TextView) convertView.findViewById(R.id.trailer_text);
        trailerTextView.setText(mTrailers.get(position).getName());
        if (position == 0) {

            MovieDetailActivity activity = (MovieDetailActivity) mContext;
            activity.setTrailerPathString("https://www.youtube.com/watch?v=" + mTrailers.get(position).getKey());
            activity.setTrailersNo(mTrailers.size());
            activity.getShare().setVisibility(View.VISIBLE);
        }


        return convertView;
    }

    public void add(List<MovieTrailer> trailerss) {
        getmTrailers().clear();
        getmTrailers().addAll(trailerss);
        notifyDataSetChanged(); //adjust dimensions
        //width
        adjustView(); //to handle GridView in ScrollView issue

    }

    private void adjustView() {
        trailGridview.setNumColumns(mTrailers.size());
        ViewGroup.LayoutParams lp = trailGridview.getLayoutParams();
        int minWidth = (int) dpToPixels(80);
        lp.width = minWidth + minWidth * mTrailers.size();
        trailGridview.setLayoutParams(lp);
        LinearLayout parent = (LinearLayout) trailGridview.getParent();
        ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
        layoutParams.width = (int) dpToPixels(85) + minWidth * mTrailers.size();
        parent.setLayoutParams(layoutParams);
        trailGridview.setAdapter(this);

        //height
        trailGridview.post(new Runnable() {
            @Override
            public void run() {
                int h = trailGridview.getMeasuredHeight();
                ViewGroup.LayoutParams params = trailGridview.getLayoutParams();
                params.height = h + 20;
                trailGridview.setLayoutParams(params);

            }
        });

    }

    public List<MovieTrailer> getmTrailers() {
        return mTrailers;
    }


    private float dpToPixels(int dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
