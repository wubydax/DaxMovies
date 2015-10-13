package com.wubydax.awesomedaxsmovies.utils;

import android.graphics.Bitmap;

/**
 * Created by Anna Berkovitch on 23/09/2015.
 */
public interface FragmentCallbackListener {
    public void onFragmentCall(String title, int colorPrimary, int colorPrimaryDark, boolean isDetails);
    public void onListItemClick();
    public void updateTitleBySort();

}


