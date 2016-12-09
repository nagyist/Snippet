package com.snippet.snippet.view;

import android.app.AlertDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.TagListener;
import com.snippet.snippet.controller.adapters.ClarifAIHelper;
import com.snippet.snippet.controller.adapters.TagAdapter;
import com.snippet.snippet.model.DatabaseHelper;

import java.util.List;
import com.snippet.snippet.controller.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String FILEPATH_EXTRA_KEY = "snippet/file_path";
    public static final String BITMAP_EXTRA_KEY = "snippet/bitmap";

    @BindView(R.id.bigImageView) ImageView mImageView;
    @BindView(R.id.addManageTagsBtn) Button tagsButton;
    @BindView(R.id.tagGrid) GridView gridView;
    @BindView(R.id.layoutTags) LinearLayout layoutTagsView;
    @BindView(R.id.buttonCloseTags) Button closeTagsButton;
    @BindView(R.id.buttonAddTag) Button addTagsButton;

    AlertDialog autoTagDialog;

    private int mImageId;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ButterKnife.bind(this);

        mFilePath = getIntent().getStringExtra(FILEPATH_EXTRA_KEY);
        ImageUtils.addImageToImageView(this, mImageView, mFilePath, null, null);

        //Build the dialog for prompting the user to send the image to ClarifAI
        createAutoTagDialog();

        //Launch the dialog if the user has not previously autotagged this image
        try{
            if (!DatabaseUtils.getAutoTaggedFromFilePath(ImageViewerActivity.this, mFilePath)) {
                autoTagDialog.show();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            //Image could not be found in the database
            autoTagDialog.show();
            Toast.makeText(ImageViewerActivity.this, "Could not find image in DB", Toast.LENGTH_SHORT).show();
        }

        // TODO placeholder
        tagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageViewerActivity.this, "Tags Button", Toast.LENGTH_SHORT).show();
                List<String> tags = DatabaseUtils.getTagsFromFilePath(ImageViewerActivity.this, mFilePath);

                gridView.setAdapter(new TagAdapter(ImageViewerActivity.this, tags));
                layoutTagsView.setVisibility(View.VISIBLE);
                //gridView.setVisibility(View.VISIBLE);
                for(String tag: tags) {
                    Log.d("Tags Available", mFilePath+": "+tag);
                }
            }
        });

        //Allow the close button to close the tag viewer window
        closeTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTagsView.setVisibility(View.GONE);
            }
        });

        //Allow the add button to launch a fragment to manually add tags
        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddManualTagFragment().show(ImageViewerActivity.this.getFragmentManager(), "AddManualTag");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle back arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAutoTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //TODO change string constants to resources
        builder.setMessage(R.string.Autotag_Dialog_Message)
                .setTitle(R.string.Autotag_Dialog_Title);
        builder.setPositiveButton(R.string.Autotag_Dialog_Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(ImageViewerActivity.this, "You clicked Yes", Toast.LENGTH_SHORT).show();
                //Set appropriate flag in database
                DatabaseUtils.setAutoTaggedFromFilePath(ImageViewerActivity.this, mFilePath, true);
                //Launch ClarifAI request
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClarifAIHelper clarifAIHelper = new ClarifAIHelper(ImageViewerActivity.this);
                        clarifAIHelper.sendToClarifAI(mFilePath, tagListener);
                    }
                }).start();

            }
        });
        builder.setNegativeButton(R.string.Autotag_Dialog_No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(ImageViewerActivity.this, "You clicked No", Toast.LENGTH_SHORT).show();
                //Don't do anything, because we want the user to tag images
            }
        });
        autoTagDialog = builder.create();
    }

    TagListener tagListener = new TagListener() {
        @Override
        public void onReceiveTags(List<String> tags) {
            //Update the tags in the database
            DatabaseUtils.addTagToFilePath(ImageViewerActivity.this, tags, mFilePath);
        }
    };

}
