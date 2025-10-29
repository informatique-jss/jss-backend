package com.jss.osiris.modules.myjss.wordpress.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.EmptyStringAsEmptyListDeserializer;

public class AcfPost {

    private boolean premium;

    private Integer premium_percentage;

    private boolean sticky;

    private String seo_title;

    private String seo_description;

    private String applePodcastLinkUrl;

    private String spotifyLinkUrl;

    private String deezerLinkUrl;

    private String amazonMusicLinkUrl;

    @JsonDeserialize(using = EmptyStringAsEmptyListDeserializer.class)
    private List<Integer> associated_post;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public Integer getPremium_percentage() {
        return premium_percentage;
    }

    public void setPremium_percentage(Integer premium_percentage) {
        this.premium_percentage = premium_percentage;
    }

    public List<Integer> getAssociated_post() {
        return associated_post;
    }

    public void setAssociated_post(List<Integer> associated_post) {
        this.associated_post = associated_post;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getSeo_title() {
        return seo_title;
    }

    public void setSeo_title(String seo_title) {
        this.seo_title = seo_title;
    }

    public String getSeo_description() {
        return seo_description;
    }

    public void setSeo_description(String seo_description) {
        this.seo_description = seo_description;
    }

    public String getApplePodcastLinkUrl() {
        return applePodcastLinkUrl;
    }

    public void setApplePodcastLinkUrl(String applePodcastLinkUrl) {
        this.applePodcastLinkUrl = applePodcastLinkUrl;
    }

    public String getSpotifyLinkUrl() {
        return spotifyLinkUrl;
    }

    public void setSpotifyLinkUrl(String spotifyLinkUrl) {
        this.spotifyLinkUrl = spotifyLinkUrl;
    }

    public String getDeezerLinkUrl() {
        return deezerLinkUrl;
    }

    public void setDeezerLinkUrl(String deezerLinkUrl) {
        this.deezerLinkUrl = deezerLinkUrl;
    }

    public String getAmazonMusicLinkUrl() {
        return amazonMusicLinkUrl;
    }

    public void setAmazonMusicLinkUrl(String amazonMusicLinkUrl) {
        this.amazonMusicLinkUrl = amazonMusicLinkUrl;
    }
}
