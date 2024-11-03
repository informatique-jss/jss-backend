package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface TagService {
        public Tag getTag(Integer id);

        public Tag addOrUpdateTagFromWordpress(Tag tag);

        public List<Tag> getAvailableTags();

        public Tag getTagBySlug(String slug);
}
