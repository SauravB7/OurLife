package com.saurav.ourlife.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.saurav.ourlife.Adapters.FullSizeAdapter;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Helper.Utils;
import com.saurav.ourlife.Interfaces.ImageItemListener;
import com.saurav.ourlife.R;

import java.net.URISyntaxException;

public class FullscreenImageActivity extends Activity {

    ViewPager viewPagerImage;
    String[] images;
    int position;
    int downloadPosition;
    public ProgressBar downloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        if(savedInstanceState == null) {
            Intent i = getIntent();
            images = i.getStringArrayExtra("IMAGES");
            position = i.getIntExtra("POSITION", 0);
        }
        viewPagerImage = findViewById(R.id.viewPagerImage);
        downloadProgress = findViewById(R.id.downloadProgress);
        downloadProgress.setVisibility(View.GONE);

        final ImageItemListener itemListener = new ImageItemListener() {
            @Override
            public void setPosition(int position) {
                downloadPosition = position;
                ActivityCompat.requestPermissions(FullscreenImageActivity.this, Utils.getPermissionsAll(), Utils.getPermissionsCode());
            }
        };

        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, images, itemListener);
        viewPagerImage.setAdapter(fullSizeAdapter);
        viewPagerImage.setCurrentItem(position,true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Utils.getPermissionsCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    AWSS3Helper.downloadFile(images[downloadPosition], this);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO: show that no permission given, close and open again to give permission
            }
        }
    }
}
