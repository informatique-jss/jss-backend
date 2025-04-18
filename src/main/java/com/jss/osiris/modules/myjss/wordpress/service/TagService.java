package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface TagService {
        public Tag getTag(Integer id);

        public Tag addOrUpdateTagFromWordpress(Tag tag);

        public List<Tag> getAvailableTags();

        public Tag getTagBySlug(String slug);

        public List<Tag> getAllTagsByJssCategory(JssCategory jssCategory);

        public List<Tag> getAllTagsByTag(Tag tag);

        public List<Tag> getAllTagsByAuthor(Author author);

        public List<Tag> getAllTagsBySerie(Serie serie);
}
