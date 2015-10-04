package com.wubydax.awesomedaxsmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainViewFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context c;
    private String LOG_TAG, SORT_KEY;
    private String jsonTitle, jsonDate, jsonPopularity, jsonRating, jsonSynopsis;
    private GridView mGridView;
    private View searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchEditText;
    private SharedPreferences mPrefs;
    private MovieAdapter mAdapter;
    private List<MovieData> mList;
    private List<String> ids;
    private Parcelable state;
    private int width, height;
    private int pagesNumber = 1;
    private DataFragment dataFragment;
    private FragmentCallbackListener mListener;
    private boolean isLoading = false, isSorting = false, isSearch = false;
    private MenuItem sort;

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
        if (searchEditText.hasFocus()) {
            sort.setVisible(false);
        }
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movieGridView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        isLoading = swipeRefreshLayout.isRefreshing();
        searchView = rootView.findViewById(R.id.searchContainer);
        searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);
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

        if (savedInstanceState == null) {
            //specifically for popfrombackstack which does not preserve instance
            mList = dataFragment.getMovieDataList();
            ids = dataFragment.getIdsList();
            if (dataFragment.getSearchStatus()) {
                searchEditText.requestFocus();
                isLoading = true;
            }
            //if not from back stack and savedInstanceState is null means new app launch
            //so we create list
            if (mList == null) {
                mList = new ArrayList<MovieData>();
                ids = new ArrayList<String>();
                createData(getUrl());
            } else {
                mAdapter = new MovieAdapter(c, mList, width, height);
                mGridView.setAdapter(mAdapter);
            }
        } else {
            pagesNumber = savedInstanceState.getInt("pageNumber");
            isLoading = savedInstanceState.getBoolean("isLoading");
            mList = dataFragment.getMovieDataList();
            ids = dataFragment.getIdsList();
            mAdapter = new MovieAdapter(c, mList, width, height);
            mGridView.setAdapter(mAdapter);
        }
        Log.d(LOG_TAG, "onViewCreated isLoading is " + String.valueOf(isLoading));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Bitmap mBitmap;
            MovieData movieToPass;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                ImageView mThumbnail = (ImageView) view.findViewById(R.id.moviePoster);
                Drawable mDrawable = mThumbnail.getDrawable();


                if (mList != null && mList.size() > 0) {
                    if (isSearch) {
                        movieToPass = dataFragment.getMovieDataList().get(i);
                    } else {
                        movieToPass = mList.get(i);

                    }
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

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (sort != null) {

                    if (hasFocus) {
                        sort.setVisible(false);
                        isLoading = true;
                        isSearch = true;
                    } else {
                        isSearch = false;
                        isLoading = false;
                        sort.setVisible(true);
                        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {

                    ((EditText) v).setText("");
                    searchView.setVisibility(View.GONE);

                    return true;
                }
                return false;
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
                if (totalItemCount > 0 && !isLoading && mLastVisibleItem == totalItemCount && totalItemCount == mList.size()) {
                    isLoading = true;
                    Log.d(LOG_TAG, "onScroll setting isLoading to true");
                    pagesNumber++;
                    isSorting = false;
                    createData(getUrl());
                }

                if (firstVisibleItem > mLastFirstVisibleItem) {
                    searchView.setVisibility(View.GONE);
                } else if (firstVisibleItem < mLastFirstVisibleItem) {
                    searchView.setVisibility(View.VISIBLE);
                }

                mLastFirstVisibleItem = firstVisibleItem;


            }
        });


    }

    private String getUrl() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = format.format(date);
        calendar.add(Calendar.MONTH, -2);
        Date twoMonthAgoDate = calendar.getTime();
        String twoMonthAgo = format.format(twoMonthAgoDate);
        String url = new Uri.Builder()
                .scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("primary_release_date.gte", twoMonthAgo)
                .appendQueryParameter("primary_release_date.lte", todayDate)
                .appendQueryParameter("language", "en")
                .appendQueryParameter("sort_by", mPrefs.getString(SORT_KEY, jsonPopularity) + ".desc")
                .appendQueryParameter("api_key", "e674a7b5d3ff614af0ef26806ce2d17b")
                .appendQueryParameter("page", String.valueOf(pagesNumber))
                .build().toString();

        Log.d(LOG_TAG, "getUrl " + url);
        return url;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageNumber", pagesNumber);
        outState.putBoolean("isLoading", isLoading);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void createData(String url) {
        new AsyncTask<String, Void, List<MovieData>>() {
            boolean isConnected = false;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected List<MovieData> doInBackground(String... params) {
                String jsonString = getRequest(params[0]);


                List<MovieData> movieDataList;
                List<String> idList;
                Log.d(LOG_TAG, "buildJsonObjectList page number is " + String.valueOf(pagesNumber));
                if (isSorting) {
                    movieDataList = new ArrayList<MovieData>();
                    idList = new ArrayList<String>();
                    mList.clear();
                    ids.clear();
                    Log.d(LOG_TAG, "doInBackground sorting is true");
                } else {
                    movieDataList = mList;
                    idList = ids;
                    Log.d(LOG_TAG, "doInBackground sorting is false");
                }

                try {
                    JSONObject mJsonMain = new JSONObject(jsonString);
                    JSONArray mainArray = mJsonMain.getJSONArray("results");
                    for (int i = 0; i < mainArray.length(); i++) {

                        JSONObject current = mainArray.getJSONObject(i);
                        MovieData movieDataCurrent = new MovieData();

                        movieDataCurrent.setId(current.getString("id"));
                        movieDataCurrent.setTitle(current.getString("title"));
                        movieDataCurrent.setDate(current.getString("release_date"));
                        movieDataCurrent.setSynopsis(current.getString("overview"));
                        movieDataCurrent.setPosterPath(current.getString("poster_path"));
                        movieDataCurrent.setVoteAverage(current.getString("vote_average"));
                        movieDataCurrent.setVoteCount(current.getString("vote_count"));

                        if (!idList.contains(movieDataCurrent.getId())) {
                            if (!movieDataCurrent.getPosterPath().equals("null") && !movieDataCurrent.getSynopsis().equals("null") && current.getString("original_language").equals("en")) {
                                movieDataList.add(movieDataCurrent);
                                idList.add(movieDataCurrent.getId());
                            }

                        } else {
                            Log.d(LOG_TAG, "buildJsonObjectList list contains object !!!!!!!");
                        }

                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "buildJsonObjectList ", e);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "buildJsonObjectList ", e);

                }
                mList = movieDataList;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return movieDataList;


            }

            private String getRequest(String url) {
                HttpURLConnection mConnection = null;
                String jsonString = null;

                if (isConnected()) {

                    try {
                        URL mUrl = new URL(url);
                        mConnection = (HttpURLConnection) mUrl.openConnection();
                        mConnection.setRequestMethod("GET");
                        mConnection.connect();
                        InputStream inputStream = mConnection.getInputStream();
                        jsonString = getStringFromInputStream(inputStream);
                    } catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "doInBackground ", e);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "doInBackground ", e);
                    } finally {
                        if (mConnection != null) {
                            mConnection.disconnect();
                        }
                    }
                    isConnected = true;

                } else {
                    isConnected = false;
                    jsonString = null;
                }
                return jsonString;

            }

            private String getStringFromInputStream(InputStream is) throws IOException {
                String retrievedString = null;
                StringBuffer stringBuffer = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                if (is != null) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuffer.append(line + "\n");
                    }
                    if (stringBuffer.length() > 0) {
                        retrievedString = stringBuffer.toString();
                    }
                } else {
                    retrievedString = null;
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "getStringFromInputStream error closing BufferedReader ", e);
                    }
                }
                return retrievedString;
            }

            @Override
            protected void onPostExecute(List<MovieData> list) {
                super.onPostExecute(list);
                swipeRefreshLayout.setRefreshing(false);
                if (!isConnected) {
                    showDialog("NoConnection");
                } else {
                    state = mGridView.onSaveInstanceState();
                    if (mAdapter == null || isSorting) {
                        mAdapter = new MovieAdapter(c, list, width, height);
                        mGridView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                        if (state != null) {
                            mGridView.onRestoreInstanceState(state);
                        }

                    }

                    isLoading = false;
                }


            }

            private void showDialog(String param) {
                MyDialogFragment mDialog = new MyDialogFragment();
                Bundle params = new Bundle();
                params.putString("content", param);
                mDialog.setArguments(params);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(mDialog, "dialog").commitAllowingStateLoss();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
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
        }.execute(url);
    }


    @Override
    public void onPause() {
        state = mGridView.onSaveInstanceState();
        dataFragment.setMovieData(mList);
        dataFragment.setIdsData(ids);
        dataFragment.setSearchBoolean(isSearch);
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
            isSorting = true;
            pagesNumber = 1;
            createData(getUrl());
        }


    }


}

