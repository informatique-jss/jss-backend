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
                    String smallestNonNullImageUrl = ""; // At least the Full size is present
                    if (sizes.getFull() != null) {
                        String newUrl = sizes.getFull().getSource_url().replace(wordpressMediaBaseUrl,
                                apacheMediaBaseUrl);
                        media.setUrlFull(newUrl);
                        smallestNonNullImageUrl = newUrl;
                    }
                    if (sizes.getLarge() != null) {
                        String newUrl = sizes.getLarge().getSource_url().replace(wordpressMediaBaseUrl,
                                apacheMediaBaseUrl);
                        media.setUrlLarge(newUrl);
                        smallestNonNullImageUrl = newUrl;
                    } else {
                        media.setUrlLarge(smallestNonNullImageUrl);
                    }
                    if (sizes.getMedium() != null) {
                        String newUrl = sizes.getMedium().getSource_url().replace(wordpressMediaBaseUrl,
                                apacheMediaBaseUrl);
                        media.setUrlMedium(newUrl);
                        smallestNonNullImageUrl = newUrl;
                    } else {
                        media.setUrlMedium(smallestNonNullImageUrl);
                    }
                    if (sizes.getMedium_large() != null) {
                        String newUrl = sizes.getMedium_large().getSource_url().replace(wordpressMediaBaseUrl,
                                apacheMediaBaseUrl);
                        media.setUrlMediumLarge(newUrl);
                    } else {
                        media.setUrlMediumLarge(smallestNonNullImageUrl);
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
            if (media.getCaption() != null && media.getCaption().getRendered() != null)
                media.setCaptionText(media.getCaption().getRendered().replaceAll("<[^>]*>", ""));
        }
        return media;
    }

    public Media getMediaByUrl(String url) {
        return mediaRepository.findByUrlFull(url);
    }
}
