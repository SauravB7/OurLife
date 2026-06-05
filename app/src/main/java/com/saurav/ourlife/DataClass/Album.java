package com.saurav.ourlife.DataClass;

import java.util.List;

public class Album {

    private final String folderName;
    private final String folderImage;
    private final int imageCount;
    private final String[] imagePaths;

    public Album(String folderName, String folderImage, int imageCount, String[] imagePaths) {
        this.folderName = folderName;
        this.folderImage = folderImage;
        this.imageCount = imageCount;
        this.imagePaths = imagePaths;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderImage() {
        return folderImage;
    }

    public int getImageCount() {
        return imageCount;
    }

    public String[] getImagePaths() {
        return imagePaths;
    }
}
