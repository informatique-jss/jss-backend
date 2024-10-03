package com.jss.osiris.modules.myjss.wordpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Media;
import com.jss.osiris.modules.myjss.wordpress.model.MediaSizes;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Autowired
    AuthorService authorService;

    @Value("${apache.media.base.url}")
    private String apacheMediaBaseUrl;

    @Value("${wordpress.media.base.url}")
    private String wordpressMediaBaseUrl;

    @Override
    @Cacheable(value = "wordpress-media")
    public Media getMedia(Integer id) {
        return completeMedia(wordpressDelegate.getMedia(id));
    }

    private Media completeMedia(Media media) {
        if (media != null) {
            if (media.getMedia_details() != null && media.getMedia_details().getFile() != null) {
                if (media.getMedia_details().getSizes() != null) {
                    MediaSizes sizes = media.getMedia_details().getSizes();
                    if (sizes.getFull() != null) {
                        media.setUrlFull(
                                sizes.getFull().getSource_url().replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
                    }
                    if (sizes.getLarge() != null) {
                        media.setUrlLarge(
                                sizes.getLarge().getSource_url().replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
                    }
                    if (sizes.getMedium() != null) {
                        media.setUrlMedium(
                                sizes.getMedium().getSource_url().replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
                    }
                    if (sizes.getMedium_large() != null) {
                        media.setUrlMediumLarge(sizes.getMedium_large().getSource_url().replace(wordpressMediaBaseUrl,
                                apacheMediaBaseUrl));
                    }
                    if (sizes.getThumbnail() != null) {
                        media.setUrlThumbnail(
                                sizes.getThumbnail().getSource_url().replace(wordpressMediaBaseUrl,
                                        apacheMediaBaseUrl));
                    }
                }
            }
            if (media.getAuthor() != null && media.getAuthor() > 0)
                media.setFullAuthor(authorService.getAuthor(media.getAuthor()));
        }
        return media;
    }
}
