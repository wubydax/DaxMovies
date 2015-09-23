package com.wubydax.awesomedaxsmovies;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends android.support.v4.app.Fragment{
    private Drawable mBg;
    private JSONObject jsonObject;
    private Context c;
    private String LOG_TAG = "DetailsFragment";
    private int width, height;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        c = getActivity();
        width = Math.round(getResources().getDimension(R.dimen.blured_image_width));
        height = Math.round(getResources().getDimension(R.dimen.blured_image_height));
        View rootView = inflater. inflate(R.layout.fragment_details, container, false);
        try {
        ImageView mPosterView = (ImageView) rootView.findViewById(R.id.detailPoster);

            Picasso.with(c).
                    load(getString(R.string.db_poster_path_beginning) + jsonObject.getString("poster_path"))
                    .resize(Math.round(getResources().getDimension(R.dimen.detail_poster_width)),
                            Math.round(getResources().getDimension(R.dimen.detail_poster_height)))
                    .into(mPosterView);

        TextView mTitleText = (TextView) rootView.findViewById(R.id.detailTitle);
        TextView mDateText = (TextView) rootView.findViewById(R.id.detailDate);
        TextView mRatingText = (TextView) rootView.findViewById(R.id.detailRating);
        TextView mSynopsisText = (TextView) rootView.findViewById(R.id.detailSynopsis);
            mTitleText.setText(jsonObject.getString("title"));
            mDateText.setText(jsonObject.getString("release_date"));
            mRatingText.setText(jsonObject.getString("vote_average"));
            mSynopsisText.setText(jsonObject.getString("overview"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = blurBitmap(convertDrawableToBitmap(mBg), mBg.getIntrinsicWidth(), mBg.getIntrinsicHeight());
        Bitmap twice = blurBitmap(bitmap, mBg.getIntrinsicWidth(), mBg.getIntrinsicHeight());
        Drawable bg = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(twice, width, height, true));

        rootView.setBackgroundDrawable(bg);

        return rootView;
    }

    private Bitmap convertDrawableToBitmap(Drawable drawable){

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap blurBitmap(Bitmap bitmap, int width, int height){
        try
        {
            android.support.v8.renderscript.RenderScript rs = android.support.v8.renderscript.RenderScript.create(c);
            android.support.v8.renderscript.Allocation allocation = android.support.v8.renderscript.Allocation.createFromBitmap(rs, bitmap);

            android.support.v8.renderscript.ScriptIntrinsicBlur blur = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
            blur.setRadius(25);
            blur.setInput(allocation);

            Bitmap blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            android.support.v8.renderscript.Allocation outAlloc = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurredBitmap);

            blur.forEach(outAlloc);
            outAlloc.copyTo(blurredBitmap);

            rs.destroy();
            return blurredBitmap;
        }
        catch (Exception e) {
            Log.d(LOG_TAG, "blurBitmap error bluring bitmap ", e);
            return bitmap;
        }

    }

    public void getBackground(Drawable drawable){
        mBg = drawable;
    }

    public void getJSONObject(JSONObject json){
        jsonObject = json;

    }


}