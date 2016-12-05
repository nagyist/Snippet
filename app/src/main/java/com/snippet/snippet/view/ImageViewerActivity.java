package com.snippet.snippet.view;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.snippet.snippet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String FILEID_EXTRA_KEY = "snippet/fileid";
    public static final String BITMAP_EXTRA_KEY = "snippet/bitmap";

    @BindView(R.id.bigImageView) ImageView mImageView;
    @BindView(R.id.addManageTagsBtn) Button tagsButton;

    private int mImageId;

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

        mImageId = getIntent().getIntExtra(FILEID_EXTRA_KEY, -1);
        Bitmap bmp = getIntent().getParcelableExtra(BITMAP_EXTRA_KEY);
        mImageView.setImageBitmap(bmp);

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
