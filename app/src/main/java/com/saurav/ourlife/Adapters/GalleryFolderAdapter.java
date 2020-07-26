package com.saurav.ourlife.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.saurav.ourlife.DataClass.Album;
import com.saurav.ourlife.Interfaces.AlbumsRVClickListener;
import com.saurav.ourlife.R;

public class GalleryFolderAdapter  extends RecyclerView.Adapter<GalleryFolderAdapter.ImageViewHolder> {

    Context context;
    Album[] albums;
    AlbumsRVClickListener clickListener;

    public GalleryFolderAdapter(Context context, Album[] albums, AlbumsRVClickListener clickListener) {
        this.context = context;
        this.albums = albums;
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_album, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final Album currentAlbum = albums[position];
        String currentImage = currentAlbum.getFolderImage();

        final ImageView imageView = holder.imageView;
        final ProgressBar progressBar = holder.progressBar;
        final TextView folderName = holder.folderName;
        final TextView folderImageCount = holder.folderImageCount;

        Glide.with(context).load(currentImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        folderName.setText(currentAlbum.getFolderName());
                        folderImageCount.setText(currentAlbum.getImageCount() + " items");
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public int getItemCount() {
        return albums.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        ProgressBar progressBar;
        TextView folderName, folderImageCount;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.folderImage);
            progressBar = itemView.findViewById(R.id.folderProgBar);
            folderName = itemView.findViewById(R.id.folderName);
            folderImageCount = itemView.findViewById(R.id.folderImageCount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, albums[getAdapterPosition()]);
        }
    }
}
