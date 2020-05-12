package com.saurav.ourlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;

import com.amazonaws.services.s3.AmazonS3;
import com.google.android.material.navigation.NavigationView;
import com.saurav.ourlife.Fragments.GalleryFragment;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.R;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variable
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

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
        setContentView(R.layout.activity_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        if(Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar
        setSupportActionBar(toolbar);

        // Nav Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void getConfigValues(Context context) {
        BUCKET_NAME = GenericHelper.getConfigValue(context, "s3.bucketName");
        ACCESS_KEY = GenericHelper.getConfigValue(context, "s3.accessKey");
        ACCESS_SECRET = GenericHelper.getConfigValue(context, "s3.accessSecret");
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.nav_gallery:
                if(!GenericHelper.hasPermission(this, PERMISSIONS_ALL)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, PERMISSIONS_CODE);
                } else {
                    initiateS3();
                    final String[] images = S3Helper.listFileURLs("testAlbum").toArray(new String[0]);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("imagesURL", images);
                    GalleryFragment galleryFragment = new GalleryFragment();
                    galleryFragment.setArguments(bundle);

                    loadFragment(galleryFragment);
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateS3();
                final String[] images = S3Helper.listFileURLs("testAlbum").toArray(new String[0]);
                Bundle bundle = new Bundle();
                bundle.putStringArray("imagesURL", images);
                GalleryFragment galleryFragment = new GalleryFragment();
                galleryFragment.setArguments(bundle);

                loadFragment(galleryFragment);
            } else {
                //TODO: show that no permission given, close and open again to give permission
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame, fragment);
        transaction.commit();
    }

    private void initiateS3() {
        getConfigValues(this);
        S3Helper = new AWSS3Helper(BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET, this);
        S3CLIENT = S3Helper.getS3CLIENT();
    }
}
