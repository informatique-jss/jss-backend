package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Tag;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Override
    @Cacheable(value = "wordpress-tags")
    public List<Tag> getAvailableTags() {
        List<Tag> categories = wordpressDelegate.getAvailableTags();
        return categories;
    }
}
