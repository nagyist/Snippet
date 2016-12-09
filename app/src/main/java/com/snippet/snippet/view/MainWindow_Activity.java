package com.snippet.snippet.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.controller.ImageUtils;
import com.snippet.snippet.controller.adapters.PhotosRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWindow_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_UNTAGGEDPHOTOS = "UNTAGGED";
    private static final String TAG_TAGGEDPHOTOS = "TAGGED";
    private static final String TAG_HIDDENPHOTOS = "HIDDEN";
    public static final int PERMISSION_CAMERA = 1002;
    private String currentPhotoPath = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;
    @BindView(R.id.untaggedPhotosLayout) LinearLayout untaggedPhotosLayout;
    @BindView(R.id.taggedPhotosLabel) TextView taggedPhotosLabel;
    @BindView(R.id.taggedPhotosDivider) View taggedPhotosDivider;
    @BindView(R.id.taggedPhotosRecyclerView) RecyclerView taggedPhotosRecyclerView;
    @BindView(R.id.progressBar) ContentLoadingProgressBar progressBar;
    @BindView(R.id.untaggedPhotosButton) Button untaggedPhotosButton;
    @BindView(R.id.mainSearchBar) EditText searchBar;
    @BindView(R.id.mainSearchButton) Button searchButton;

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
                requestCameraPermissions();
                dispatchTakePictureIntent();
            }
        });

        //THIS MUST BE CALLED
        DatabaseUtils.createDatabaseTables(this);

        //TODO remove this for final deliverable as this is only for debugging purposes
//        DatabaseUtils.removeAllTables(this);

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
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainWindow_Activity.this.doSearch(searchBar.getText().toString());
            }
        });

        new AsyncImageLogicPaths().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            new TakePictureUpdateViews().execute(currentPhotoPath);
        }
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
        }
        else if (id == R.id.nav_untagged_photos) {
            Intent intent = new Intent(this, UntaggedPhotosActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            Toast.makeText(this, "Nothing here yet, Boss!", Toast.LENGTH_SHORT).show();
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

    private void doSearch(String searched) {
        List<String> paths;
        if (! (searched.equals(""))) {
            untaggedPhotosLayout.setVisibility(View.GONE);
            taggedPhotosLabel.setVisibility(View.GONE);
            taggedPhotosDivider.setVisibility(View.GONE);
            String[] terms = searched.split("\\s+"); // regex for platform-independent space
            paths = DatabaseUtils.getImagePathsWithTag(this, Arrays.asList(terms));
        } else {
            untaggedPhotosLayout.setVisibility(View.VISIBLE);
            taggedPhotosLabel.setVisibility(View.VISIBLE);
            taggedPhotosDivider.setVisibility(View.VISIBLE);
            paths = DatabaseUtils.getImagePathsWithTags(this);
        }
        ((PhotosRecyclerViewAdapter) taggedPhotosRecyclerView.getAdapter()).replaceDataset(paths);
    }

    private List<String> getImagePaths() {
        List<String> paths = DatabaseUtils.getAllFilePaths(this);
        if(paths.size() == 0) {
            paths = ImageUtils.getImagesPath(this);
            DatabaseUtils.addFilePathsToDB(this, paths);
        }

        return paths;
    }

    protected class AsyncImageLogicPaths extends AsyncTask<String, Integer, List<Pair<String, List<String>>>> {

        @Override
        protected List<Pair<String, List<String>>> doInBackground(String... params) {
            getImagePaths();
            // TODO hack hard coding the various recycler view tags into the async call.
            // So change it when the hidden one exists (if ever)
            Pair<String, List<String>> untagged = new Pair<>(TAG_UNTAGGEDPHOTOS, DatabaseUtils.getImagePathsWithoutTags(MainWindow_Activity.this));
            Pair<String, List<String>> tagged = new Pair<>(TAG_TAGGEDPHOTOS, DatabaseUtils.getImagePathsWithTags(MainWindow_Activity.this));
            List<Pair<String, List<String>>> results = new ArrayList<>();
            results.add(untagged);
            results.add(tagged);
            return results;
        }

        @Override
        protected void onPostExecute(List<Pair<String, List<String>>> result) {
            for (Pair<String, List<String>> pair : result) {
                List<String> paths = pair.second; // default value
                // TODO recycler view tags hardcoded in here as well
                switch(pair.first) {
                    case TAG_UNTAGGEDPHOTOS:
                        paths = DatabaseUtils.getImagePathsWithoutTags(MainWindow_Activity.this);
                        break;
                    case TAG_TAGGEDPHOTOS:
                        paths = DatabaseUtils.getImagePathsWithTags(MainWindow_Activity.this);
                        break;
                }
                updateRecyclerView(pair.first, paths);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle savedInstanceState = new Bundle();

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
            updateRecyclerView(TAG_UNTAGGEDPHOTOS, result);
        }
    }

    @Override
    protected void onStart() {
        new AsyncImageLogicPaths().execute();
        super.onStart();
    }
}
