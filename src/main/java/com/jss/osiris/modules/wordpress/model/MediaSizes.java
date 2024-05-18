package com.jss.osiris.modules.wordpress.model;

public class MediaSizes {
    private String file;
    private MediaSize full;
    private MediaSize large;
    private MediaSize medium;
    private MediaSize medium_large;
    private MediaSize thumbnail;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public MediaSize getFull() {
        return full;
    }

    public void setFull(MediaSize full) {
        this.full = full;
    }

    public MediaSize getLarge() {
        return large;
    }

    public void setLarge(MediaSize large) {
        this.large = large;
    }

    public MediaSize getMedium() {
        return medium;
    }

    public void setMedium(MediaSize medium) {
        this.medium = medium;
    }

    public MediaSize getMedium_large() {
        return medium_large;
    }

    public void setMedium_large(MediaSize medium_large) {
        this.medium_large = medium_large;
    }

    public MediaSize getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(MediaSize thumbnail) {
        this.thumbnail = thumbnail;
    }

}
