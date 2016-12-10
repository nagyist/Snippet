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
import com.snippet.snippet.controller.DatabaseUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines an Adapter that can display tags in a GridView.  Provides methods
 * for adding and removing tags from the display as well.
 * @author Jordan Burklund
 */

public class TagAdapter extends BaseAdapter {
    private Context context;
    private List<String> tags;
    private Boolean removeFromDB = false;
    private String filepath;

    /**
     * Constructor
     * @param context  Context of the parent activity
     * @param tags  List of initial tags to display
     */
    public TagAdapter(Context context, List<String> tags, String filepath) {
        this.context = context;
        this.tags = tags;
        this.filepath = filepath;
    }

    /**
     * Get the number of tags that the GridView should display
     * @return number of tags in the list
     */
    public int getCount() {
        return tags.size();
    }

    /**
     * Required method to get the Object in the list at a certain position.  Not ever used...
     * @param position
     * @return
     */
    public Object getItem(int position) {
        //TODO
        return null;
    }

    /**
     * Required method to get the ID of an item.  Not ever used...
     * @param position
     * @return
     */
    public long getItemId(int position) {
        //TODO
        return 0;
    }

    /**
     * Add a tag to the view
     * @param tag  Tag name to add
     */
    public void addTag(final String tag) {
        //Ensure that adding tags is run on the UI thread
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add tag, and update the UI
                tags.add(tag);
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Add a list of tags to the view
     * @param new_tags List of tag names to add
     */
    public void addTags(final List<String> new_tags) {
        //Ensure that adding tags is run on the UI thread
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add tags, and update UI
                tags.addAll(new_tags);
                TagAdapter.this.notifyDataSetChanged();
            }
        });
    }

    /**
     * Get a list of tags for this image
     * @return List of tags the user wants to add
     */
    public List<String> getTags() {
        // Shallow copy all of the tags
        return new ArrayList<String>(tags);
    }

    public void shouldAlsoRemoveFromDB(Boolean value) {
        this.removeFromDB = value;
    }

    /**
     * Built-in method to create a new view to be added to the grid
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        //Set the text, and remove button action
        textView.setText(tags.get(position));
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tagname = tags.get(position);
                tags.remove(position);
                notifyDataSetChanged();
                if(TagAdapter.this.removeFromDB) {
                    DatabaseUtils.removeTagFromImage(context, filepath, tagname);
                }
            }
        });
        return convertView;
    }
}
