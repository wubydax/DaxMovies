package com.wubydax.awesomedaxsmovies.utils;

public interface FragmentCallbackListener {
    void onFragmentCall(String title, int colorPrimary, int colorPrimaryDark, boolean isDetails);

    void onListItemClick();

    void updateTitleBySort();

}


