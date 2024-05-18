package com.jss.osiris.modules.wordpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.wordpress.model.Author;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Override
    @Cacheable(value = "wordpress-author")
    public Author getAuthor(Integer id) {
        return wordpressDelegate.getAuthor(id);
    }
}
