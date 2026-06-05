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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.saurav.ourlife.Helper.GlideURLCustomCacheKey;
import com.saurav.ourlife.Interfaces.GalleryRVClickListener;
import com.saurav.ourlife.R;

public class GalleryImageAdapter  extends RecyclerView.Adapter<GalleryImageAdapter.ImageViewHolder> {

    Context context;
    String[] urlList;
    GalleryRVClickListener clickListener;

    public GalleryImageAdapter(Context context, String[] urlList, GalleryRVClickListener clickListener) {
        this.context = context;
        this.urlList = urlList;
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String currentImage = urlList[position];
        final ImageView imageView = holder.imageView;
        final ProgressBar progressBar = holder.progressBar;

        Glide.with(context).load(new GlideURLCustomCacheKey(currentImage))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .sizeMultiplier(0.15f)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public int getItemCount() {
        return urlList.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}
