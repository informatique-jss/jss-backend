package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Serie;

public interface SerieService {
        public Serie getSerie(Integer id);

        public List<Serie> getAvailableSeries();

        public Serie addOrUpdateSerie(Serie author);

        public Serie getSerieBySlug(String slug);
}
