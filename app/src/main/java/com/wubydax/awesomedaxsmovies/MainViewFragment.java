package com.wubydax.awesomedaxsmovies;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainViewFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context c;
    private String LOG_TAG, SORT_KEY;
    private String jsonTitle, jsonDate, jsonPopularity, jsonRating, jsonSynopsis;
    String mQuery;
    private GridView mGridView;
    SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences mPrefs;
    private MovieAdapter mAdapter;
    private List<Results> mList;
    private List<String> ids;
    private Parcelable state;
    private int width, height;
    private int pagesNumber = 1;
    private DataFragment dataFragment;
    private FragmentCallbackListener mListener;
    private boolean isLoading = false, isSorting = false, isSearch = false;
    private MenuItem sort;
    private Retrofit retrofit;

    public MainViewFragment() {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getActivity();
        LOG_TAG = "MainViewFragment";
        SORT_KEY = "sort_by";
        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("data");
        if (dataFragment == null) {
            dataFragment = new DataFragment();
            getFragmentManager().beginTransaction().add(dataFragment, "data").commit();
        }

        int spacing = Math.round(getResources().getDimension(R.dimen.grid_spacing));
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int dispWidth = size.x;
        int columns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 3 : 5;
        String[] jsonStringTags = new String[]{jsonTitle, jsonDate, jsonPopularity, jsonRating, jsonSynopsis};
        int[] ids = new int[]{R.string.json_title, R.string.json_release_date, R.string.json_popularity, R.string.json_vote_average, R.string.json_release_date};
        for (int i = 0; i < jsonStringTags.length; i++) {
            jsonStringTags[i] = getString(ids[i]);
        }
        width = (dispWidth - spacing * 4) / columns;
        height = Math.round(width * 1.5F);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        sort = menu.findItem(R.id.action_sort);
        sort.setVisible(true);

        final MenuItem search = menu.findItem(R.id.search);
        final SearchManager searchManager =
                (SearchManager) c.getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (android.widget.SearchView) search.getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        if(isSearch){
            mQuery = dataFragment.getQuery();
            if(mQuery!=null){
                searchView.onActionViewExpanded();
                searchView.clearFocus();
                searchView.setQuery(mQuery, false);
            }

        }
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                searchView.clearFocus();
                isSearch = true;
                isLoading = true;
                Toast.makeText(c, "search for " + query, Toast.LENGTH_SHORT).show();
                fetchData(false, isSearch, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                if (newText.length() == 0 && isSearch) {
                    isSearch = false;
                    pagesNumber = 1;
                    isLoading = false;
                    fetchData(true, isSearch, null);
                    Toast.makeText(c, "Search cleared", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });

        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movieGridView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isSearch = dataFragment.getSearchStatus();
        isLoading = dataFragment.isLoading();
        pagesNumber = dataFragment.getPageNumber();
        Log.d(LOG_TAG, "page number is " + String.valueOf(pagesNumber));

        if (savedInstanceState == null) {
            //specifically for popfrombackstack which does not preserve instance
            mList = dataFragment.getMovieDataList();
            ids = dataFragment.getIdsList();

            //if not from back stack and savedInstanceState is null means new app launch
            //so we create list
            if (mList == null) {
                mList = new ArrayList<Results>();
                ids = new ArrayList<String>();
                fetchData(false, false, null);
            } else {
                mAdapter = new MovieAdapter(c, mList, width, height);
                mGridView.setAdapter(mAdapter);
            }
        } else {
            mList = dataFragment.getMovieDataList();
            ids = dataFragment.getIdsList();
            mAdapter = new MovieAdapter(c, mList, width, height);
            mGridView.setAdapter(mAdapter);
        }
        Log.d(LOG_TAG, "onViewCreated isLoading is " + String.valueOf(isLoading));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Bitmap mBitmap;
            Results movieToPass;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                ImageView mThumbnail = (ImageView) view.findViewById(R.id.moviePoster);
                Drawable mDrawable = mThumbnail.getDrawable();


                if (mList != null && mList.size() > 0) {

                    movieToPass = mList.get(i);


                    if (mDrawable != null) {
                        mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                        dataFragment.setDetailsData(movieToPass, mBitmap);
                        mListener.onListItemClick();
                    } else {
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                dataFragment.setDetailsData(movieToPass, bitmap);
                                mListener.onListItemClick();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.with(c).load(getString(R.string.db_poster_path_beginning) + movieToPass.getPosterPath()).into(target);
                    }


                }
            }
        });


        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem, mLastVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastVisibleItem = firstVisibleItem + visibleItemCount;
                if (totalItemCount > 0 && !isLoading && mLastVisibleItem == totalItemCount && totalItemCount == mList.size() && !isSearch) {
                    isLoading = true;
                    Log.d(LOG_TAG, "onScroll setting isLoading to true");
                    pagesNumber++;
                    fetchData(false, false, null);
                }


            }
        });


    }

    private void fetchData(final boolean isSorting, final boolean isSearch, String query) {
        Call<JsonResponse> call;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = format.format(date);
        calendar.add(Calendar.MONTH, -2);
        Date twoMonthAgoDate = calendar.getTime();
        String twoMonthAgo = format.format(twoMonthAgoDate);


        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())

                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);
        swipeRefreshLayout.setRefreshing(true);
        if(!isSearch) {
            call = api.getDataList(twoMonthAgo, todayDate, "en", mPrefs.getString(SORT_KEY, jsonPopularity) + ".desc", "e674a7b5d3ff614af0ef26806ce2d17b", String.valueOf(pagesNumber));
        }else{
            call = api.getSearchDataList(query, "e674a7b5d3ff614af0ef26806ce2d17b");
        }
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Response<JsonResponse> response, Retrofit retrofit) {
                swipeRefreshLayout.setRefreshing(false);
                if(response.isSuccess()) {
                    if (!isSearch) {
                        isLoading = false;
                    }
                    JsonResponse jr = response.body();
                    if (isSorting || isSearch) {
                        mList = new ArrayList<Results>();
                        ids = new ArrayList<String>();
                    }
                    if (jr != null) {
                        for (int i = 0; i < jr.getResults().size(); i++) {
                            Results result = jr.getResults().get(i);
                            String id = String.valueOf(result.getId());
                            if (!result.isAdult() && result.getPosterPath() != null && result.getOriginalLanguage().equals("en") && !ids.contains(id)) {
                                mList.add(result);
                                ids.add(id);
                            }
                        }
                    }
                    state = mGridView.onSaveInstanceState();
                    if (mAdapter == null || isSorting || isSearch) {
                        mAdapter = new MovieAdapter(c, mList, width, height);
                        mGridView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                        if (state != null) {
                            mGridView.onRestoreInstanceState(state);
                        }

                    }
                }else{
                    Log.e(LOG_TAG, response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Throwable t) {


            }


        });
    }

    private boolean isConnected() {
                ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo actNet = cm.getActiveNetworkInfo();
                if (actNet != null) {
                    return actNet.isConnected();
                } else {
                    return false;
                }
            }



    @Override
    public void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        state = mGridView.onSaveInstanceState();
        dataFragment.setMovieData(mList);
        dataFragment.setIdsData(ids);
        dataFragment.setSearchBoolean(isSearch);
        dataFragment.setIsLoading(isLoading);
        dataFragment.setQuery(mQuery);
        dataFragment.setPageNumber(pagesNumber);
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "onSharedPreferenceChanged called");

        if (key.equals(SORT_KEY)) {
            pagesNumber = 1;
            fetchData(true, false, null);
        }


    }


}

