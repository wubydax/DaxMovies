package com.wubydax.awesomedaxsmovies.utils;

import com.wubydax.awesomedaxsmovies.api.JsonResponse;

public interface FragmentCallbackListener {
    void onFragmentCall(String title, int colorPrimary, int colorPrimaryDark, boolean isDetails);

    void onListItemClick();

    void updateTitleBySort();

    void detailsInfoReady();

}


