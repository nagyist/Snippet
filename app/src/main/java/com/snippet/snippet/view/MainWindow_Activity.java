package com.snippet.snippet.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.PermissionChecker;
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;
import com.snippet.snippet.model.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWindow_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_UNTAGGEDPHOTOS = "UNTAGGED";
    private static final String TAG_TAGGEDPHOTOS = "TAGGED";
    private static final String TAG_HIDDENPHOTOS = "HIDDEN";
    public static final int PERMISSION_CAMERA = 1002;

    private List<String> paths;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;
    @BindView(R.id.taggedPhotosRecyclerView) RecyclerView taggedPhotosRecyclerView;
    @BindView(R.id.progressBar) ContentLoadingProgressBar progressBar;
    @BindView(R.id.untaggedPhotosButton) Button untaggedPhotosButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window_);
        /*USE THE BINDVIEW ANNOTATION INSTEAD OF FIND VIEW BY ID. THIS WILL MAKE OUR CODE CLEANER
        * THANKS TO BUTTERKNIFE*/
        ButterKnife.bind(this); //This allows you to use the bindings you made above
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainWindow_Activity.this, "Camera Button", Toast.LENGTH_SHORT).show();
                requestCameraPermissions();
            }
        });

        paths = null;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Set the Layout Managers
        untaggedPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taggedPhotosRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));

        //Set the RecyclerView Adapters
//        ArrayList<Bitmap> resourceLocations = new ArrayList<>();
        ArrayList<Bitmap> resourceLocations = new ArrayList<>();
        untaggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));
        taggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));

        progressBar.setVisibility(View.VISIBLE);
        progressBar.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new AsyncImageLogic().execute(TAG_UNTAGGEDPHOTOS);
                new AsyncImageLogic().execute(TAG_TAGGEDPHOTOS);
            }
        });
        thread.run();

        untaggedPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainWindow_Activity.this.getBaseContext(), UntaggedPhotosActivity.class);
                intent.putExtra(UntaggedPhotosActivity.pathsExtraKey, paths);
                startActivity(intent);
            }
        });
    }

    public void requestCameraPermissions() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Temporary proof of concept code to show the images in the recyclerview
     * @param TAG The tag to indicate which photos should be placed in the view
     * @return an arraylist of resource locations
     */
    public List<Bitmap> fillWithBitmaps(String TAG) {
        List<Bitmap> bitmaps = new ArrayList<>();
        if(paths == null) {
            paths = ImageUtils.getImagesPath(this);
        }
        switch(TAG) {
            case TAG_UNTAGGEDPHOTOS:
                bitmaps = ImageUtils.getImagesBitmap(paths, Math.min(50, paths.size()));
                break;
            case TAG_TAGGEDPHOTOS:
                bitmaps = ImageUtils.getImagesBitmap(paths, Math.min(150, paths.size()));
                break;
            case TAG_HIDDENPHOTOS:
                break;
        }

        return bitmaps;
    }

    /* ATTEMPTING TO DO ASYNCHRONOUS IMAGE FETCHING. ISSUES WITH UPDATING UI NOT ON ORIGINAL THREAD */
    public void updateRecyclerView(String TAG, List<Bitmap> imagesToAdd) {
        switch(TAG) {
            case TAG_UNTAGGEDPHOTOS:
                ((PhotosRecyclerViewAdapter) untaggedPhotosRecyclerView.getAdapter()).addImage(imagesToAdd);
                break;
            case TAG_TAGGEDPHOTOS:
                ((PhotosRecyclerViewAdapter) taggedPhotosRecyclerView.getAdapter()).addImage(imagesToAdd);
                progressBar.hide();
                progressBar.setVisibility(View.GONE);
                break;
            case TAG_HIDDENPHOTOS:
                break;
        }
    }

    protected class AsyncImageLogic extends AsyncTask<String, Integer, Pair<String, List<Bitmap>>> {

        @Override
        protected Pair<String, List<Bitmap>> doInBackground(String... params) {
            List<Bitmap> bitmaps = new ArrayList<>();
            for (int i = 0; i < params.length; i++) {
                bitmaps = fillWithBitmaps(params[i]);
            }

            return new Pair<>(params[0], bitmaps);
        }

        @Override
        protected void onPostExecute(Pair<String, List<Bitmap>> result) {
            updateRecyclerView(result.first, result.second);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle savedInstanceState = new Bundle();

    }
}
