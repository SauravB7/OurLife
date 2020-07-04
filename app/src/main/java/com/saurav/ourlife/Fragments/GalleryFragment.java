package com.saurav.ourlife.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saurav.ourlife.Activities.FullscreenImageActivity;
import com.saurav.ourlife.Adapters.GalleryImageAdapter;
import com.saurav.ourlife.Helper.FragmentHelper;
import com.saurav.ourlife.Interfaces.IRecyclerViewClickListener;
import com.saurav.ourlife.R;

public class GalleryFragment extends Fragment {

    protected RecyclerView galleryRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    View view;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        String[] images = getArguments().getStringArray("imagesURL");

        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 4);
        galleryRecyclerView.setHasFixedSize(true);
        galleryRecyclerView.setLayoutManager(layoutManager);

        FragmentHelper.updateToolbarBG(getActivity(), R.color.colorSurface);

        createGallery(images);

        return view;
    }

    public void createGallery(final String[] images) {
        final IRecyclerViewClickListener listener = new IRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getActivity().getApplicationContext(), FullscreenImageActivity.class);
                i.putExtra("IMAGES", images);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        };

        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(getContext(), images, listener);
        galleryRecyclerView.setAdapter(galleryImageAdapter);
    }
}
