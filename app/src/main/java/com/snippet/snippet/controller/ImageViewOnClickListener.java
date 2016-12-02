package com.snippet.snippet.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageViewOnClickListener implements View.OnClickListener {

    private Context mContext;

    public ImageViewOnClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "You have Clicked: " + ((ImageView) v).toString(), Toast.LENGTH_SHORT).show();
    }
}
