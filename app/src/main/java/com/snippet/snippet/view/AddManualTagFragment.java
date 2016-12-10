package com.snippet.snippet.view;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.adapters.TagAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jordan Burklund
 * @date Dec. 2016
 */

public class AddManualTagFragment extends DialogFragment {
    Button addNewTagButton;
    AutoCompleteTextView tagText;
    GridView tagsGridView;

    TagAdapter image_tagAdapter;
    String filepath;

    private static final String FILE_PATH_KEY = "file_path";
    private static final String ADAPTER_KEY = "adapter";
    /**
     * Returns a new Fragment and specifies the filepath and TagAdapter for the actual GridView
     * to display images.  Not a constructor, b/c it violates instantiation of a Fragment
     * @param filepath Path of the file that the tags are going to be added to
     * @param adapter Adapter for the GridView on the ImageViewerActivity
     * @return new AddManualTagFragment
     */
    public static AddManualTagFragment newInstance(String filepath, TagAdapter adapter) {
        AddManualTagFragment f = new AddManualTagFragment();
        //Add the parameters to the Fragment
        f.image_tagAdapter = adapter;
        f.filepath = filepath;
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        //Create a new tag adapter for the grid view to add and remove tags
        final TagAdapter local_tagAdapter = new TagAdapter(getActivity(), new ArrayList<String>());

        //Use the builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_tag_manual, null);
        builder.setView(view);
        builder.setPositiveButton("Add tags", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Add tags from the temp list to the database, and to the GridView
                List<String> newTags = local_tagAdapter.getTags();
                image_tagAdapter.addTags(newTags);
                DatabaseUtils.addTagToFilePath(AddManualTagFragment.this.getActivity(), newTags, filepath);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //remove tags from the temp list
            }
        });

        //Get a reference to the auto complete text view
        tagText = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        //Add a listener to the Add button to add the tag to the temp list
        addNewTagButton = (Button) view.findViewById(R.id.addNewTagButton);
        addNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Pressed Add Tag", Toast.LENGTH_SHORT).show();
                local_tagAdapter.addTag(tagText.getText().toString());
            }
        });

        //Add the adapter to the grid view
        tagsGridView = (GridView) view.findViewById(R.id.fragmentTagGrid);
        tagsGridView.setAdapter(local_tagAdapter);

        //Build the AlertDialog and return it;
        return builder.create();
    }
}
