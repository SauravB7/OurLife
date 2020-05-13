package com.saurav.ourlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Helper.GenericHelper;
import com.saurav.ourlife.R;

import java.net.URISyntaxException;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    String[] images;
    LayoutInflater layoutInflater;
    boolean isFullScreen = false;

    public FullSizeAdapter(Context context, String[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = layoutInflater.inflate(R.layout.full_image_item, null);

        ImageView fullImageView = v.findViewById(R.id.fullImage);
        final FloatingActionButton downloadImage_fab = v.findViewById(R.id.downloadImage_fab);
        Glide.with(context).load(images[position]).apply(new RequestOptions().centerInside())
                .into(fullImageView);
        downloadImage_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AWSS3Helper.downloadFile(images[position]);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    isFullScreen = true;
                    GenericHelper.hideSystemUI(v);
                    downloadImage_fab.setVisibility(View.GONE);
                } else {
                    isFullScreen = false;
                    GenericHelper.showSystemUI(v);
                    downloadImage_fab.setVisibility(View.VISIBLE);
                }
            }
        });

        ViewPager vp = (ViewPager)container;
        vp.addView(v,0);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager vp = (ViewPager)container;
        View v = (View)object;
        vp.removeView(v);
    }
}
