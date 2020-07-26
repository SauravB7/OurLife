package com.saurav.ourlife.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.saurav.ourlife.DataClass.Album;
import com.saurav.ourlife.Fragments.GalleryAlbumsFragment;

import java.util.List;

public class CreateAlbums extends AsyncTask<String, Void, Album[]> {

    private final Context context;
    private final ProgressBar progBar;
    private GalleryAlbumsFragment fragment;

    public CreateAlbums(Context context, ProgressBar progBar, GalleryAlbumsFragment fragment) {
        this.context = context;
        this.progBar = progBar;
        this.fragment = fragment;
    }

    @Override
    protected Album[] doInBackground(String... strings) {
        //TODO: call createAlbum task here

        String memoriesCategory = strings[0];
        AWSS3Helper S3Helper = new AWSS3Helper(context);
        Album[] albums = null;

        switch (memoriesCategory) {
            case "Birthdays":
                List<String> folderNames = S3Helper.listFolderNames("Memories/Birthdays");
                albums = generateAlbumsArray(folderNames, S3Helper);
                break;
        }
        return albums;
    }

    @Override
    protected void onPostExecute(Album[] albums) {
        //TODO: remove progress bar here
        fragment.showAlbums(albums);
        progBar.setVisibility(View.GONE);

    }

    private Album[] generateAlbumsArray(List<String> folderNames, AWSS3Helper S3Helper) {
        Album[] albums = new Album[folderNames.size()];

        for(int i = 0; i<folderNames.size(); i++) {
            String folderName = GenericHelper.splitS3Keys(folderNames.get(i));
            String[] imagePaths = S3Helper.listFileURLs(folderNames.get(i)).toArray(new String[0]);
            Album album = new Album(folderName, imagePaths[0], imagePaths.length, imagePaths);

            albums[i] = album;
        }

        return albums;
    }
}
