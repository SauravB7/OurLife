package com.saurav.ourlife.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.saurav.ourlife.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

public class Utils {
    private static final String TAG = "Helper";
    private static final int PERMISSIONS_CODE = 100;
    private static final String[] PERMISSIONS_ALL = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public static int getPermissionsCode() {
        return PERMISSIONS_CODE;
    }

    public static String getTAG() {
        return TAG;
    }

    public static String[] getPermissionsAll() {
        return PERMISSIONS_ALL;
    }

    public static String getConfigValue(Context context, String name) {
        Resources res = context.getResources();
        try {
            InputStream rawResource = res.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);

        }
        catch (IOException e) {
            Log.e(getTAG(), "Failed to open config file.");
        }
        catch (Resources.NotFoundException e) {
            Log.e(getTAG(), "Unable to find the config file: " + e.getMessage());
        }
        return null;
    }

    public static boolean hasPermission(Context context, String[] permissions) {
        if(context != null && permissions != null) {
            for(String permission: permissions) {
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getPath(Uri uri, Context context) {
        String imagePath = null;
        String docId = DocumentsContract.getDocumentId(uri);
        String mediaId = docId.split(":")[1];
        String[] projection = { Media.DATA };

        @SuppressLint("Recycle")
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
                projection,
                Media._ID + "=?",
                new String[] {mediaId},
                null);

        if((cursor != null) && cursor.moveToFirst()) {
            imagePath = cursor.getString(cursor.getColumnIndex(projection[0]));
        }

        return imagePath;
    }

    public static void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    public static void showSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    public static String getCurrentDateAsString (String option) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        switch (option) {
            case "DAY":
                return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            case "MONTH":
                return new SimpleDateFormat("MMMM").format(calendar.getTime());
                
            case "YEAR":
                return String.valueOf(calendar.get(calendar.YEAR));

            default:
                return new SimpleDateFormat("dd MMMM, YYYY").format(calendar.getTime());
        }
    }

    public static String splitS3Keys(String prefix) {
        String[] str = prefix.replaceAll("/+$", "").split("/");

        return str[str.length - 1].replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    }
}