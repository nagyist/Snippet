package com.snippet.snippet.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.snippet.snippet.view.ImageViewerActivity;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageViewOnClickListener implements View.OnClickListener {

    private Context mContext;
    private int mFileId;

    public ImageViewOnClickListener(Context context, int fileId) {
        mContext = context;
        mFileId = fileId;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "You have Clicked: " + ((ImageView) v).toString(), Toast.LENGTH_SHORT).show();
        Bitmap image = ((BitmapDrawable)((ImageView) v).getDrawable()).getBitmap(); // b/c Bitmap implements Parcelable
        // TODO Best practice dictates not passing the whole bitmap in the extras,
        // should we just pass id and let image viewer reload from db?
        Intent imageViewerIntent = new Intent(mContext, ImageViewerActivity.class);
        imageViewerIntent.putExtra(ImageViewerActivity.FILEID_EXTRA_KEY, mFileId);
        imageViewerIntent.putExtra(ImageViewerActivity.BITMAP_EXTRA_KEY, image);
        imageViewerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(imageViewerIntent);
    }
}
