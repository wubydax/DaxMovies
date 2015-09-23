package com.wubydax.awesomedaxsmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Context c;
    private String LOG_TAG;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private MovieAdapter mAdapter;
    private List<JSONObject> mList;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        c = getActivity();
        LOG_TAG = "MainActivityFragment";
        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        mEditor = mPrefs.edit();
        final View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.movieGridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        String[] requestedPagesUrls = new String[3];
        for (int i = 1; i < 4; i++) {
            requestedPagesUrls[i - 1] = c.getString(R.string.db_request) + String.valueOf(i).toString();
        }
        createData(requestedPagesUrls);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView mThumbnail = (ImageView) view.findViewById(R.id.moviePoster);
                Drawable mDrawable = mThumbnail.getDrawable();
                if (mList != null && mList.size() > 0) {

                    JSONObject jsonToPass = mList.get(i);

                    DetailsFragment df = new DetailsFragment();
                    df.getBackground(mDrawable);
                    df.getJSONObject(jsonToPass);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(container.getId(), df).addToBackStack(null).commit();
                }
            }
        });
        return rootView;
    }

    private void createData(String[] url) {
        new AsyncTask<String, Void, String[]>() {


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

                } else {
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
                if (!isConnected()) {
                    MyDialogFragment mDialog = new MyDialogFragment();
                    Bundle params = new Bundle();
                    params.putString("content", "NoConnection");
                    mDialog.setArguments(params);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(mDialog, "dialog").commitAllowingStateLoss();
                } else {
                    mAdapter = new MovieAdapter(c, buildJsonObjectList(s));
                    mGridView.setAdapter(mAdapter);
                }
            }

            private List<JSONObject> buildJsonObjectList(String[] jsonStringArray) {
                mList = new ArrayList<>();

                try {
                    for (int j = 0; j < jsonStringArray.length; j++) {
                        JSONObject mJsonMain = new JSONObject(jsonStringArray[j]);
                        JSONArray mainArray = mJsonMain.getJSONArray("results");
                        for (int i = 0; i < mainArray.length(); i++) {
                            JSONObject current = mainArray.getJSONObject(i);

                            mList.add(mainArray.getJSONObject(i));
                        }
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "buildJsonObjectList ", e);
                }
                return mList;
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

}
