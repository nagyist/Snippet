package com.snippet.snippet.controller.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.ImageViewOnClickListener;

import java.util.List;

/**
 * Created by Mustang on 11/30/16.
 */

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {
    private List<Bitmap> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public ViewHolder(ImageView v, Context context) {
            super(v);
            mImageView = v;
            mImageView.setOnClickListener(new ImageViewOnClickListener(context));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotosRecyclerViewAdapter(List<Bitmap> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PhotosRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_view_preview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((ImageView) v, mContext);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImageView.setImageBitmap(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addImage(Bitmap imageBitmap) {
        mDataset.add(imageBitmap);
        this.notifyDataSetChanged();
    }

    public void addImage(List<Bitmap> imageBitmaps) {
        for (Bitmap imageBitmap: imageBitmaps) {
            mDataset.add(imageBitmap);
        }
        this.notifyDataSetChanged();
    }

    public void addImage(Bitmap[] imageBitmaps) {
        for (Bitmap imageBitmap: imageBitmaps) {
            mDataset.add(imageBitmap);
        }
        this.notifyDataSetChanged();
    }


}