package com.saurav.ourlife.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saurav.ourlife.Helper.AWSS3Helper;
import com.saurav.ourlife.Helper.GlideURLCustomCacheKey;
import com.saurav.ourlife.Helper.Utils;
import com.saurav.ourlife.Interfaces.ImageItemListener;
import com.saurav.ourlife.R;

import java.net.URISyntaxException;

public class FullSizeAdapter extends PagerAdapter {

    private Context context;
    private String[] images;
    private LayoutInflater layoutInflater;
    private boolean isFullScreen = false;
    private ImageItemListener itemListener;

    public FullSizeAdapter(Context context, String[] images, ImageItemListener itemListener) {
        this.context = context;
        this.images = images;
        this.itemListener = itemListener;
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
        View v = layoutInflater.inflate(R.layout.item_full_image, null);

        ImageView fullImageView = v.findViewById(R.id.fullImage);
        final FloatingActionButton downloadImage_fab = v.findViewById(R.id.downloadImage_fab);
        final ProgressBar imageProgress = v.findViewById(R.id.imageProgress);

        Glide.with(context).load(new GlideURLCustomCacheKey(images[position]))
                .apply(new RequestOptions().centerInside())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(fullImageView);

        final ViewPager vp = (ViewPager)container;
        downloadImage_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.hasPermission(context, Utils.getPermissionsAll())) {
                    try {
                        AWSS3Helper.downloadFile(images[position], context);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    itemListener.setPosition(position);
                }
            }
        });
        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    Utils.hideSystemUI(v);
                    downloadImage_fab.setVisibility(View.GONE);
                    isFullScreen = true;
                } else {
                    Utils.showSystemUI(v);
                    downloadImage_fab.setVisibility(View.VISIBLE);
                    isFullScreen = false;
                }
            }
        });

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
