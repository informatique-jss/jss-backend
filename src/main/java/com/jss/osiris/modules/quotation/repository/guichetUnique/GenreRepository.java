package com.jss.osiris.modules.quotation.repository.guichetUnique;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Genre;

public interface GenreRepository extends CrudRepository<Genre, String> {
}
