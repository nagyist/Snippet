package com.snippet.snippet.view;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.snippet.snippet.R;

/**
 * @author Jordan Burklund
 * @date Dec. 2016
 */

public class AddManualTagFragment extends DialogFragment {
    Button addNewTagButton;
    Button addAllTagsButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {

        //Use the builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_tag_manual, null);;
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


        //Add a listener to the Add button to add the tag to the temp list
        addNewTagButton = (Button) view.findViewById(R.id.addNewTagButton);
        addNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Pressed Add Tag", Toast.LENGTH_SHORT).show();
            }
        });

        //Build the AlertDialog and return it;
        return builder.create();
    }
}
