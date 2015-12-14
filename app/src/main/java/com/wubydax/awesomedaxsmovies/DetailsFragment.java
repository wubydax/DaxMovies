package com.wubydax.awesomedaxsmovies;


import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wubydax.awesomedaxsmovies.api.ApiInterface;
import com.wubydax.awesomedaxsmovies.api.JsonResponse;
import com.wubydax.awesomedaxsmovies.api.Reviews;
import com.wubydax.awesomedaxsmovies.api.Videos;
import com.wubydax.awesomedaxsmovies.data.MovieContract;
import com.wubydax.awesomedaxsmovies.data.MyDbHelper;
import com.wubydax.awesomedaxsmovies.utils.DataFragment;
import com.wubydax.awesomedaxsmovies.utils.FragmentCallbackListener;
import com.wubydax.awesomedaxsmovies.utils.MyDialogFragment;
import com.wubydax.awesomedaxsmovies.utils.Utils;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private Bitmap mBg;
    private JsonResponse.Results movieData;
    private String mTitle, mDate, mSynopsis, mGenre, mRating;
    private FragmentCallbackListener mListener;
    private DataFragment dataFragment;
    private HashMap<Integer, String> genreHashMap;
    private ContentResolver cr;
    private Utils utils;
    private boolean isFavourite;
    private boolean isExpanded = false;
    private boolean isTwoPane;
    private Cursor cursor;
    private float mRatingDouble, mPopularity;
    private long movieId, jsonMovieId;
    LinearLayout reviewsContainer, trailersContainer;
    Button showMore;
    private List<Reviews.Result> reviewList;
    private List<Videos.Result> trailerList;
    private ProgressBar progressBar;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(boolean isTablet) {
        DetailsFragment thisFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isTwoPane", isTablet);
        thisFragment.setArguments(bundle);
        return thisFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sort = menu.findItem(R.id.action_sort);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem refresh = menu.findItem(R.id.refresh);
        MenuItem fav = menu.findItem(R.id.action_favourite);
        share.setVisible(true);
        fav.setVisible(true);
        if (isFavourite) {
            fav.getIcon().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        } else {
            fav.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        }
        if (!isTwoPane) {
            sort.setVisible(false);
            search.setVisible(false);
            refresh.setVisible(false);
        }
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_intent_string), mTitle, getIntentString()));
                startActivity(Intent.createChooser(intent, getActivity().getString(R.string.intent_picker_title)));
                break;
            case R.id.action_favourite:
                if (!isFavourite) {
                    insertIntoDb(item);
                } else {
                    removeFromDb(item);
                }

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertIntoDb(MenuItem item) {

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.TMDB_ID, movieId);
        cv.put(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN, mTitle);
        cv.put(MovieContract.MovieEntry.MOVIE_POPULARITY_COLUMN, mPopularity);
        cv.put(MovieContract.MovieEntry.MOVIE_GENRE_COLUMN, mGenre);
        cv.put(MovieContract.MovieEntry.MOVIE_POSTER_BITMAP_COLUMN, utils.getBytes(mBg));
        cv.put(MovieContract.MovieEntry.MOVIE_RATING_COLUMN, mRatingDouble);
        cv.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN, mDate);
        cv.put(MovieContract.MovieEntry.MOVIE_SYNOPSIS_COLUMN, mSynopsis);
        Uri uri = cr.insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        if (uri != null) {
            if (Long.parseLong(uri.getLastPathSegment()) == movieId) {
                item.getIcon().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                isFavourite = true;
                Toast.makeText(getActivity(), mTitle + getActivity().getResources().getString(R.string.added_success), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failure_adding_to_favourites) + mTitle, Toast.LENGTH_LONG).show();
                isFavourite = false;

            }
        }

    }

    private void removeFromDb(MenuItem item) {
        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);
        int removed = cr.delete(uri, null, null);
        if (removed > 0) {
            Toast.makeText(getActivity(), mTitle + getActivity().getResources().getString(R.string.removed_from_db), Toast.LENGTH_LONG).show();
            item.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            isFavourite = false;

        }

    }

    private boolean isFavourite() {
        MyDbHelper dbHelper = new MyDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        if (db != null) {
            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);
            Cursor c = cr.query(uri, null, null, null, MovieContract.MovieEntry.MOVIE_TITLE_COLUMN);
            if (c != null) {
                count = c.getCount();
                c.close();
            }
            return count > 0;
        } else
            return false;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isTwoPane = this.getArguments().getBoolean("isTwoPane");
        cr = getActivity().getContentResolver();
        utils = new Utils(getActivity());
        int width = Math.round(getResources().getDimension(R.dimen.blured_image_width));
        int height = Math.round(getResources().getDimension(R.dimen.blured_image_height));
        dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("data");
        boolean isFavourites = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort_by", "popularity").equals("favourites");
        jsonMovieId = dataFragment.getJsonMovieId();
        mBg = dataFragment.getBitmap();


        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView mTitleText = (TextView) rootView.findViewById(R.id.detailTitle);
        TextView mDateText = (TextView) rootView.findViewById(R.id.detailDate);
        TextView mRatingText = (TextView) rootView.findViewById(R.id.detailRating);
        TextView mSynopsisText = (TextView) rootView.findViewById(R.id.detailSynopsis);
        TextView mGenreText = (TextView) rootView.findViewById(R.id.detailGenreText);
        ImageView mPosterView = (ImageView) rootView.findViewById(R.id.detailPoster);
        ImageView mRatingStars = (ImageView) rootView.findViewById(R.id.ratingImage);
        reviewsContainer = (LinearLayout) rootView.findViewById(R.id.reviewsContainer);
        trailersContainer = (LinearLayout) rootView.findViewById(R.id.trailersContainer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        showMore = (Button) rootView.findViewById(R.id.buttonExpand);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    if (trailersContainer.getVisibility() == View.GONE) {
                        if (trailersContainer.getChildCount() == 0) {
                            if (jsonMovieId != movieId) {
                                fetchAddonData();
                            } else {
                                getSavedAddonData();
                            }
                        } else {
                            trailersContainer.setVisibility(View.VISIBLE);
                            reviewsContainer.setVisibility(View.VISIBLE);
                            isExpanded = true;
                        }

                    }
                } else {
                    trailersContainer.setVisibility(View.GONE);
                    reviewsContainer.setVisibility(View.GONE);
                    isExpanded = false;
                }
                showMore.setText(buttonText());
            }

        });


        if (isFavourites)

        {
            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, dataFragment.getMovieId());

            cursor = getActivity().getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.MOVIE_TITLE_COLUMN);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {

                        mTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN));
                        mDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN));
                        mSynopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_SYNOPSIS_COLUMN));
                        mRating = String.valueOf(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RATING_COLUMN)));
                        mRatingDouble = Float.parseFloat(mRating);
                        mGenre = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_GENRE_COLUMN));
                        movieId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.TMDB_ID));
                        mPopularity = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POPULARITY_COLUMN));
                        mBg = utils.getImage(cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_BITMAP_COLUMN)));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }

        } else

        {
            movieData = dataFragment.getMovieData();
            genreHashMap = dataFragment.getHashMapGenres();
            mTitle = movieData.getTitle();
            mDate = movieData.getReleaseDate();
            mRating = String.valueOf(movieData.getVoteAverage());
            mSynopsis = movieData.getOverview();
            List<Integer> genreIdList = movieData.getGenreIds();
            mRatingDouble = movieData.getVoteAverage();
            movieId = movieData.getId();
            mPopularity = movieData.getPopularity();
            mGenre = genreString(genreIdList);

        }

        isFavourite = isFavourite();
        isExpanded = jsonMovieId == movieId && dataFragment.isExpanded();

        if (isExpanded)

        {
            getSavedAddonData();
        } else

        {
            showMore.setText(getActivity().getResources().getString(R.string.show_more));
        }

        mPosterView.setImageBitmap(mBg);


        mGenreText.setText(Html.fromHtml("<b>" +

                getString(R.string.details_genre)

                + "</b>" + " " + "<i>" + mGenre + "</i>"));
        mTitleText.setText(Html.fromHtml("<b>" + mTitle + "</b>"));
        mDateText.setText(String.format(

                getString(R.string.details_release_date), mDate

        ));
        mRatingText.setText(String.format(

                getString(R.string.details_rating), mRating

        ));
        mSynopsisText.setText(mSynopsis);
        mRatingStars.setImageDrawable(utils.getRatingImage(mRatingDouble));


        Bitmap bitmap = utils.blurBitmap(mBg, mBg.getWidth(), mBg.getHeight());
        Bitmap twice = utils.blurBitmap(bitmap, mBg.getWidth(), mBg.getHeight());
        Bitmap landscape = Bitmap.createBitmap(twice,
                0,
                twice.getHeight() / 3,
                twice.getWidth(),
                Math.round(twice.getWidth() / 1.5F));
        Drawable bgPortrait = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(twice, width, height, true));
        Drawable bgLandscape = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(landscape, height, width, true));

        if (

                getResources()

                        .

                                getConfiguration()

                        .orientation == Configuration.ORIENTATION_PORTRAIT)

        {
            rootView.setBackgroundDrawable(bgPortrait);
        } else

        {
            rootView.setBackgroundDrawable(bgLandscape);
        }


        return rootView;
    }

    private void getSavedAddonData() {
        reviewList = dataFragment.getReviewList();
        trailerList = dataFragment.getTrailersList();
        trailersContainer.setVisibility(View.VISIBLE);
        reviewsContainer.setVisibility(View.VISIBLE);
        setUpAddonViews();

    }

    private void setUpAddonViews() {
        trailersContainer.setVisibility(View.VISIBLE);
        reviewsContainer.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (trailerList != null) {
            if (trailerList.size() > 0) {
                for (int i = 0; i < trailerList.size(); i++) {
                    View trailersView = inflater.inflate(R.layout.trailers_list_item, null);
                    TextView trailerName = (TextView) trailersView.findViewById(R.id.trailerName);
                    trailerName.setText(trailerList.get(i).getName());
                    trailersView.setLayoutParams(params);
                    trailersContainer.addView(trailersView, i);
                    relayToOnClickListener(trailersView, trailerList.get(i).getKey());
                }

            } else {
                TextView noItems = new TextView(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                noItems.setTextColor(Color.WHITE);
                noItems.setText(R.string.no_trailers);
                trailersContainer.addView(noItems);

            }

            isExpanded = true;


        }
        if (reviewList != null) {
            if (reviewList.size() > 0) {
                for (int j = 0; j < reviewList.size(); j++) {
                    View reviewsView = inflater.inflate(R.layout.reviews_list_item, null);
                    TextView reviewerName = (TextView) reviewsView.findViewById(R.id.reviewerName);
                    TextView reviewText = (TextView) reviewsView.findViewById(R.id.reviewText);
                    reviewerName.setText(reviewList.get(j).getAuthor());
                    reviewText.setText(reviewList.get(j).getContent());
                    reviewsView.setLayoutParams(params);
                    reviewsContainer.addView(reviewsView, j);
                }
            } else {
                TextView noItems = new TextView(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                noItems.setTextColor(Color.WHITE);
                noItems.setText(R.string.no_reviews);
                reviewsContainer.addView(noItems);

            }
            isExpanded = true;


        }
        showMore.setText(buttonText());

    }

    private void relayToOnClickListener(View view, final String key) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(key);
            }
        });

    }

    private String getIntentString() {

        return new Uri.Builder()
                .scheme("https")
                .authority("www.themoviedb.org")
                .appendPath("movie")
                .appendPath(String.valueOf(movieId)).build().toString();
    }

    private String genreString(List<Integer> genreIdList) {
        StringBuilder sb = new StringBuilder();
        if (genreIdList != null) {
            if (genreIdList.size() > 0) {
                for (int i = 0; i < genreIdList.size(); i++) {
                    sb.append(genreHashMap.get(genreIdList.get(i)));
                    if (i < genreIdList.size() - 1) {
                        sb.append(", ");
                    }
                }

            } else {
                sb.append("Not Specified");
            }
        } else {
            sb.append("Not Available");
        }

        return sb.toString();
    }

    @Override
    public void onResume() {
        int colorPrimary = utils.getColor(mBg);
        int colorPrimaryDark = utils.darkenColor(colorPrimary);
        mListener.onFragmentCall(mTitle, colorPrimary, colorPrimaryDark, true);
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cursor != null) {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        mListener.onFragmentCall(getString(R.string.app_name), getResources().getColor(R.color.primary), getResources().getColor(R.color.primary_dark), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataFragment != null) {
            dataFragment.setDetailsData(movieData, mBg);
            dataFragment.setIsExpanded(isExpanded);
            dataFragment.setTrailersList(trailerList);
            dataFragment.setReviewList(reviewList);
            dataFragment.setJsonMovieId(jsonMovieId);
            if (isFavourite) {
                dataFragment.setCursorDetailsData(movieId, mBg);

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentCallbackListener) context;
        } catch (ClassCastException e) {
            Log.e("onAttach: ", e.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fetchAddonData() {
        final String api_key = BuildConfig.TMDB_KEY;
        final Call<Videos> callTrailers;
        final Call<Reviews> callReviews;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ApiInterface api = retrofit.create(ApiInterface.class);

        callTrailers = api.getVideos(String.valueOf(movieId), api_key);
        callReviews = api.getReviews(String.valueOf(movieId), api_key);
        progressBar.setVisibility(View.VISIBLE);
        callReviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Response<Reviews> response, Retrofit retrofit) {
                if (response.errorBody() == null) {
                    Reviews reviews = response.body();
                    reviewList = reviews.getResults();
                    callTrailers.enqueue(new Callback<Videos>() {
                        @Override
                        public void onResponse(Response<Videos> response, Retrofit retrofit) {
                            if (response.errorBody() == null) {
                                Videos videos = response.body();
                                jsonMovieId = videos.getId();
                                trailerList = videos.getResults();
                                setUpAddonViews();
                            } else {
                                handleDataFetchError();
                            }
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            handleDataFetchError();
                        }
                    });
                } else {
                    handleDataFetchError();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                handleDataFetchError();
            }
        });
    }

    private void handleDataFetchError() {
        progressBar.setVisibility(View.GONE);
        isExpanded = false;
        MyDialogFragment dialogFragment = new MyDialogFragment();
        Bundle params = new Bundle();
        if (!isConnected()) {
            params.putString("content", "NoConnection");
        } else {
            params.putString("content", "NoData");
        }
        dialogFragment.setArguments(params);
        getFragmentManager().beginTransaction().add(dialogFragment, "dialog").commit();

    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNet = cm.getActiveNetworkInfo();
        return actNet != null && actNet.isConnected();
    }


    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    private String buttonText() {
        return isExpanded ? getActivity().getResources().getString(R.string.hide_more) : getActivity().getResources().getString(R.string.show_more);
    }
}
