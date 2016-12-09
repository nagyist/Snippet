package com.snippet.snippet.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.snippet.snippet.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kyle Carrero on 11/30/16.
 */

public class ImageUtils {

    public static final int REQUEST_IMAGE_CAPTURE = 500;
    private static String mCurrentPhotoPath = "";

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

                if(!PathOfImage.contains("Android/data")) { //to avoid images from other apps
                    listOfAllImages.add(PathOfImage); //add the path to the list
                }
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

    /**
     * Adds a file to the given ImageView using the Picasso Library to handle efficient memory usage.
     * @param context The context of the Application
     * @param imageView The ImageView being used
     * @param filePath The File path to the file locally on disk
     * @param resizeWidth The new width you want the image to be to reduce memory costs (NULL IF THERE IS NO CHANGE TO THE IMAGE)
     * @param resizeHeight The new height you want the image to be to reduce momory costs (NULL IF THERE IS NO CHANGE TO THE IMAGE)
     */
    public static void addImageToImageView(Context context, ImageView imageView, String filePath, Integer resizeWidth, Integer resizeHeight) {
//        Log.d("DEBUG File Path", "File Path is: " + filePath);
        if(resizeHeight == null || resizeWidth == null) {
            Picasso.with(context).load(new File(filePath)).placeholder(R.drawable.placeholder).into(imageView);
        }
        else {
            Picasso.with(context).load(new File(filePath)).resize(resizeWidth, resizeHeight).centerCrop().placeholder(R.drawable.placeholder).into(imageView);
        }
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Pictures/Snippet");
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.d("DEBUG File Path", "File Path is: " + image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
