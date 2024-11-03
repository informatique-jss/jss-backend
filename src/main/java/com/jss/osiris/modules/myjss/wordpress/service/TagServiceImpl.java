package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;

    @Override
    public Tag getTag(Integer id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isPresent())
            return tag.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag addOrUpdateTagFromWordpress(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAvailableTags() {
        return IterableUtils.toList(tagRepository.findAll());
    }

    @Override
    public Tag getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }
}
