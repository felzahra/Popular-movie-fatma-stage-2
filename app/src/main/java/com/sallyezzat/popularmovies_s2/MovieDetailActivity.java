package com.sallyezzat.popularmovies_s2;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.sallyezzat.popularmovies_s2.data.MovieContract;
import com.sallyezzat.popularmovies_s2.domains.Movie;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class MovieDetailActivity extends FragmentActivity  {
    private ToggleButton toggleButton;
    private TextView title;
    private TextView date;
    private TextView vote;
    private TextView plotSynopsis;
    private Movie m;
    private ImageView image;
    private String movieId;
    private byte[] posterIm;
    private String trailerPathString;
    private TextView share;
    private Integer trailersNo;
    private Bitmap imageBmp;
    private boolean favorite;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        toggleButton = findViewById(R.id.myToggleButton);
        share = findViewById(R.id.trailer_share);
        image = findViewById(R.id.im_imageView);
        mScrollView = findViewById(R.id.scroll);
        if (savedInstanceState != null) {
            imageBmp = (Bitmap) savedInstanceState.getParcelable("imageBmp");
            image.setImageBitmap(imageBmp);
            favorite = savedInstanceState.getBoolean("favorite");
            //other text components saved in page design by using android:freezesText="true"
        } else {
            Intent intent = getIntent();
            Parcelable movie = intent.getParcelableExtra("movie");//get the movie object

            m = (Movie) movie;


            title = findViewById(R.id.tv_title);
            date = findViewById(R.id.tv_date);
            vote = findViewById(R.id.tv_vote_average);
            plotSynopsis = findViewById(R.id.tv_plot_synopsis);
            title.setText(m.getTitle());
            date.setText(m.getReleaseDate());
            vote.setText(String.valueOf(m.getVoteAverage()));
            plotSynopsis.setText(m.getOverview());

            if (m.getPosterPath() != null)//
                Picasso.with(this).load(m.getPosterPath()).placeholder(R.drawable.movie_placeholder)
                        .error(R.drawable.movie_placeholder_error).into(image);
            else {//db case
                posterIm = m.getPosterImg();
                if (posterIm != null && posterIm.length > 0) {


                    Bitmap bmp = BitmapFactory.decodeByteArray(posterIm, 0, posterIm.length);
                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                            150, false));

                }
            }

            //favourite star button
            favorite = isFavorite(m.getId());
            focusTopPage(); //to solve focus on trailer fragment when page 1st load
        }

        toggleButton.setChecked(favorite); //determine favorite case
        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), favorite ? R.drawable.star_on : R.drawable.star_off));

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Uri uri = addToFavoritelist(m);
                    if (uri != null) {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_on));
                        // Toast.makeText(MovieDetailActivity.this, R.string.added_to_favorite_msg, Toast.LENGTH_LONG).show();
                        toast(R.string.added_to_favorite_msg);

                    } else return;
                } else {
                    removeFromFavoritelist(m.getId());
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_off));
                    toast(R.string.removed_from_favorite_msg);

                }
            }
        });

        getShare().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getTrailerPathString() != null) {

                    shareTrailer();

                }

            }
        });



    }

    private void focusTopPage() { //solve scroll to first fragment
        ScrollView scroll = findViewById(R.id.scroll);
        scroll.setFocusableInTouchMode(true);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }

    private void shareTrailer() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getTrailerPathString());
        if (shareIntent.resolveActivity(getPackageManager()) != null) //if a suitable app found
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_trailer)));
        else  Toast.makeText(this, R.string.no_text_app_msg, Toast.LENGTH_LONG).show();
    }

    private void toast(int msg) {
        Toast toast = Toast.makeText(MovieDetailActivity.this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void removeFromFavoritelist(Integer movieId) {
        Uri uri = MovieContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(movieId.toString()).build();
        final int delete = getContentResolver().delete(uri, null, null);
    }


    private Uri addToFavoritelist(Movie m) {

        Uri uri = insertToDB();
        return uri;
    }

    private Uri insertToDB() {
        //  a ContentValues instance to pass the values to the insert query
        ContentValues cv = new ContentValues();

        //if list only id and image will be needed
        cv.put(MovieContract.MoviesEntry.COLUMN_MOVIE_ID, m.getId());
        cv.put(MovieContract.MoviesEntry.COLUMN_TITLE, m.getTitle());
        cv.put(MovieContract.MoviesEntry.COLUMN_OVERVIEW, m.getOverview());
        cv.put(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE, m.getReleaseDate());
        cv.put(MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE, m.getVoteAverage());
        cv.put(MovieContract.MoviesEntry.COLUMN_POSTER_PATH, m.getPosterPath());
        cv.put(MovieContract.MoviesEntry.COLUMN_POSTER_IMG, imgToByteArr(image));

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, cv);
        return uri;
    }

    private byte[] imgToByteArr(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }


    private boolean isFavorite(Integer id) {
//if a record found in the movies table , movie is a favorite
        Uri uri = MovieContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(id.toString()).build();


        Cursor c = getContentResolver().query(uri, null, null, null, null);

        if (c != null && c.getCount() > 0 && c.moveToFirst()) return true; //favorite=true
        else return false;  //favorite=false
    }


    public Integer getMovieId() {
        Intent intent = getIntent();
        Parcelable movie = intent.getParcelableExtra("movie");//get the movie object
        m = (Movie) movie;
        return m.getId();

    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }


    private String getTrailerPathString() {
        return trailerPathString;
    }

    public void setTrailerPathString(String trailerPathString) {
        this.trailerPathString = trailerPathString;
    }


    public TextView getShare() {
        return share;
    }

    public void setTrailersNo(int trailersNo) {
        this.trailersNo = trailersNo;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Make sure you save the current image resource
        imageBmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
        outState.putParcelable("imageBmp", imageBmp);
        // outState.putParcelable("movie",m);//get the movie
        outState.putBoolean("favorite", favorite);//togglebutton value
        outState.putIntArray("scrollPos",
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()}); //save scroll location

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("scrollPos");
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
        //      m= savedInstanceState.getParcelable("movie");

        imageBmp = (Bitmap) savedInstanceState.getParcelable("imageBmp");
        image.setImageBitmap(imageBmp);
        favorite = savedInstanceState.getBoolean("favorite");
        //other text components saved in page design by using android:freezesText="true"
    }

}
