package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Media;
import com.jss.osiris.modules.myjss.wordpress.model.MediaSizes;
import com.jss.osiris.modules.myjss.wordpress.repository.MediaRepository;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    AuthorService authorService;

    @Value("${apache.media.base.url}")
    private String apacheMediaBaseUrl;

    @Value("${wordpress.media.base.url}")
    private String wordpressMediaBaseUrl;

    @Autowired
    MediaRepository mediaRepository;

    @Override
    public Media getMedia(Integer id) {
        Optional<Media> media = mediaRepository.findById(id);
        if (media.isPresent())
            return media.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Media addOrUpdateMediaFromWordpress(Media media) {
        if (media.getMedia_details() != null) {
            media.setFile(media.getMedia_details().getFile());
        }
        return mediaRepository.save(completeMedia(media));
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
            } else {
                media.setUrlFull(media.getSource_url());
            }
            if (media.getMedia_details() != null)
                media.setLength(media.getMedia_details().getLength());
            if (media.getAuthor() != null && media.getAuthor() > 0)
                media.setFullAuthor(authorService.getAuthor(media.getAuthor()));
        }
        return media;
    }

    public Media getMediaByUrl(String url) {
        return mediaRepository.findByUrlFull(url);
    }
}
