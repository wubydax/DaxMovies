package com.wubydax.awesomedaxsmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.wubydax.awesomedaxsmovies.utils.FragmentCallbackListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FragmentCallbackListener, FragmentManager.OnBackStackChangedListener {
    MainViewFragment mainViewFragment;
    String FRAGMENT_TAG;
    FragmentTransaction ft;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    MenuItem search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mainViewFragment = new MainViewFragment();
            ft.add(R.id.container, new MainViewFragment()).commit();


        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        updateTitleBySort();
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem share = menu.findItem(R.id.share);
        search = menu.findItem(R.id.search);
        share.setVisible(false);

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
        if(isDetails) {
            setTitle(title);
        }else{
            updateTitleBySort();
        }
        ColorDrawable colorDrawable = new ColorDrawable(colorPrimary);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }
    }

    @Override
    public void onListItemClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DetailsFragment detailsFragment = new DetailsFragment();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.container, detailsFragment).addToBackStack(null);
        ft.commit();


    }

    @Override
    public void updateTitleBySort() {
        String sortValue = sp.getString("sort_by", "popular");
        int index = Arrays.asList(getResources().getStringArray(R.array.dialog_sort_values)).indexOf(sortValue);
        String title = getResources().getStringArray(R.array.dialog_sort_options)[index];
        setTitle(title);
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isHomeUpNeeded());

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
