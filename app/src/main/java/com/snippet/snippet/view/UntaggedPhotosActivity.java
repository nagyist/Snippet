package com.snippet.snippet.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.ImageUtils;
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.snippet.snippet.view.MainWindow_Activity.PERMISSION_CAMERA;

public class UntaggedPhotosActivity extends AppCompatActivity {

    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;

    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_untagged_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar toolbarNew = getSupportActionBar();
        if (toolbarNew != null) { // if statement is to get rid of potential null pointer warning
            toolbarNew.setDisplayHomeAsUpEnabled(true);
            toolbarNew.setDisplayShowHomeEnabled(true);
        }

        ButterKnife.bind(this);

        untaggedPhotosRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        List<String> resourceLocations = new ArrayList<>();
        untaggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));

        // TODO camera logic copied from MainWindow_Activity which was incomplete at the time
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(UntaggedPhotosActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermissions();
                }
                else {
                    dispatchTakePictureIntent();
                }
            }
        });

        new AsyncImageLogicPaths().execute();
    }

    // Copied from MainWindow_Activity
    public void requestCameraPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    private void updateRecyclerView(List<String> imagesToAdd) {
        ((PhotosRecyclerViewAdapter) untaggedPhotosRecyclerView.getAdapter()).replaceDataset(imagesToAdd);
    }

    protected class AsyncImageLogicPaths extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> paths = DatabaseUtils.getUntaggedImagePathsWithoutTags(UntaggedPhotosActivity.this);

            return paths;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            updateRecyclerView(result);
        }
    }

    @Override
    protected void onStart() {
        new AsyncImageLogicPaths().execute();
        super.onStart();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ImageUtils.REQUEST_IMAGE_CAPTURE);
                currentPhotoPath = photoFile.getAbsolutePath();
            }
        }
    }

    protected class TakePictureUpdateViews extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> paths = new ArrayList<>();
            DatabaseUtils.addFilePathToDB(getApplicationContext(), params[0], false);

            paths = DatabaseUtils.getUntaggedImagesFromDB(getApplicationContext());

            return paths;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            currentPhotoPath = "";
            updateRecyclerView(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String path = currentPhotoPath;
            new TakePictureUpdateViews().execute(currentPhotoPath);
            Intent imageViewerIntent = new Intent(this, ImageViewerActivity.class);
            imageViewerIntent.putExtra(ImageViewerActivity.FILEPATH_EXTRA_KEY, path);
            startActivity(imageViewerIntent);
        }
    }
}
