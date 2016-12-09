package com.snippet.snippet.view;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.snippet.snippet.R;
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
    Button addAllTagsButton;
    AutoCompleteTextView tagText;
    GridView tagsGridView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        //Create a new tag adapter for the grid view to add and remove tags
        List<String> testList = new ArrayList<String>(Arrays.asList("apple", "orange"));
        final TagAdapter tagAdapter = new TagAdapter(getActivity(), new ArrayList<String>());

        //Use the builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_tag_manual, null);
        builder.setView(view);
        builder.setPositiveButton("Add tags", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Add tags from the temp list to the database
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
                tagAdapter.addTag(tagText.getText().toString());
            }
        });

        //Add the adapter to the grid view
        tagsGridView = (GridView) view.findViewById(R.id.fragmentTagGrid);
        tagsGridView.setAdapter(tagAdapter);

        //Build the AlertDialog and return it;
        return builder.create();
    }
}
