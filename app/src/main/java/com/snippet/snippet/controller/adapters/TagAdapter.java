package com.snippet.snippet.controller.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snippet.snippet.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author Jordan Burklund
 */

public class TagAdapter extends BaseAdapter {
    private Context context;
    private List<String> tags;

    public TagAdapter(Context context, List<String> tags) {
        this.context = context;
        this.tags = tags;
    }

    public int getCount() {
        return tags.size();
    }

    public Object getItem(int position) {
        //TODO
        return null;
    }

    public long getItemId(int position) {
        //TODO
        return 0;
    }

    public void addTag(String tag) {
        tags.add(tag);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        LinearLayout linearLayout;
        Button removeButton;

        //If the view hasn't been initialized, create a tag view
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.tag_view, null);
        }

        //Get references to the views
        textView = (TextView) convertView.findViewById(R.id.tagViewText);
        removeButton = (Button) convertView.findViewById(R.id.tagViewCancel);

        //Set the text, and button action
        textView.setText(tags.get(position));
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TagButton", "You clicked to remove a tag!");
            }
        });
        return convertView;
    }
}
