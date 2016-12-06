package com.snippet.snippet.controller;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;
import com.snippet.snippet.view.ImageViewerActivity;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageViewOnClickListener implements View.OnClickListener {

    private Context mContext;
    private PhotosRecyclerViewAdapter.ViewHolder mViewHolder;

    public ImageViewOnClickListener(Context context, PhotosRecyclerViewAdapter.ViewHolder viewHolder) {
        mContext = context;
        mViewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(mContext, "You have Clicked: " + ((ImageView) v).toString(), Toast.LENGTH_SHORT).show();
        String path = mViewHolder.getPath();
        Intent imageViewerIntent = new Intent(mContext, ImageViewerActivity.class);
        imageViewerIntent.putExtra(ImageViewerActivity.FILEPATH_EXTRA_KEY, path);
        imageViewerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(imageViewerIntent);
    }
}
