package com.snippet.snippet.controller.adapters;

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
        if (convertView == null) {
            //if it's not recycled, initialize attributes
            linearLayout = new LinearLayout(context);
            textView = new TextView(context);
            removeButton = new Button(context);
            removeButton.setText("X");
            textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            linearLayout.addView(textView);
            linearLayout.addView(removeButton);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(8,8,8,8);
        } else {
            linearLayout = (LinearLayout) convertView;
            textView = (TextView) linearLayout.getChildAt(0);
            removeButton = (Button) linearLayout.getChildAt(1);
        }

        textView.setText(tags.get(position));
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TagButton", "You clicked to remove a tag!");
            }
        });
        return linearLayout;
    }
}
