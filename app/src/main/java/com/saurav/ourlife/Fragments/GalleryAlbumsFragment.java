package com.saurav.ourlife.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.saurav.ourlife.Activities.HomeActivity;
import com.saurav.ourlife.Adapters.GalleryFolderAdapter;
import com.saurav.ourlife.DataClass.Album;
import com.saurav.ourlife.Helper.CreateAlbums;
import com.saurav.ourlife.Helper.FragmentHelper;
import com.saurav.ourlife.Interfaces.AlbumsRVClickListener;
import com.saurav.ourlife.Interfaces.GalleryRVClickListener;
import com.saurav.ourlife.R;

public class GalleryAlbumsFragment extends Fragment {

    protected RecyclerView albumsRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    ProgressBar albumsProgBar;
    View view;
    Album[] albums;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery_albums, container, false);
        String memoriesCategory = getArguments().getString("memoriesCategory");

        albumsRecyclerView = view.findViewById(R.id.galleryAlbums);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        albumsRecyclerView.setHasFixedSize(true);
        albumsRecyclerView.setLayoutManager(layoutManager);
        albumsProgBar = view.findViewById(R.id.albumsProgBar);

        FragmentHelper.updateToolbarBG(getActivity(), R.color.colorSurface);

        createAlbums(memoriesCategory);

        return view;
    }

    private void createAlbums(String memoriesCategory) {
        CreateAlbums createAlbumsTask = new CreateAlbums(getActivity().getApplicationContext(), albumsProgBar, this);
        createAlbumsTask.execute(memoriesCategory);
    }

    public void showAlbums(final Album[] albums) {

        final AlbumsRVClickListener listener = new AlbumsRVClickListener() {
            @Override
            public void onClick(View view, Album album) {
                GalleryFragment galleryFragment = new GalleryFragment();
                Bundle data = new Bundle();
                data.putStringArray("imagesURL", album.getImagePaths());
                galleryFragment.setArguments(data);

                ((HomeActivity) getActivity()).loadFragment(galleryFragment, album.getFolderName());
            }
        };

        GalleryFolderAdapter galleryFolderAdapter = new GalleryFolderAdapter(getContext(), albums, listener);
        albumsRecyclerView.setAdapter(galleryFolderAdapter);
    }
}