package com.snippet.snippet.model;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.snippet.snippet.controller.PermissionChecker;
import com.snippet.snippet.view.MainWindow_Activity;

import java.util.ArrayList;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageUtils {

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                PathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(PathOfImage);
            }
        }
        return listOfAllImages;
    }

    public static ArrayList<Bitmap> getImagesBitmap(ArrayList<String> imagePaths, int numImages) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        for(int i = 0; i < numImages; i++) {
            bitmaps.add(BitmapFactory.decodeFile(imagePaths.get(i), options));
        }
        Log.d("DEBUG IMAGE SEARCH", "Number of images found: " + imagePaths.size());
        return bitmaps;
    }

}
