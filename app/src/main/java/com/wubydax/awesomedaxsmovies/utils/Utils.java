package com.wubydax.awesomedaxsmovies.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.wubydax.awesomedaxsmovies.R;


public class Utils {
    Context context;
    String LOG_TAG;
    public Utils(Context context){
        this.context = context;
        LOG_TAG = "Utils";

    }

    public int getColor(Bitmap bitmap){
        Palette palette = Palette.generate(bitmap);
        int mainColor = palette.getDarkVibrantColor(palette.getMutedColor(0));
        if(mainColor == 0 || isBrightColor(mainColor)){
            mainColor = palette.getDarkMutedColor(context.getResources().getColor(R.color.primary));
        }
        return mainColor;
    }

    public int darkenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public boolean isBrightColor(int color){

        boolean isBright = false;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math
                .sqrt(rgb[0] * rgb[0] * .241 +
                        rgb[1]* rgb[1] * .691 +
                        rgb[2] * rgb[2] * .068);

        if (brightness >= 200) {
            isBright = true;
        }

        return isBright;
    }

    public Bitmap blurBitmap(Bitmap bitmap, int width, int height) {
        try {
            android.support.v8.renderscript.RenderScript rs = android.support.v8.renderscript.RenderScript.create(context);
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
        } catch (Exception e) {
            Log.e(LOG_TAG, "blurBitmap error blurring bitmap ", e);
            return bitmap;
        }

    }

    public Drawable getRatingImage(Double rating){
        int roundedRating = (int) Math.round(rating);
        int mIdentifier = context.getResources().getIdentifier("rating_" + String.valueOf(roundedRating), "drawable", context.getPackageName());
        return context.getResources().getDrawable(mIdentifier);
    }
}
