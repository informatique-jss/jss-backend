package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.wordpress.model.NewspaperPage;
import com.jss.osiris.modules.myjss.wordpress.repository.NewspaperPageRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class NewspaperPageServiceImpl implements NewspaperPageService {

    @Autowired
    NewspaperPageRepository newspaperPageRepository;

    @Autowired
    NewspaperService newspaperService;

    @Autowired
    SearchService searchService;

    @PersistenceContext
    private EntityManager entityManager;

    public NewspaperPage addOrUpdateNewspaperPage(NewspaperPage newspaperPage) {
        return newspaperPageRepository.save(newspaperPage);
    }

    public List<NewspaperPage> getNewspaperPages() {
        return IterableUtils.toList(newspaperPageRepository.findAll());
    }

    @Override
    public List<IndexEntity> searchNewspapersEntities(String searchText, String sortBy) {
        if (searchText != null && searchText.trim().length() > 0)
            return searchService.searchForEntities(searchText,
                    NewspaperPage.class.getSimpleName(), false, sortBy, true);

        return null;
    }
}
