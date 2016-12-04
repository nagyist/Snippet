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
import java.util.List;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageUtils {

    public static List<String> getImagesPath(Activity activity) {
        Uri uri;
        List<String> listOfAllImages = new ArrayList<>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        //Ensure permissions are granted
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //get the path for the images database

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            //create a cursor that can iterate through the database of image data
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                PathOfImage = cursor.getString(column_index_data); //get the path from the database entry

                listOfAllImages.add(PathOfImage); //add the path to the list
            }
        }

        return listOfAllImages;
    }

    public static List<Bitmap> getImagesBitmap(List<String> imagePaths, int numImages) {
        List<Bitmap> bitmaps = new ArrayList<>();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        for(int i = 0; i < numImages; i++) {
            bitmaps.add(BitmapFactory.decodeFile(imagePaths.get(i), options));
        }
        return bitmaps;
    }

}
