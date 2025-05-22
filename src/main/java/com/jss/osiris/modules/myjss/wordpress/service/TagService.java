package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface TagService {
        public Tag getTag(Integer id);

        public Tag addOrUpdateTagFromWordpress(Tag tag);

        public List<Tag> getAvailableTags();

        public Tag getTagBySlug(String slug);

        public List<Tag> getAllTagsByJssCategory(JssCategory jssCategory);

        public List<Tag> getAllTagsByTag(Tag tag) throws OsirisException;

        public List<Tag> getAllTagsByAuthor(Author author);

        public List<Tag> getAllTagsBySerie(Serie serie);

        public List<Tag> getAllTagsByPublishingDepartment(PublishingDepartment publishingDepartment)
                        throws OsirisException;

        public List<Tag> getAllTagsByIdf() throws OsirisException;

        public List<Tag> getAllTendencyTags() throws OsirisException;

}
