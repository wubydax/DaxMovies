package com.wubydax.awesomedaxsmovies;

import android.graphics.Bitmap;

/**
 * Created by Anna Berkovitch on 23/09/2015.
 */
public interface FragmentCallbackListener {
    public void onFragmentCall(String title, int colorPrimary, int colorPrimaryDark);
    public void onListItemClick();

}


