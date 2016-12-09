package com.snippet.snippet.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String FILEPATH_EXTRA_KEY = "snippet/file_path";
    public static final String BITMAP_EXTRA_KEY = "snippet/bitmap";
    public static final int MAX_RESOLUTION = 4096;

    @BindView(R.id.bigImageView) ImageView mImageView;
    @BindView(R.id.addManageTagsBtn) Button tagsButton;

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

        // TODO placeholder
        tagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageViewerActivity.this, "Tags Button", Toast.LENGTH_SHORT).show();
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
}
