package com.sallyezzat.popularmovies_s2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.domains.MovieReview;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MEASURED_SIZE_MASK;

/**
 * Created by Soly on 12/25/2017.
 */

public class ReviewAdapter extends BaseAdapter {
    private final List<MovieReview> mReviews = new ArrayList<>();
    private final Context mContext;
    GridView reviewGridview;


    public ReviewAdapter(Context c, List<MovieReview> ReviewsList, GridView grid) {
        mContext = c;
        reviewGridview = grid;
        mReviews.addAll(ReviewsList);

    }

    public int getCount() {
        return getmReviews().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView AutherTextView;
        TextView ReviewTextView;
        if (convertView == null) { //bind item page
            //Inflate the XML based Layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.review_item, parent, false);
            }

        }
        ReviewTextView = (TextView) convertView.findViewById(R.id.review_text);
        AutherTextView = (TextView) convertView.findViewById(R.id.review_auther);


        ReviewTextView.setText(mReviews.get(position).getContent());
        AutherTextView.setText(mContext.getString(R.string.review_by) + mReviews.get(position).getAuthor());

        return convertView;
    }

    public void add(List<MovieReview> Reviews) {
        getmReviews().clear();
        getmReviews().addAll(Reviews);
        notifyDataSetChanged();
        adjustView();
    }

    private void adjustView() {
        reviewGridview.setAdapter(this);
        reviewGridview.post(new Runnable() {
            @Override
            public void run() {

                int expandSpec = View.MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                        View.MeasureSpec.AT_MOST);
                reviewGridview.measure(700, expandSpec);
                int w = reviewGridview.getMeasuredWidth();
                int h = reviewGridview.getMeasuredHeight();
                ViewGroup.LayoutParams params = reviewGridview.getLayoutParams();
                params.height = h ;
                params.width = w;
                reviewGridview.setLayoutParams(params);

            }
        });

    }

    public List<MovieReview> getmReviews() {
        return mReviews;
    }

}