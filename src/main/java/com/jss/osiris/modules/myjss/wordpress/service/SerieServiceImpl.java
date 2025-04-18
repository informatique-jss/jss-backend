package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.repository.SerieRepository;

@Service
public class SerieServiceImpl implements SerieService {

    @Autowired
    SerieRepository serieRepository;

    @Autowired
    MediaService mediaService;

    @Override
    public Serie getSerie(Integer id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent())
            return serie.get();
        return null;
    }

    @Override
    public Serie getSerieBySlug(String slug) {
        return serieRepository.findBySlug(slug);
    }

    @Override
    public List<Serie> getAvailableSeries() {
        return IterableUtils.toList(serieRepository.findAll());
    }

    @Override
    public Page<Serie> getSeries(Pageable pageableRequest) {
        return serieRepository.findAll(pageableRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Serie addOrUpdateSerie(Serie serie) {
        if (serie.getAcf() != null) {
            serie.setExcerptText(serie.getAcf().getExcerpt());
            serie.setTitleText(serie.getAcf().getTitle());
            serie.setSerieOrder(serie.getAcf().getOrdre());
            if (serie.getAcf().getPicture() != null)
                serie.setPicture(mediaService.getMedia(serie.getAcf().getPicture()));
        }
        return serieRepository.save(serie);
    }
}
