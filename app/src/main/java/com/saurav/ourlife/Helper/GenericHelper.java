package com.saurav.ourlife.Helper;

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
import java.util.Properties;

public class GenericHelper {
    private static final String TAG = "Helper";

    public static String getConfigValue(Context context, String name) {
        Resources res = context.getResources();
        try {
            InputStream rawResource = res.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);

        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
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
}