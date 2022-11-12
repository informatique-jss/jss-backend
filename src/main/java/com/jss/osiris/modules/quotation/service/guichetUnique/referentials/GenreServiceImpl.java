package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Genre;
import com.jss.osiris.modules.quotation.repository.guichetUnique.GenreRepository;

@Service
public class GenreServiceImpl implements GenreService {

    @Autowired
    GenreRepository GenreRepository;

    @Override
    @Cacheable(value = "genreList", key = "#root.methodName")
    public List<Genre> getGenre() {
        return IterableUtils.toList(GenreRepository.findAll());
    }
}
