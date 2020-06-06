package com.saurav.ourlife.Helper;

import android.app.Activity;

import androidx.appcompat.widget.Toolbar;

import com.saurav.ourlife.Activities.HomeActivity;
import com.saurav.ourlife.R;

public class FragmentHelper {

    public static void updateToolbarBG(Activity activity, int resId) {
        if(activity.getClass().equals(HomeActivity.class)) {
            Toolbar toolbar = ((HomeActivity)activity).toolbar;
            toolbar.setBackgroundResource(resId);
        }
    }
}
