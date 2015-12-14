package com.wubydax.awesomedaxsmovies;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.wubydax.awesomedaxsmovies.utils.FragmentCallbackListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FragmentCallbackListener, FragmentManager.OnBackStackChangedListener {
    MainViewFragment mainViewFragment;
    boolean isTwoPane;
    SharedPreferences sp;
    MenuItem search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View detailsFragmentContainer = findViewById(R.id.detailsFragmentContainer);
        isTwoPane = detailsFragmentContainer != null;

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isNewState", true);
            bundle.putBoolean("isTwoPane", isTwoPane);
            mainViewFragment = new MainViewFragment();
            mainViewFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, mainViewFragment).commit();
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        updateTitleBySort();
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem fav = menu.findItem(R.id.action_favourite);
        search = menu.findItem(R.id.search);
        share.setVisible(false);
        fav.setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed() {
        SearchView searchView = (SearchView) search.getActionView();
        if (searchView.isShown() && searchView.getQuery().length() > 0 || searchView.hasFocus()) {
            searchView.onActionViewCollapsed();
            searchView.setQuery("", false);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStack();

        } else {
            super.onBackPressed();

        }
    }


    @Override
    public void onFragmentCall(String title, int colorPrimary, int colorPrimaryDark, boolean isDetails) {
        if (isDetails) {
            if (!isTwoPane) {
                setTitle(title);
            }
        } else {
            updateTitleBySort();
        }
        ColorDrawable colorDrawable = new ColorDrawable(colorPrimary);
        try {
            getSupportActionBar().setBackgroundDrawable(colorDrawable);

        } catch (NullPointerException e) {
            Log.e("actionbar null", e.toString());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }
    }

    @Override
    public void onListItemClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        DetailsFragment detailsFragment = DetailsFragment.newInstance(isTwoPane);

        if (!isTwoPane) {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        } else {

            ft.detach(getSupportFragmentManager().findFragmentByTag("details"))
                    .replace(R.id.detailsFragmentContainer, DetailsFragment.newInstance(isTwoPane), "details")
                    .commit();

        }

    }

    @Override
    public void updateTitleBySort() {
        String sortValue = sp.getString("sort_by", "popular");
        int index = Arrays.asList(getResources().getStringArray(R.array.dialog_sort_values)).indexOf(sortValue);
        String title = getResources().getStringArray(R.array.dialog_sort_options)[index];
        setTitle(title);
    }

    @Override
    public void detailsInfoReady() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (isTwoPane) {
            DetailsFragment detailsFragment = DetailsFragment.newInstance(true);
            Fragment old = getSupportFragmentManager().findFragmentByTag("details");
            if (old != null) {
                ft.detach(old)
                        .commit();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.detailsFragmentContainer, detailsFragment, "details")
                    .commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isHomeUpNeeded());
        } catch (NullPointerException e) {
            Log.e("No support action bar ", e.toString());
        }

    }

    private boolean isHomeUpNeeded() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
