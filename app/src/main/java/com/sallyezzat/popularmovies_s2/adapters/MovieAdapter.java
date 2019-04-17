package com.sallyezzat.popularmovies_s2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sallyezzat.popularmovies_s2.R;
import com.sallyezzat.popularmovies_s2.domains.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahmed on 12/18/2017.
 */

public class MovieAdapter extends BaseAdapter {

    private final List<Movie> mMovies = new ArrayList<>();
    private final Context mContext;


    public MovieAdapter(Context c, List<Movie> moviesList) {
        mContext = c;
        getmMovies().addAll(moviesList);
    }

    public int getCount() {
        return getmMovies().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            //    imageView.setLayoutParams(new GridView.LayoutParams(350, 300)); //width, height
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); //crop if necessary
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }


        if (getmMovies().get(position).getPosterPath() != null)
            Picasso.with(mContext).load(getmMovies().get(position).getPosterPath()).placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_placeholder_error).into(imageView);
        else {

            Bitmap bmp = BitmapFactory.decodeByteArray(getmMovies().get(position).getPosterImg(), 0, getmMovies().get(position).getPosterImg().length);

            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                    150, false));


        }

        return imageView;
    }


    public void add(List<Movie> moviess) {
        getmMovies().clear();
        getmMovies().addAll(moviess);
        notifyDataSetChanged();
    }

    public List<Movie> getmMovies() {
        return mMovies;
    }

}
