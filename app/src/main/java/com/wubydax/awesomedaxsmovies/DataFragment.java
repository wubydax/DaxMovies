package com.wubydax.awesomedaxsmovies;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private JSONObject jsonObject;
    private Bitmap posterBitmap;
    private List<JSONObject> jsonList;


    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setDetailsData(JSONObject json, Bitmap poster) {
        jsonObject = json;
        posterBitmap = poster;

    }

    public void setJsonData(List<JSONObject> jsonList) {
        this.jsonList = jsonList;

    }

    public List<JSONObject> getJsonList() {
        return jsonList;
    }

    public JSONObject getJson() {
        return jsonObject;
    }

    public Bitmap getBitmap() {
        return posterBitmap;
    }


}
