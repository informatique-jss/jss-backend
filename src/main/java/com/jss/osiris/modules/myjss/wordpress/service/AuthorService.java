package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Author;

public interface AuthorService {
        public Author getAuthor(Integer id);

        public Author addOrUpdateAuthor(Author author);

        public Author getAuthorBySlug(String slug);

        public List<Author> getFollowedAuthorsForCurrentUser();

        public List<Author> getAllAuthors();
}
