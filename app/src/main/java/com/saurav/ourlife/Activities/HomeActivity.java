package com.saurav.ourlife.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.saurav.ourlife.Fragments.DashboardFragment;
import com.saurav.ourlife.Fragments.GalleryFragment;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.R;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variable
    public Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarToggle;
    ChipNavigationBar bottomNavBar;

    AmazonS3 S3CLIENT;
    AWSS3Helper S3Helper;
    String BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        // Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        bottomNavBar = findViewById(R.id.nav_bottom);

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Nav bar
        bottomNavBar.setItemSelected(R.id.home, true);

        initApp();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        actionBarToggle.syncState();
    }

    private void initApp() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Nav Drawer Menu
        navigationView.bringToFront();
        actionBarToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarToggle);
        navigationView.setNavigationItemSelectedListener(this);

        //Home Dashboard Fragment
        loadFragment(new DashboardFragment(), "DASHBOARD");

        //onFragmentBackStackChange
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    String tag = getSupportFragmentManager().findFragmentById(R.id.home_frame).getTag();
                    switch (tag) {
                        case "DASHBOARD":
                            navigationView.setCheckedItem(R.id.nav_home);
                            bottomNavBar.setItemSelected(R.id.home, true);
                            break;

                        case "GALLERY":
                            bottomNavBar.setItemSelected(R.id.favorites, true);
                            break;
                    }
                }
            }
        });

        //initialize S3 instance
        initiateS3();
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
                loadFragment(new DashboardFragment(), "DASHBOARD");
                bottomNavBar.setItemSelected(R.id.home, true);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GenericHelper.PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGallery();
            } else {
                //TODO: show that no permission given, close and open again to give permission
            }
        }
    }

    private void getConfigValues(Context context) {
        BUCKET_NAME = GenericHelper.getConfigValue(context, "s3.bucketName");
        ACCESS_KEY = GenericHelper.getConfigValue(context, "s3.accessKey");
        ACCESS_SECRET = GenericHelper.getConfigValue(context, "s3.accessSecret");
    }

    private void initiateS3() {
        getConfigValues(this);
        S3Helper = new AWSS3Helper(BUCKET_NAME, ACCESS_KEY, ACCESS_SECRET, this);
        S3CLIENT = S3Helper.getS3CLIENT();
    }

    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void initGallery() {
        final String[] images = S3Helper.listFileURLs("testAlbum").toArray(new String[0]);
        Bundle bundle = new Bundle();
        bundle.putStringArray("imagesURL", images);
        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);
        loadFragment(galleryFragment, "GALLERY");

        bottomNavBar.setItemSelected(R.id.home, true);
    }
}