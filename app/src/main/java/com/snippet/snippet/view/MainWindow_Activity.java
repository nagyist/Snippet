package com.snippet.snippet.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.ImageUtils;
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWindow_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_UNTAGGEDPHOTOS = "UNTAGGED";
    private static final String TAG_TAGGEDPHOTOS = "TAGGED";
    private static final String TAG_HIDDENPHOTOS = "HIDDEN";
    public static final int PERMISSION_CAMERA = 1002;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;
    @BindView(R.id.taggedPhotosRecyclerView) RecyclerView taggedPhotosRecyclerView;
    @BindView(R.id.progressBar) ContentLoadingProgressBar progressBar;
    @BindView(R.id.untaggedPhotosButton) Button untaggedPhotosButton;

    private String tagToSearch = "";

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

        //TODO remove this for final deliverable as this is only for debugging purposes
        DatabaseUtils.removeAllTables(this);

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

        List<String> resourceLocations = new ArrayList<>();
        untaggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));
        taggedPhotosRecyclerView.setAdapter(new PhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));

        progressBar.setVisibility(View.VISIBLE);
        progressBar.show();

        untaggedPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainWindow_Activity.this.getBaseContext(), UntaggedPhotosActivity.class);
                // Pass the data from the untagged photos recycler view as that's the untagged photos
                ArrayList<String> paths = new ArrayList<>(((PhotosRecyclerViewAdapter) untaggedPhotosRecyclerView.getAdapter()).getDataset());
                intent.putExtra(UntaggedPhotosActivity.pathsExtraKey, paths);
                startActivity(intent);
            }
        });

        new AsyncImageLogicPaths().execute();
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

    private void updateRecyclerView(String TAG, List<String> imagesToAdd) {
        switch(TAG) {
            case TAG_UNTAGGEDPHOTOS:
                ((PhotosRecyclerViewAdapter) untaggedPhotosRecyclerView.getAdapter()).replaceDataset(imagesToAdd);
                break;
            case TAG_TAGGEDPHOTOS:
                ((PhotosRecyclerViewAdapter) taggedPhotosRecyclerView.getAdapter()).replaceDataset(imagesToAdd);
                progressBar.hide();
                progressBar.setVisibility(View.GONE);
                break;
            case TAG_HIDDENPHOTOS:
                break;
        }
    }

    private List<String> getImagePaths() {
        List<String> paths = DatabaseUtils.getAllFilePaths(this);
        if(paths.size() == 0) {
            paths = ImageUtils.getImagesPath(this);
            DatabaseUtils.addFilePathsToDB(this, paths);
        }

        return paths;
    }

    protected class AsyncImageLogicPaths extends AsyncTask<String, Integer, Pair<List<String>, List<String>>> {

        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... params) {
            List<String> paths = new ArrayList<>();
//            for (int i = 0; i < params.length; i++) {
//                paths = getImagePaths();
//            }
            /*switch(params[0]) {
                case TAG_UNTAGGEDPHOTOS:
                    paths = DatabaseUtils.getUntaggedImagesFromDB(MainWindow_Activity.this);
                    break;
                case TAG_TAGGEDPHOTOS:
                    paths = getImagePaths(); //DatabaseUtils.getTaggedImagesFromDB(MainWindow_Activity.this);
                    break;
                case TAG_HIDDENPHOTOS:
                    // TODO not implemented yet
                    //paths = DatabaseUtils.getHiddenImagesFromDB(MainWindow_Activity.this);
            }*/
            paths = getImagePaths();
            // TODO hack hard coding the various recycler view tags into the async call.
            // So change it when the hidden one exists (if ever)
            List<String> viewTags = new ArrayList<>();
            viewTags.add(TAG_UNTAGGEDPHOTOS);
            viewTags.add(TAG_TAGGEDPHOTOS);
            return new Pair<>(viewTags, paths);
        }

        @Override
        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            for (String tag : result.first) {
                List<String> paths = result.second; // default value
                // TODO recycler view tags hardcoded in here as well
                switch(tag) {
                    case TAG_UNTAGGEDPHOTOS:
                        paths = DatabaseUtils.getUntaggedImagesFromDB(MainWindow_Activity.this);
                        break;
                    case TAG_TAGGEDPHOTOS:
                        paths = DatabaseUtils.getTaggedImagesFromDB(MainWindow_Activity.this);
                        break;
                }
                updateRecyclerView(tag, paths);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle savedInstanceState = new Bundle();

    }
}
