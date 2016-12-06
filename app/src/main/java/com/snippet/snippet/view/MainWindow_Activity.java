package com.snippet.snippet.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
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
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.adapters.UntaggedPhotosRecyclerViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainWindow_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_UNTAGGEDPHOTOS = "UNTAGGED";
    private static final String TAG_TAGGEDPHOTOS = "TAGGED";
    private static final String TAG_HIDDENPHOTOS = "HIDDEN";

    private Context context;
    private ClarifaiClient client;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.untaggedPhotosRecyclerView) RecyclerView untaggedPhotosRecyclerView;
    @BindView(R.id.taggedPhotosRecyclerView) RecyclerView taggedPhotosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        ArrayList<Integer> resourceLocations = fillWithImages(TAG_UNTAGGEDPHOTOS);
        untaggedPhotosRecyclerView.setAdapter(new UntaggedPhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));
        resourceLocations = fillWithImages(TAG_TAGGEDPHOTOS);
        taggedPhotosRecyclerView.setAdapter(new UntaggedPhotosRecyclerViewAdapter(resourceLocations, this.getApplicationContext()));

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
    public ArrayList<Integer> fillWithImages(String TAG) {
        ArrayList<Integer> resourceLocations = new ArrayList<Integer>();
        switch(TAG) {
            case TAG_UNTAGGEDPHOTOS:
                for(int i = 0; i < 10; i++) {
                    resourceLocations.add(R.mipmap.ic_launcher);
                }
                break;
            case TAG_TAGGEDPHOTOS:
                for(int i = 0; i < 30; i++) {
                    resourceLocations.add(R.mipmap.ic_launcher);
                }
                break;
            case TAG_HIDDENPHOTOS:
                for(int i = 0; i < 10; i++) {
                    resourceLocations.add(R.mipmap.ic_launcher);
                }
                break;
        }

        return resourceLocations;
    }

}
