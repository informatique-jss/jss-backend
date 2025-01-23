package com.jss.osiris.modules.myjss.wordpress.service;

import com.jss.osiris.modules.myjss.wordpress.model.Media;

public interface MediaService {
        public Media getMedia(Integer id);

        public Media addOrUpdateMediaFromWordpress(Media media);

        public Media getMediaByUrl(String url);
}
