package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Author;

public interface AuthorRepository extends QueryCacheCrudRepository<Author, Integer> {

    Author findBySlug(String slug);

    @Query("select a from Author a")
    List<Author> findAllAuthors();

}