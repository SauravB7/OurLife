package com.saurav.ourlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.s3.AmazonS3;
import com.saurav.ourlife.Adapters.GalleryImageAdapter;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Interfaces.IRecyclerViewClickListener;
import com.saurav.ourlife.R;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

public class GalleryActivity extends AppCompatActivity {

    protected RecyclerView galleryRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    private static final int PERMISSIONS_CODE = 100;
    private static final String[] PERMISSIONS_ALL = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    AmazonS3 S3CLIENT;
    AWSS3Helper S3Helper;
    String BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        if(Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        galleryRecyclerView = findViewById(R.id.galleryRecyclerView);
        layoutManager = new GridLayoutManager(this, 3);
        galleryRecyclerView.setHasFixedSize(true);
        galleryRecyclerView.setLayoutManager(layoutManager);

        if(!GenericHelper.hasPermission(this, PERMISSIONS_ALL)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, PERMISSIONS_CODE);
        } else {
            getConfigValues(this);
            S3Helper = new AWSS3Helper(BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET, GalleryActivity.this);
            S3CLIENT = S3Helper.getS3CLIENT();

            final String[] images = S3Helper.listFileURLs("testAlbum").toArray(new String[0]);
            createGallery(images);
        }
    }

    private void createGallery(final String[] images) {
        final IRecyclerViewClickListener listener = new IRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), FullscreenImageActivity.class);
                i.putExtra("IMAGES", images);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        };

        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this, images, listener);
        galleryRecyclerView.setAdapter(galleryImageAdapter);
    }

    private void getConfigValues(Context context) {
        BUCKET_NAME = GenericHelper.getConfigValue(context, "s3.bucketName");
        ACCESS_KEY = GenericHelper.getConfigValue(context, "s3.accessKey");
        ACCESS_SECRET = GenericHelper.getConfigValue(context, "s3.accessSecret");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getConfigValues(this);
                S3Helper = new AWSS3Helper(BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET, GalleryActivity.this);
                S3CLIENT = S3Helper.getS3CLIENT();

                final String[] images = S3Helper.listFileURLs("testAlbum").toArray(new String[0]);
                createGallery(images);

            } else {
                //TODO: show that no permission given, close and open again to give permission

            }
        }
    }
}