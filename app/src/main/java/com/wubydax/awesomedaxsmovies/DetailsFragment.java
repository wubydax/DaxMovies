package com.wubydax.awesomedaxsmovies;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private Bitmap mBg;
    private JSONObject jsonObject;
    private Context c;
    private String LOG_TAG = "DetailsFragment";
    private int width, height;
    private FragmentCallbackListener mListener;
    private String mTitle, mDate, mRating, mSynopsis;
    private double mRatingDouble;
    private DataFragment dataFragment;
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
        sort.setVisible(false);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public void onPause() {
        super.onPause();
        dataFragment.setDetailsData(jsonObject, mBg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        c = getActivity();
        utils = new Utils(c);
        width = Math.round(getResources().getDimension(R.dimen.blured_image_width));
        height = Math.round(getResources().getDimension(R.dimen.blured_image_height));
        dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("data");
        jsonObject = dataFragment.getJson();
        mBg = dataFragment.getBitmap();

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView mTitleText = (TextView) rootView.findViewById(R.id.detailTitle);
        TextView mDateText = (TextView) rootView.findViewById(R.id.detailDate);
        TextView mRatingText = (TextView) rootView.findViewById(R.id.detailRating);
        TextView mSynopsisText = (TextView) rootView.findViewById(R.id.detailSynopsis);
        ImageView mPosterView = (ImageView) rootView.findViewById(R.id.detailPoster);
        ImageView mRatingStars = (ImageView) rootView.findViewById(R.id.ratingImage);
        mPosterView.setImageBitmap(mBg);

        try {
            mTitle = jsonObject.getString(c.getString(R.string.json_title));
            mDate = jsonObject.getString(c.getString(R.string.json_release_date));
            mRating = jsonObject.getString(c.getString(R.string.json_vote_average));
            mSynopsis = jsonObject.getString(c.getString(R.string.json_synopsis));
            mRatingDouble = Double.parseDouble(mRating);
            mTitleText.setText(mTitle);
            mDateText.setText(String.format(getString(R.string.details_release_date), mDate));
            mRatingText.setText(String.format(getString(R.string.details_rating), mRating));
            mSynopsisText.setText(mSynopsis);
            mRatingStars.setImageDrawable(utils.getRatingImage(mRatingDouble));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        int colorPrimary = utils.getColor(mBg);
        int colorPrimaryDark = utils.darkenColor(colorPrimary);

        mListener.onFragmentCall(mTitle, colorPrimary, colorPrimaryDark);

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        mListener.onFragmentCall(getString(R.string.app_name), getResources().getColor(R.color.primary), getResources().getColor(R.color.primary_dark));
    }


    @Override
    public void onAttach(Context context) {
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
