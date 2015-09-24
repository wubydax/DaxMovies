package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

/**
 * Created by dax on 24/09/15.
 */
public class Utils {
    public Utils(Context context){

    }

    public int getColor(Bitmap bitmap){
        Palette palette = Palette.generate(bitmap);
        int mainColor = palette.getVibrantColor(0);
        if(mainColor == 0 || isBrightColor(mainColor)){
            mainColor = palette.getDarkVibrantColor(palette.getMutedColor(0));
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
}
