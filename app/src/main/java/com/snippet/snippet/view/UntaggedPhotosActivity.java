package com.snippet.snippet.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.snippet.snippet.view.MainWindow_Activity.PERMISSION_CAMERA;

public class UntaggedPhotosActivity extends AppCompatActivity {

    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;

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
                Toast.makeText(UntaggedPhotosActivity.this, "Camera Button", Toast.LENGTH_SHORT).show();
                requestCameraPermissions();
            }
        });

        updateRecyclerView(DatabaseUtils.getImagePathsWithoutTags(this));
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
}
