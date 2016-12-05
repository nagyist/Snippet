package com.snippet.snippet.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;
import com.snippet.snippet.model.ImageUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.snippet.snippet.view.MainWindow_Activity.PERMISSION_CAMERA;

public class UntaggedPhotosActivity extends AppCompatActivity {

    public static final String pathsExtraKey = "snippet/paths";

    private ArrayList<String> paths;

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
        ArrayList<Bitmap> resourceLocations = new ArrayList<>();
        ArrayList<Integer> resourceIds = new ArrayList<>();
        untaggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, resourceIds, this.getApplicationContext()));
        paths = getIntent().getStringArrayListExtra(pathsExtraKey);
        ArrayList<Bitmap> bitmaps = ImageUtils.getImagesBitmap(paths, Math.min(50, paths.size()));
        // TODO again using all 0s for image ids until we actually know what to use
        resourceIds = new ArrayList<>(bitmaps.size());
        for (int i = 0; i < bitmaps.size(); i++) {
            resourceIds.add(0);
        }
        ((PhotosRecyclerViewAdapter) untaggedPhotosRecyclerView.getAdapter()).addImage(bitmaps, resourceIds);

        // TODO camera logic copied from MainWindow_Activity which was incomplete at the time
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UntaggedPhotosActivity.this, "Camera Button", Toast.LENGTH_SHORT).show();
                requestCameraPermissions();
            }
        });
    }

    // Copied from MainWindow_Activity
    public void requestCameraPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }
}
