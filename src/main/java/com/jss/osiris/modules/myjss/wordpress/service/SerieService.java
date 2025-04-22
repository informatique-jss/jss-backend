package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.modules.myjss.wordpress.model.Serie;

public interface SerieService {

        public Serie getSerie(Integer id);

        public List<Serie> getAvailableSeries();

        public Page<Serie> getSeries(Pageable pageableRequest);

        public Serie addOrUpdateSerie(Serie author);

        public Serie getSerieBySlug(String slug);
}
