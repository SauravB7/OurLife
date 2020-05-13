package com.saurav.ourlife.Activities;

import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.saurav.ourlife.Adapters.FullSizeAdapter;
import com.saurav.ourlife.R;

public class FullscreenImageActivity extends Activity {

    ViewPager viewPagerImage;
    String[] images;
    int position;

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

        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, images);
        viewPagerImage.setAdapter(fullSizeAdapter);
        viewPagerImage.setCurrentItem(position,true);

    }
}
