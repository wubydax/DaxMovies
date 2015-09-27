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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainViewFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context c;
    private String LOG_TAG;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private MovieAdapter mAdapter;
    private List<JSONObject> mList;
    private Parcelable state;
    private int width, height;
    private DataFragment dataFragment;
    private FragmentCallbackListener mListener;

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mEditor = mPrefs.edit();
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

        width = (dispWidth - spacing * 4) / columns;
        height = Math.round(width * 1.5F);
        setHasOptionsMenu(true);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem sort = menu.findItem(R.id.action_sort);
        sort.setVisible(true);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movieGridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ImageView mThumbnail = (ImageView) view.findViewById(R.id.moviePoster);
                Drawable mDrawable = mThumbnail.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) mDrawable).getBitmap();

                if (mList != null && mList.size() > 0) {

                    JSONObject jsonToPass = mList.get(i);
                    dataFragment.setDetailsData(jsonToPass, bitmap);
                    mListener.onListItemClick();

                }
            }
        });


        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mList = dataFragment.getJsonList();
            if (mList == null) {
                mList = new ArrayList<>();
                String[] requestedPagesUrls = new String[3];
                for (int i = 1; i < 4; i++) {
                    requestedPagesUrls[i - 1] = c.getString(R.string.db_request_popularity) + String.valueOf(i).toString();
                }
                createData(requestedPagesUrls);
            } else {
                mAdapter = new MovieAdapter(c, mList, width, height);
                mGridView.setAdapter(mAdapter);
            }
        } else {
            mList = dataFragment.getJsonList();
            mAdapter = new MovieAdapter(c, mList, width, height);
            mGridView.setAdapter(mAdapter);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void createData(String[] url) {
        new AsyncTask<String, Void, String[]>() {
            boolean isConnected = false;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String[] doInBackground(String... params) {
                String[] jsonStringArray = new String[3];
                for (int i = 0; i < params.length; i++) {
                    jsonStringArray[i] = getRequest(params[i]);
                }

                return jsonStringArray;

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
            protected void onPostExecute(String[] s) {
                super.onPostExecute(s);
                mProgressBar.setVisibility(View.GONE);
                if (!isConnected) {
                    showDialog("NoConnection");
                } else {
                    mAdapter = new MovieAdapter(c, buildJsonObjectList(s), width, height);
                    mGridView.setAdapter(mAdapter);
                    if (state != null) {
                        mGridView.onRestoreInstanceState(state);
                    }
                }
            }

            private List<JSONObject> buildJsonObjectList(String[] jsonStringArray) {

                try {
                    for (int j = 0; j < jsonStringArray.length; j++) {
                        JSONObject mJsonMain = new JSONObject(jsonStringArray[j]);
                        JSONArray mainArray = mJsonMain.getJSONArray("results");
                        for (int i = 0; i < mainArray.length(); i++) {
                            JSONObject current = mainArray.getJSONObject(i);
                            if (current.getString("poster_path") != null && !current.getString("poster_path").equals("null")) {
                                mList.add(current);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "buildJsonObjectList ", e);
                }
                catch (Exception e){
                    Log.e(LOG_TAG, "buildJsonObjectList ", e);
                    showDialog("NoData");

                }
                String compareParam = mPrefs.getInt("sort_by", 0)==0 ? "popularity" :"vote_average";
                sortList(compareParam);
                return mList;
            }

            private void showDialog(String  param){
                MyDialogFragment mDialog = new MyDialogFragment();
                Bundle params = new Bundle();
                params.putString("content", param);
                mDialog.setArguments(params);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(mDialog, "dialog").commitAllowingStateLoss();
            }

            @Override
            protected void onCancelled() {
                mProgressBar.setVisibility(View.GONE);
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

    public void sortList(final String jsonParam) {
        Collections.sort(mList, new Comparator<JSONObject>() {
            int sortBy;

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    sortBy = Double.compare(Double.parseDouble(lhs.getString(jsonParam)), Double.parseDouble(rhs.getString(jsonParam)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return sortBy;
            }
        });
        Collections.reverse(mList);
    }

    @Override
    public void onPause() {
        state = mGridView.onSaveInstanceState();
        dataFragment.setJsonData(mList);
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
        switch (key) {
            case ("sort_by"):
                int sort = sharedPreferences.getInt(key, 0);
                final String jsonParam = (sort == 0) ? "popularity" : "vote_average";
                sortList(jsonParam);
                mAdapter.notifyDataSetChanged();
                break;
        }


    }

}
