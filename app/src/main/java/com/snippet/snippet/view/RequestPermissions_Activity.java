package com.snippet.snippet.view;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.snippet.snippet.R;
import com.snippet.snippet.controller.PermissionChecker;

public class RequestPermissions_Activity extends AppCompatActivity {

    public static final int PERMISSION_EXTERNAL_STORAGE = 1001;

    boolean storagePermission;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions);

        intent = new Intent(this, MainWindow_Activity.class);

        if(Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                storagePermission = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE);
            }
            else {
                toMainActivity();
            }
        }
        else {
            toMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE: {
                Log.d("Permissions Debugging", "onRequestPermissionsResult: WRITE_EXTERNAL_STORAGE " + (grantResults[0] == PackageManager.PERMISSION_GRANTED));
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    storagePermission = true;
                }
                else {

//                    storagePermission = false;
                }
                break;
            }

//            case PERMISSION_CAMERA: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    cameraPermission = true;
//
//                } else {
//
//                    cameraPermission = false;
//
//                }
//                break;
//            }
        }

        if(storagePermission) {
            toMainActivity();
        }
        else {
            this.finishAffinity();
        }
    }

    private void toMainActivity() {
        startActivity(intent);
    }
}
