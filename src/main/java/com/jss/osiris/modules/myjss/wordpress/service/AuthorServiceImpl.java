package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.repository.AuthorRepository;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public Author getAuthor(Integer id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent())
            return author.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Author addOrUpdateAuthor(Author author) {
        if (author.getAvatar_urls() != null) {
            author.setAvatar_url_size_24(author.getAvatar_urls().getSize_24());
            author.setAvatar_url_size_48(author.getAvatar_urls().getSize_48());
            author.setAvatar_url_size_96(author.getAvatar_urls().getSize_96());
        }
        return authorRepository.save(author);
    }
}
