package com.wubydax.awesomedaxsmovies;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDialogFragment extends android.support.v4.app.DialogFragment {


    public MyDialogFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context c = getActivity();
        Bundle params = getArguments();
        String mContent = params.getString("content", null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(c);
                if(mContent.equals("NoConnection")){
                    mBuilder.setTitle(R.string.device_offline_title)
                            .setMessage(R.string.device_offline_message)
                            .setNegativeButton(android.R.string.cancel, null);
                }
        AlertDialog dialog = mBuilder.create();
        return dialog;
    }
}
