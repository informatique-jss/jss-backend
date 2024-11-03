package com.jss.osiris.modules.myjss.wordpress.model;

public class MediaDetails {
    private String file;
    private MediaSizes sizes;
    private Integer length;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public MediaSizes getSizes() {
        return sizes;
    }

    public void setSizes(MediaSizes sizes) {
        this.sizes = sizes;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

}
