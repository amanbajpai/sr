package com.ros.smartrocket.ui.gallery.model;

public class BucketInfo {
    private String name;
    private String firstImageContainedPath;
    private int photoCountByAlbum;
    private long bucketId;
    private String date;

    public BucketInfo(String name, String firstImageContainedPath, int photoCountByAlbum) {
        this.name = name;
        this.firstImageContainedPath = firstImageContainedPath;
        this.photoCountByAlbum = photoCountByAlbum;
    }

    public BucketInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImageContainedPath() {
        return firstImageContainedPath;
    }

    public void setFirstImageContainedPath(String firstImageContainedPath) {
        this.firstImageContainedPath = firstImageContainedPath;
    }

    public int getMediaCount() {
        return photoCountByAlbum;
    }

    public void setMediaCount(int photoCountByAlbum) {
        this.photoCountByAlbum = photoCountByAlbum;
    }

    public int getPhotoCountByAlbum() {
        return photoCountByAlbum;
    }

    public void setPhotoCountByAlbum(int photoCountByAlbum) {
        this.photoCountByAlbum = photoCountByAlbum;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public void setDateTaken(String date) {
        this.date = date;
    }
}
