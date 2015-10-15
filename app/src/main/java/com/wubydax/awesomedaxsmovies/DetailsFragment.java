package com.wubydax.awesomedaxsmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wubydax.awesomedaxsmovies.api.JsonResponse;
import com.wubydax.awesomedaxsmovies.utils.DataFragment;
import com.wubydax.awesomedaxsmovies.utils.FragmentCallbackListener;
import com.wubydax.awesomedaxsmovies.utils.Utils;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private Bitmap mBg;
    private JsonResponse.Results movieData;
    private String mTitle;
    private FragmentCallbackListener mListener;
    private DataFragment dataFragment;
    private HashMap<Integer, String> genreHashMap;
    Utils utils;


    public DetailsFragment() {
        // Required empty public constructor
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
        share.setVisible(true);
        sort.setVisible(false);
        search.setVisible(false);
        refresh.setVisible(false);
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
                intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_intent_string), movieData.getTitle(), getIntentString()));
                startActivity(Intent.createChooser(intent, getActivity().getString(R.string.intent_picker_title)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataFragment.setDetailsData(movieData, mBg);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        utils = new Utils(getActivity());
        int width = Math.round(getResources().getDimension(R.dimen.blured_image_width));
        int height = Math.round(getResources().getDimension(R.dimen.blured_image_height));
        dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("data");
        movieData = dataFragment.getMovieData();
        mBg = dataFragment.getBitmap();
        genreHashMap = dataFragment.getHashMapGenres();

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView mTitleText = (TextView) rootView.findViewById(R.id.detailTitle);
        TextView mDateText = (TextView) rootView.findViewById(R.id.detailDate);
        TextView mRatingText = (TextView) rootView.findViewById(R.id.detailRating);
        TextView mSynopsisText = (TextView) rootView.findViewById(R.id.detailSynopsis);
        TextView mGenreText = (TextView) rootView.findViewById(R.id.detailGenreText);
        ImageView mPosterView = (ImageView) rootView.findViewById(R.id.detailPoster);
        ImageView mRatingStars = (ImageView) rootView.findViewById(R.id.ratingImage);
        mPosterView.setImageBitmap(mBg);

        mTitle = movieData.getTitle();
        String mDate = movieData.getReleaseDate();
        String mRating = String.valueOf(movieData.getVoteAverage());
        String mSynopsis = movieData.getOverview();
        List<Integer> genreIdList = movieData.getGenreIds();
        Double mRatingDouble = Double.parseDouble(mRating);
        mTitleText.setText(Html.fromHtml("<b>" + mTitle + "</b>"));
        mDateText.setText(String.format(getString(R.string.details_release_date), mDate));
        mRatingText.setText(String.format(getString(R.string.details_rating), mRating));
        mGenreText.setText(Html.fromHtml("<b>" + getString(R.string.details_genre) + "</b>" + " " + "<i>" + genreString(genreIdList) + "</i>"));
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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rootView.setBackgroundDrawable(bgPortrait);
        } else {
            rootView.setBackgroundDrawable(bgLandscape);
        }


        return rootView;
    }

    private String getIntentString() {

        return new Uri.Builder()
                .scheme("https")
                .authority("www.themoviedb.org")
                .appendPath("movie")
                .appendPath(String.valueOf(movieData.getId())).build().toString();
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
        mListener.onFragmentCall(getString(R.string.app_name), getResources().getColor(R.color.primary), getResources().getColor(R.color.primary_dark), false);
    }


    @Override
    public void onAttach(Context context) {
        String LOG_TAG = "DetailsFragment";
        super.onAttach(context);
        try {
            mListener = (FragmentCallbackListener) context;
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "onAttach Activity must implement the interface", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
